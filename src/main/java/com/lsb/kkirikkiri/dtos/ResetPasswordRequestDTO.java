package com.lsb.kkirikkiri.dtos;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String email;
    private String password;
    private String token;
    private String authCode;
}
