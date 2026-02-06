package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.user.EmailTokenEntity;
import com.lsb.kkirikkiri.entities.user.StoreEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.results.VerifyEmailResult;
import com.lsb.kkirikkiri.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController extends AbstractGeneralController{

    private final UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/user/";
        }

        return "user/login";
    }

    @RequestMapping(value = "/login")
    @ResponseBody
    public Map<String, Object> postLogin(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            HttpSession session) {

        Pair<Result, UserEntity> result = this.userService.login(email, password);
        if (result.getLeft() == CommonResult.SUCCESS) {
            session.setAttribute("sessionUser", result.getRight());
        }
        return prepareJsonResponse(result.getLeft());

    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogout(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser, HttpSession session) {
        if (sessionUser != null) {
            session.invalidate();
        }
        return "redirect:/user/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/user/";
        }

        return "user/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postUser(@RequestParam(value = "termMarketingAgreed", required = false) boolean termMarketingAgreed, UserEntity user, StoreEntity store, EmailTokenEntity emailToken, @RequestParam(value = "businessLicense", required = false) MultipartFile businessLicense, @RequestParam(value = "reportCardUrl", required = false) MultipartFile reportCardUrl) {
        Result result;
        try {
            result = this.userService.register(user, store, emailToken, termMarketingAgreed, businessLicense, reportCardUrl);
        } catch (TransactionalException e) {
            result = (Result) e.result;
        }
        return prepareJsonResponse(result);
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postEmail(@RequestParam(value = "email", required = false) String email) throws MessagingException {
        Pair<Result, EmailTokenEntity> result = this.userService.sendEmail(email);
        Map<String, Object> response = prepareJsonResponse(result.getLeft());
        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("salt", result.getRight().getSalt());
        }
        return response;
    }

    @RequestMapping(value = "/session-status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getSessionStatus(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();
        if (sessionUser == null) {
            response.put("result", false);
        } else {
            response.put("result", true);
            response.put("email", sessionUser.getEmail());
        }
        return response;
    }

    @RequestMapping(value = "/nickname-status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getNicknameStatus(@RequestParam(value = "nickname", required = false) String nickname) {
        Result result = this.userService.checkNickname(nickname);
        return prepareJsonResponse(result);
    }

    @RequestMapping(value = "/email", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> patchEmail(EmailTokenEntity emailToken) {
        Result result = this.userService.verifyEmail(emailToken);
        Map<String, Object> response = new HashMap<>(prepareJsonResponse(result));
        if (result == CommonResult.SUCCESS) {
            response.put("salt", emailToken.getSalt());
            System.out.println("서버가 보내는 솔트" + emailToken.getSalt());
        }
        return response;
    }

}
