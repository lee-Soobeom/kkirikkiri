package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.WalletEntity;
import com.lsb.kkirikkiri.mappers.WalletMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletMapper walletMapper;

    public CommonResult createWallet(String userEmail) {
        if (!UserValidator.validateEmail(userEmail)) {
            return CommonResult.FAILURE;
        }
        UUID uuid = UUID.randomUUID();
        return this.walletMapper.insert(new WalletEntity(userEmail, 0, null, uuid.toString())) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
