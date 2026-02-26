package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.dtos.AdminStoreDTO;
import com.lsb.kkirikkiri.dtos.AdminUserDTO;
import com.lsb.kkirikkiri.entities.UserWalletEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.services.AdminService;
import com.lsb.kkirikkiri.services.WalletService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final WalletService walletService;
    private final AdminService adminService;

    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(HttpSession session, @RequestParam(value = "menu", required = false, defaultValue = "dashboard") String menu) {
        UserEntity user = (UserEntity) session.getAttribute("sessionUser");
        if (user == null || (!"ADMIN".equals(user.getStatus()) && !user.isAdmin())) {
            return new ModelAndView("redirect:/user/login");
        }
        ModelAndView mv = new ModelAndView("admin/index");
        mv.addObject("currentMenu", menu);

        if ("approval".equals(menu)) {
            List<AdminStoreDTO> bossList = this.adminService.getPendingBosses();
            mv.addObject("bossList", bossList);
        }
        if ("user".equals(menu)) {
            List<AdminUserDTO> userList = this.adminService.getUserList();
            mv.addObject("userList", userList);
            mv.addObject("totalUserCount", this.adminService.getTotalUserCount());
            mv.addObject("todayJoinCount", this.adminService.getTodayJoinCount());
        }

        mv.addObject("wallet", new UserWalletEntity());

        return mv;
    }

    @RequestMapping(value = "/approve", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> approveBoss(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String status = payload.get("status");

        Map<String, Object> response = new HashMap<>();

        boolean isSuccess = adminService.processApproval(email, status);

        if (isSuccess) {
            response.put("result", "success");
        } else {
            response.put("result", "error");
            response.put("message", "상태 업데이트 중 오류가 발생했습니다.");
        }

        return response;

    }
}