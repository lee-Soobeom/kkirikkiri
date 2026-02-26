package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.dtos.AdminStoreDTO;
import com.lsb.kkirikkiri.dtos.AdminUserDTO;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;

    public List<AdminStoreDTO> getPendingBosses() {
        return adminMapper.selectPendingBosses();
    }

    @Transactional
    public boolean approveBoss(String email) {
        int storeResult = adminMapper.updateStoreStatus(email, "APPROVED");

        int userResult = adminMapper.updateUserStatus(email, "BOSS");

        return storeResult > 0 && userResult > 0;
    }

    public boolean changeUserStatus(String email, String newStatus) {
        return adminMapper.updateUserStatus(email, newStatus) > 0;
    }

    @Transactional
    public boolean processApproval(String email, String status) {
        int storeResult = adminMapper.updateStoreStatus(email, status);
        String userStatus = "APPROVED".equals(status) ? "BOSS" : "GENERAL";
        int userResult = adminMapper.updateUserStatus(email, userStatus);

        return storeResult > 0 && userResult > 0;
    }

    public List<AdminUserDTO> getUserList() {
        return adminMapper.selectAllUsers();
    }

    public int getTotalUserCount() {
        return adminMapper.countAllUsers();
    }

    public int getTodayJoinCount() {
        return adminMapper.countTodayJoin();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void processDormantUsers() {
        adminMapper.updateDormantUsers();
    }
}
