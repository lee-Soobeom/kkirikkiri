package com.lsb.kkirikkiri.entities.user;

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
    private String addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}