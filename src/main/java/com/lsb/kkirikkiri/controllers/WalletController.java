package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.UserWalletEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getWallet(@SessionAttribute(value = "sessionUser") UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("user_email", sessionUser.getEmail());
        response.put("user_name", sessionUser.getName());
        UserWalletEntity dbWallet = this.walletService.getUserWallet(sessionUser.getEmail());
        response.put("wallet", dbWallet);
        return response;
    }

    @RequestMapping(value = "/pay", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> putPay(@SessionAttribute(value = "sessionUser") UserEntity sessionUser,
                                      @RequestParam(value = "articleId", required = false, defaultValue = "0") int articleId) {
        Map<String, Object> response = new HashMap<>();
        Result result = this.walletService.putWallet(sessionUser, articleId);
        response.put("result", result.name());
        return response;
    }
}