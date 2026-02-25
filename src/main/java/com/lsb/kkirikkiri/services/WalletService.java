package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.GroupWalletEntity;
import com.lsb.kkirikkiri.entities.UserWalletEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.mappers.WalletMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.UserValidator;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.ParticipantVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final ParticipantService participantService;
    private final ArticleMapper articleMapper;
    private final WalletMapper walletMapper;
    private final UserMapper userMapper;

    public CommonResult createGroupWallet(int articleId) {
        GroupWalletEntity groupWalletEntity = new GroupWalletEntity(articleId, 0, String.join(",", new String[]{"", "", "", "", ""}));
        return this.walletMapper.insertGroupWallet(groupWalletEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public CommonResult createUserWallet(String userEmail) {
        if (!UserValidator.validateEmail(userEmail)) {
            return CommonResult.FAILURE;
        }
        UUID uuid = UUID.randomUUID();
        return this.walletMapper.insertUserWallet(new UserWalletEntity(userEmail, 0, null, null, uuid.toString())) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public UserWalletEntity getUserWallet(String userEmail) {
        return !UserValidator.validateEmail(userEmail)
                ? null
                : this.userMapper.selectByEmail(userEmail) == null
                ? null
                : this.walletMapper.selectUserWalletByEmail(userEmail);
    }

    @Transactional
    public CommonResult putWallet(UserEntity sessionUser, int articleId) {
        if (sessionUser == null ||
                this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return CommonResult.FAILURE_SESSION;
        }
        if (articleId <= 0) {
            return CommonResult.FAILURE;
        }
        ArticleVo dbArticleVo = this.articleMapper.selectById(articleId);
        if (dbArticleVo == null) {
            return CommonResult.FAILURE;
        }
        ParticipantVo dbParticipantVo = this.participantService.getParticipantByArticleId(articleId);
        if (dbParticipantVo == null) {
            return CommonResult.FAILURE;
        }
        UserWalletEntity dbUserWallet = this.walletMapper.selectUserWalletByEmail(sessionUser.getEmail());
        if (dbUserWallet == null) {
            return CommonResult.FAILURE;
        }
        GroupWalletEntity dbGroupWalletEntity = this.walletMapper.selectGroupWalletByArticleId(articleId);
        if (dbGroupWalletEntity == null) {
            return CommonResult.FAILURE;
        }
        int cost;
        if (dbArticleVo.getShareChecked()) {
            cost = dbArticleVo.getOrderPrice() / dbParticipantVo.getCount() + dbArticleVo.getDeliveryPrice() / dbParticipantVo.getCount();
        } else {
            cost = dbArticleVo.getOrderPrice() + dbArticleVo.getDeliveryPrice() / dbParticipantVo.getCount();
        }
        dbUserWallet.setCash(dbUserWallet.getCash() - cost);
        dbUserWallet.setLastPay(LocalDateTime.now());
        if (this.walletMapper.modifyUserWallet(dbUserWallet) < 0) {
            return CommonResult.FAILURE;
        }
        dbGroupWalletEntity.setWallet(dbGroupWalletEntity.getWallet() + cost);
        String[] payedParticipants = dbGroupWalletEntity.getPayedParticipants().split(",", -1);
        for (int i = 0; i < payedParticipants.length; i++) {
            if (payedParticipants[i].isEmpty()) {
                payedParticipants[i] = sessionUser.getEmail();
                break;
            }
        }
        dbGroupWalletEntity.setPayedParticipants(String.join(",", payedParticipants));
        if (this.walletMapper.modifyGroupWallet(dbGroupWalletEntity) < 0) {
            throw new TransactionalException(CommonResult.FAILURE);
        }
        return CommonResult.SUCCESS;
    }
}
