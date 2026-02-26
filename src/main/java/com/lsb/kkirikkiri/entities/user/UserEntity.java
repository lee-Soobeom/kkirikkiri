package com.lsb.kkirikkiri.entities.user;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserEntity {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private LocalDate birth;
    private String telecom;
    private String contact;
    private String addressPrimary;
    private String addressSecondary;
    private String profileImagePath;
    private boolean isAdmin;
    private boolean isBoss;
    private String storeEmail;
    private int myPoint;
    private String role;
    private String status = "GENERAL";
    private int reviewTotal;
    private int reviewCount;
    private double reviewAvg;
    private LocalDateTime termPolicyAt;
    private LocalDateTime termPrivacyAt;
    private LocalDateTime termLocationAt;
    private LocalDateTime termMarketingAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String socialTypeCode;
    private String socialId;
    private LocalDateTime lastLoginAt;
}