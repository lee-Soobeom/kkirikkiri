package com.lsb.kkirikkiri.entities.user;

import lombok.*;

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
    private String contact;
    private String addressPrimary;
    private String addressSecondary;
    private boolean isAdmin;
    private boolean isBoss;
    private String storeEmail;
    private int myPoint;
    private String role;
    private String status;
    private int reviewTotal;
    private int reviewCount;
    private double reviewAvg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String socialTypeCode;
    private String socialId;
}