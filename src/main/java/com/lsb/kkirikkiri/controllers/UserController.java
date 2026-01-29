package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.LoginResult;
import com.lsb.kkirikkiri.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

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

        Map<String, Object> response = new HashMap<>();

        // 1. 임시 테스트용 계정 설정 (회원가입 대신 직접 비교)
        // 나중에는 이 부분을 userService.login(email, password)로 바꾸면 됩니다.
        if ("admin@sample.com".equals(email) && "123456789".equals(password)) {

            // 2. 세션에 더미 유저 정보 저장 (메인 페이지에서 쓰기 위함)
            // UserEntity가 있다면 객체를 만들어서 담아주세요.
            session.setAttribute("userEmail", email);

            response.put("result", "SUCCESS");
        } else {
            response.put("result", "FAILURE");
        }

        return response;
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
