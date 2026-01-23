package com.lsb.kkirikkiri.entities;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String contactFirst;
    private String contactSecond;
    private String contactThird;
    private String myPlace;
    private String latitude;
    private String longitude;
    private String walletBalance;
    private String reviewTotal;
    private String reviewCount;
    private String reviewAvg;
    private String status;
    private String role;
    private LocalDate termPolicyAt;
    private LocalDate termPrivacyAt;
    private LocalDate termMarketingAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}