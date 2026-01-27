package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/user/";
        }

        return "user/login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/user/";
        }

        return "user/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody // 리턴하는 문자열이 '파일명'이 아니라 '데이터'임을 알려줍니다.
    public String postRegister(HttpServletRequest request) {
        // 가입 처리 로직 (생략)

        // JS의 fetch().then()에서 받을 JSON 데이터
        return "{\"result\": \"SUCCESS\"}";
    }

}
