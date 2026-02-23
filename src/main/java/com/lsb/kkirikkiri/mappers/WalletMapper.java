package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.GroupWalletEntity;
import com.lsb.kkirikkiri.entities.UserWalletEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WalletMapper {
    int insertUserWallet(@Param(value = "wallet") UserWalletEntity wallet);

    int insertGroupWallet(@Param(value = "wallet") GroupWalletEntity wallet);

    UserWalletEntity selectUserWalletByEmail(@Param(value = "email") String email);

    UserWalletEntity selectUserWalletByCustomerKey(@Param(value = "customerKey") String customerKey);

    GroupWalletEntity selectGroupWalletByArticleId(@Param(value = "articleId") int articleId);

    int modifyUserWallet(@Param(value = "wallet") UserWalletEntity wallet);

    int modifyGroupWallet(@Param(value = "wallet") GroupWalletEntity wallet);
}
