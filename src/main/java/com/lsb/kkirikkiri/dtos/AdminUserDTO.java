package com.lsb.kkirikkiri.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminUserDTO {
    private String socialTypeCode;
    private boolean isAdmin;
    private boolean isBoss;
    private String email;
    private String contact;
    private String name;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private String status;
    private boolean isAdult;
}
