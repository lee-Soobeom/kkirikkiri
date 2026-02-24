package com.lsb.kkirikkiri.interceptors;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class WalletInterceptor implements HandlerInterceptor {
    @Autowired
    private WalletService walletService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserEntity sessionUser = (UserEntity) session.getAttribute("sessionUser");
            if (sessionUser != null) {
                request.setAttribute("wallet", this.walletService.getUserWallet(sessionUser.getEmail()));
            }
        }
        return true;
    }
}
