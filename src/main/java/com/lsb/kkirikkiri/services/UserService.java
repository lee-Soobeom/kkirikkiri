package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.user.EmailTokenEntity;
import com.lsb.kkirikkiri.entities.user.StoreEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.mappers.EmailTokenMapper;
import com.lsb.kkirikkiri.mappers.StoreMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.mappers.WalletMapper;
import com.lsb.kkirikkiri.results.*;
import com.lsb.kkirikkiri.validators.EmailTokenValidator;
import com.lsb.kkirikkiri.validators.StoreValidate;
import com.lsb.kkirikkiri.validators.UserValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WalletService walletService;
    private final EmailTokenMapper emailTokenMapper;
    private final StoreMapper storeMapper;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    // 사장님 회원 가입 시, 제출하는 사진 두 장 (사업자 등록증 사본, 영업 신고증 사본) 저장을 위한 것
    private String saveFile(MultipartFile file) {
        try {
            String uploadPath = "C:/kkirikkiri/uploads/";
            File folder = new File(uploadPath);
            if (!folder.exists()) {
                folder.mkdirs(); // 폴더가 없으면 생성
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            File targetFile = new File(uploadPath + fileName);
            file.transferTo(targetFile);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    public Pair<Result, UserEntity> login(String email, String password) {
        if (!UserValidator.validateEmail(email) ||
                !UserValidator.validatePassword(password)) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        if (!BCrypt.checkpw(password, dbUser.getPassword())) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        return Pair.of(CommonResult.SUCCESS, dbUser);
    }

    @Transactional
    public Result register(UserEntity user, StoreEntity store, EmailTokenEntity emailToken, boolean termMarketingAgreed, MultipartFile businessLicense, MultipartFile reportCardUrl) {
        if (user == null ||
                !UserValidator.validateEmail(user) ||
                !UserValidator.validatePassword(user) ||
                !UserValidator.validateNickname(user) ||
                !UserValidator.validateName(user) ||
                !UserValidator.validateBirth(user) ||
                !UserValidator.validateTelecom(user) ||
                !UserValidator.validateContact(user) ||
                !UserValidator.validateAddressPrimary(user)) {
            return CommonResult.FAILURE;
        }

        if (user.isBoss()) {
            if (store == null ||
                    !StoreValidate.validateBusinessNumber(store) ||
                    !StoreValidate.validateStoreName(store) ||
                    !StoreValidate.validateBusinessType(store) ||
                    !StoreValidate.validateStoreContact(store) ||
                    !StoreValidate.validateOperatingHours(store) ||
                    !StoreValidate.validateLicenseUrl(store) ||
                    !StoreValidate.validateReportCardUrl(store)) {
                return CommonResult.FAILURE;
            }
            if (businessLicense != null && !businessLicense.isEmpty()) {
                String licensePath = saveFile(businessLicense);
                store.setLicenseUrl(licensePath);
            }
            if (reportCardUrl != null && !reportCardUrl.isEmpty()) {
                String reportPath = saveFile(reportCardUrl);
                store.setReportCardUrl(reportPath);
            }
            this.storeMapper.insert(store);
        }

        if (emailToken == null ||
                !EmailTokenValidator.validateEmail(emailToken) ||
                !EmailTokenValidator.validateCode(emailToken) ||
                !EmailTokenValidator.validateSalt(emailToken)) {
            return CommonResult.FAILURE;
        }

        EmailTokenEntity dbEmailToken = this.emailTokenMapper.select(emailToken.getEmail(), emailToken.getCode(), emailToken.getSalt());

        if (dbEmailToken == null ||
                !dbEmailToken.isVerified() ||
                dbEmailToken.isUsed()) {
            return CommonResult.FAILURE;
        }

        dbEmailToken.setUsed(true);
        if (this.emailTokenMapper.update(dbEmailToken) < 1) {
            return CommonResult.FAILURE;
        }

        if (this.userMapper.selectByEmail(user.getEmail()) != null) {
            throw new TransactionalException(RegisterResult.FAILURE_DUPLICATE_EMAIL);
        }
        if (this.userMapper.selectByNickname(user.getNickname()) != null) {
            throw new TransactionalException(RegisterResult.FAILURE_DUPLICATE_NICKNAME);
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        String hashedPassword = encoder.encode(rawPassword);
        user.setPassword(hashedPassword);

        if (termMarketingAgreed) {
            user.setTermMarketingAt(LocalDateTime.now());
        }
        user.setTermPolicyAt(LocalDateTime.now());
        user.setTermPrivacyAt(LocalDateTime.now());
        user.setTermLocationAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        if (this.userMapper.insert(user) < 1) {
            throw new TransactionalException(CommonResult.FAILURE);
        }
        if (user.isBoss()) {
            store.setEmail(user.getEmail());
            store.setTermPolicyAt(LocalDateTime.now());
            store.setTermPrivacyAt(LocalDateTime.now());
            store.setTermBusinessVerifyAt(LocalDateTime.now());
            store.setTermThirdParty(LocalDateTime.now());
            store.setTermDocSubmissionAt(LocalDateTime.now());

            if (this.storeMapper.insert(store) < 1) {
                throw new TransactionalException(CommonResult.FAILURE);
            }
        }
        if (this.walletService.createWallet(user.getEmail()).equals(CommonResult.FAILURE)) {
            throw new TransactionalException(CommonResult.FAILURE);
        }
        return CommonResult.SUCCESS;
    }

    public Pair<Result, EmailTokenEntity> sendEmail(String email) throws MessagingException {
        if (!UserValidator.validateEmail(email)) {
            return Pair.of(CommonResult.FAILURE, null);
        }

        String code = RandomStringUtils.randomNumeric(6);
        String salt = new BCryptPasswordEncoder().encode(String.format("%s%s%f%f", email, code, Math.random(), Math.random()));
        EmailTokenEntity emailToken = new EmailTokenEntity();
        emailToken.setEmail(email);
        emailToken.setCode(code);
        emailToken.setSalt(salt);
        emailToken.setVerified(false);
        emailToken.setUsed(false);
        emailToken.setCreatedAt(LocalDateTime.now());
        emailToken.setExpiresAt(LocalDateTime.now().plusMinutes(10L));

        if (this.emailTokenMapper.insert(emailToken) < 1) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        Context context = new Context();
        context.setVariable("code", code);
        String body = this.templateEngine.process("user/sendEmail", context);

        MimeMessage message = this.mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        messageHelper.setFrom("likesuryou@gmail.com");
        messageHelper.setTo(email);
        messageHelper.setSubject("[끼리끼리] 회원가입 인증 번호 안내");
        messageHelper.setText(body, true);

        this.mailSender.send(message);

        return Pair.of(CommonResult.SUCCESS, emailToken);
    }

    public Result verifyEmail(EmailTokenEntity emailToken) {
        if (emailToken == null ||
                !EmailTokenValidator.validateEmail(emailToken) ||
                !EmailTokenValidator.validateCode(emailToken) ||
                !EmailTokenValidator.validateSalt(emailToken)) {
            return CommonResult.FAILURE;
        }
        EmailTokenEntity dbEmailToken = this.emailTokenMapper.select(emailToken.getEmail(), emailToken.getCode(), emailToken.getSalt());
        if (dbEmailToken == null ||
                dbEmailToken.isVerified() ||
                dbEmailToken.isUsed()) {
            return VerifyEmailResult.FAILURE_EXPIRED;
        }
        dbEmailToken.setVerified(true);
        return this.emailTokenMapper.update(dbEmailToken) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Result checkNickname(String nickname) {
        if (!UserValidator.validateNickname(nickname)) {
            return CommonResult.FAILURE;
        }
        if (nickname == null ||
                nickname.isEmpty() ||
                nickname.length() > 15 ||
                !nickname.matches("^[\\da-zA-Z가-힣]{1,15}$")) {
            return CommonResult.FAILURE;
        }
        UserEntity dbUser = this.userMapper.selectByNickname(nickname);
        return dbUser == null
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
