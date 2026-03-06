package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.user.EmailTokenEntity;
import com.lsb.kkirikkiri.entities.user.StoreEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.mappers.AuthMapper;
import com.lsb.kkirikkiri.mappers.EmailTokenMapper;
import com.lsb.kkirikkiri.mappers.StoreMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.results.*;
import com.lsb.kkirikkiri.validators.EmailTokenValidator;
import com.lsb.kkirikkiri.validators.StoreValidate;
import com.lsb.kkirikkiri.validators.UserValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final WalletService walletService;
    private final EmailTokenMapper emailTokenMapper;
    private final StoreMapper storeMapper;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final HttpSession httpSession;
    private final AuthMapper authMapper;

    @Value("${custom.upload-path}")
    private String uploadPath;

    // 사장님 회원 가입 시, 제출하는 사진 두 장 (사업자 등록증 사본, 영업 신고증 사본) 저장을 위한 것
    private String saveFile(MultipartFile file) throws IOException {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = UUID.randomUUID().toString() + extension;

        File targetFile = new File(uploadPath, savedFileName);
        file.transferTo(targetFile);

        return "user/display?fileName=" + savedFileName;
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

        dbUser.setLastLoginAt(LocalDateTime.now());
        if ("DORMANT".equals(dbUser.getStatus())) {
            dbUser.setStatus("GENERAL");
        }
        this.userMapper.update(dbUser);

        return Pair.of(CommonResult.SUCCESS, dbUser);
    }

    @Transactional
    public Result modify(UserEntity user, StoreEntity store, MultipartFile profileImage, MultipartFile storeImage) {

        UserEntity existingUser = this.userMapper.selectByEmail(user.getEmail());
        if (existingUser == null) return CommonResult.FAILURE;

        if (user.getNickname() != null && !user.getNickname().isBlank()) {
            if (user.getNickname().length() < 2 || user.getNickname().length() > 10) {
                return CommonResult.FAILURE;
            }
            existingUser.setNickname(user.getNickname());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }

        if (user.getTelecom() != null && !user.getTelecom().isBlank()) {
            if (!UserValidator.validateTelecom(user)) {
                return CommonResult.FAILURE;
            }
            existingUser.setTelecom(user.getTelecom());
        }

        if (user.getContact() != null && !user.getContact().isBlank()) {
            existingUser.setContact(user.getContact());
        }

        if (user.getAddressPrimary() != null) existingUser.setAddressPrimary(user.getAddressPrimary());
        if (user.getAddressSecondary() != null) existingUser.setAddressSecondary(user.getAddressSecondary());

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                existingUser.setProfileImagePath(this.saveFile(profileImage));
            }
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패");
        }


        if (this.userMapper.update(existingUser) < 1) {
            return CommonResult.FAILURE;
        }


        if (existingUser.isBoss()) {
            StoreEntity existingStore = this.storeMapper.selectByEmail(existingUser.getEmail());

            if (existingStore != null) {
                try {
                    // 가게 이미지 처리
                    if (storeImage != null && !storeImage.isEmpty()) {
                        existingStore.setStoreImagePath(this.saveFile(storeImage));
                    }

                    if (store.getStoreName() != null && !store.getStoreName().isBlank()) {
                        if (!store.getStoreName().matches("^[a-zA-Z0-9가-힣\\s]{2,10}$")) return CommonResult.FAILURE;
                        existingStore.setStoreName(store.getStoreName());
                    }

                    if (store.getStoreContact() != null && !store.getStoreContact().isBlank()) {
                        if (!store.getStoreContact().matches("^0\\d{8,10}$")) return CommonResult.FAILURE;
                        existingStore.setStoreContact(store.getStoreContact());
                    }

                    if (store.getBusinessType() != null && !store.getBusinessType().isBlank()) {
                        if (!StoreValidate.validateBusinessType(store.getBusinessType())) return CommonResult.FAILURE;
                        existingStore.setBusinessType(store.getBusinessType());
                    }

                    if (store.getOperatingHours() != null && !store.getOperatingHours().isBlank()) {
                        existingStore.setOperatingHours(store.getOperatingHours());
                    }

                    if (store.getAddressPrimary() != null && !store.getAddressPrimary().isBlank()) {
                        existingStore.setAddressPrimary(store.getAddressPrimary());
                    }

                    // 가게 DB 업데이트 실행
                    if (this.storeMapper.update(existingStore) < 1) {
                        return CommonResult.FAILURE;
                    }
                } catch (IOException e) {
                    throw new RuntimeException("가게 이미지 저장 실패");
                }
            }
        }

        return CommonResult.SUCCESS;
    }

    public String findEmailByContact(String contact) {
        String cleanContact = contact.replace("-", "");
        return userMapper.selectEmailByContact(cleanContact);
    }

    public boolean resetPassword(String email, String token, String rawPassword) {
        if (authMapper.countValidToken(email, token) == 0) {
            return false;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(rawPassword);

        int updated = userMapper.updatePassword(email, encryptedPassword);
        if (updated > 0) {
            authMapper.updateAuthUsed(email, token);
            return true;
        }

        return false;
    }

    public boolean checkToken(String email, String token){
        return authMapper.countValidToken(email, token) > 0;
    }

    @Transactional
    public boolean sendAuthCode(String email) {
        if (userMapper.selectByEmail(email) == null) {
            return false;
        }

        String authCode = String.format("%06d", new Random().nextInt(1000000));
        authMapper.insertAuth(email, authCode);

        return sendEmail(email, authCode);
    }

    private boolean sendEmail(String email, String authCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom("likesuryou@gmail.com");
            helper.setTo(email);
            helper.setSubject("[끼리끼리] 비밀번호 재설정 인증번호입니다.");

            String htmlContent = String.format(
                    "<h3>비밀번호 재설정 인증번호</h3>" +
                            "<p>아래의 인증번호 6자리를 화면에 입력해 주세요.</p>" +
                            "<h2 style='color: #4e73df;'>%s</h2>" +
                            "<p>이 번호는 5분간만 유효합니다.</p>", authCode
            );

            helper.setText(htmlContent, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public String verifyCode(String email, String code) {
        if (authMapper.countValidToken(email, code) > 0) {
            authMapper.updateAuthUsed(email, code);

            String sessionToken = UUID.randomUUID().toString();
            authMapper.insertAuth(email, sessionToken);
            return sessionToken;
        }

        return null;
    }

    @Transactional
    public Result deleteUser(UserEntity sessionUser, String email) {
        if (sessionUser == null) {
            return DeleteUserResult.FAILURE_SESSION;
        }
        UserEntity dbUser = this.userMapper.selectByEmail(email);
        if (dbUser == null) {
            return CommonResult.FAILURE;
        }
        if (!sessionUser.isAdmin() && !dbUser.getEmail().equals(sessionUser.getEmail())) {
            return DeleteUserResult.FAILURE_SESSION;
        }
        return this.userMapper.deleteUserByEmail(sessionUser.getEmail()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public UserEntity getUserByEmail(String email) {
        return this.userMapper.selectByEmail(email);
    }

    @Transactional
    public Result register(UserEntity user, StoreEntity store, EmailTokenEntity emailToken, boolean isBoss, boolean termMarketingAgreed, MultipartFile licenseFile, MultipartFile reportCardFile) {
        try {

            if (user == null || !UserValidator.validateEmail(user) || !UserValidator.validatePassword(user) ||
                    !UserValidator.validateName(user) || !UserValidator.validateBirth(user) ||
                    !UserValidator.validateTelecom(user) || !UserValidator.validateContact(user) ||
                    !UserValidator.validateAddressPrimary(user)) {
                System.out.println("DEBUG: 유저 필수 정보 누락!");
                return CommonResult.FAILURE;
            }

            if (!user.isBoss()) {
                if (!UserValidator.validateNickname(user)) return CommonResult.FAILURE;
            } else {
                if (user.getNickname() == null ||
                        user.getNickname().isBlank()) {
                    user.setNickname(store.getStoreName());
                }
                if (store == null ||
                        !StoreValidate.validateBusinessNumber(store) || !StoreValidate.validateStoreName(store) ||
                        !StoreValidate.validateBusinessType(store) || !StoreValidate.validateStoreContact(store) ||
                        !StoreValidate.validateOperatingHours(store)) {
                    return CommonResult.FAILURE;
                }
            }

            if (emailToken == null || !EmailTokenValidator.validateEmail(emailToken) ||
                    !EmailTokenValidator.validateCode(emailToken) || !EmailTokenValidator.validateSalt(emailToken)) {
                return CommonResult.FAILURE;
            }

            EmailTokenEntity dbEmailToken = this.emailTokenMapper.select(emailToken.getEmail(), emailToken.getCode(), emailToken.getSalt());
            if (dbEmailToken == null || !dbEmailToken.isVerified() || dbEmailToken.isUsed()) {
                return CommonResult.FAILURE;
            }

            if (this.userMapper.selectByEmail(user.getEmail()) != null) {
                throw new TransactionalException(RegisterResult.FAILURE_DUPLICATE_EMAIL);
            }
            if (this.userMapper.selectByNickname(user.getNickname()) != null) {
                throw new TransactionalException(RegisterResult.FAILURE_DUPLICATE_NICKNAME);
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));

            LocalDateTime now = LocalDateTime.now();
            if (termMarketingAgreed) {
                user.setTermMarketingAt(now);
            }
            if (isBoss && user.isBoss()) {
                user.setStatus("PENDING_BOSS");
            } else {
                user.setStatus("GENERAL");
            }
            user.setTermPolicyAt(now);
            user.setTermPrivacyAt(now);
            user.setTermLocationAt(now);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            if (user.getSocialTypeCode() == null || user.getSocialTypeCode().isBlank()) {
                user.setSocialTypeCode("LOCAL");
            }

            if (this.userMapper.insert(user) < 1) {
                throw new TransactionalException(CommonResult.FAILURE);
            }

            // 가게 정보
            if (isBoss && user.isBoss()) {
                store.setEmail(user.getEmail());
                store.setTermPolicyAt(now);
                store.setTermPrivacyAt(now);
                store.setTermBusinessVerifyAt(now);
                store.setTermThirdPartyAt(now);
                store.setTermDocSubmissionAt(now);
                store.setApprovalStatus("PENDING");
                store.setAppliedAt(now);
                if (licenseFile != null && !licenseFile.isEmpty()) {
                    store.setLicenseUrl(saveFile(licenseFile));
                }
                if (reportCardFile != null && !reportCardFile.isEmpty()) {
                    store.setReportCardUrl(saveFile(reportCardFile));
                }

                if (this.storeMapper.insert(store) < 1) {
                    throw new TransactionalException(CommonResult.FAILURE);
                }
            }

            dbEmailToken.setUsed(true);
            this.emailTokenMapper.update(dbEmailToken);

            if (this.walletService.createUserWallet(user.getEmail()).equals(CommonResult.FAILURE)) {
                throw new TransactionalException(CommonResult.FAILURE);
            }

            return CommonResult.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();

            if (e instanceof TransactionalException) {
                return (Result) ((TransactionalException) e).result;
            }
            return CommonResult.FAILURE;
        }
    }

    @Transactional
    public void registerSocialExtraInfo(UserEntity currentUser, UserEntity updatedInfo) throws Exception {

        currentUser.setName(updatedInfo.getName());
        currentUser.setNickname(updatedInfo.getNickname());
        currentUser.setBirth(updatedInfo.getBirth());
        currentUser.setTelecom(updatedInfo.getTelecom());
        currentUser.setContact(updatedInfo.getContact());
        currentUser.setAddressPrimary(updatedInfo.getAddressPrimary());
        currentUser.setAddressSecondary(updatedInfo.getAddressSecondary());

        int result = userMapper.updateUserExtraInfo(currentUser);

        if (result <= 0) {
            throw new Exception("사용자 정보 업데이트 실패");
        }
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
        messageHelper.setTo(email);
        messageHelper.setFrom("likesuryou@gmail.com");
        messageHelper.setSubject("[끼리끼리] 회원가입 인증 번호 안내");
        messageHelper.setText(body, true);

        this.mailSender.send(message);

        return Pair.of(CommonResult.SUCCESS, emailToken);
    }


    public boolean verifyBusinessNumber(String businessNumber) {
        String url = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=58fa4c63802dc54a0163e2a572d52d67866d1afcacada18f3d1cb19687da8871";

        Map<String, Object> body = new HashMap<>();
        body.put("b_no", Collections.singletonList(businessNumber));

        RestTemplate restTemplate = new RestTemplate();
        try {
            Map response = restTemplate.postForObject(url, body, Map.class);
            List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");

            if (data != null && !data.isEmpty()) {
                String bSttCd = (String) data.get(0).get("b_stt_cd");

                // "01" = 계속사업자 (정상)
                // "02" = 휴업자
                // "03" = 폐업자
                return "01".equals(bSttCd);  // 계속사업자만 true
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public StoreEntity getStoreByEmail(String email) {
        return this.storeMapper.selectByEmail(email);
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
