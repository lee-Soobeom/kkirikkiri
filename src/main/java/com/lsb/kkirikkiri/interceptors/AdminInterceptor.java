package com.lsb.kkirikkiri.interceptors;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        UserEntity user = (UserEntity) session.getAttribute("sessionUser");

        if (user == null || !user.isAdmin()) {
            System.err.println("관리자 아님 이슈로 님은 튕기셨습니다");
            response.sendRedirect("/user/login");
            return false;
        }

        return true;
    }
}