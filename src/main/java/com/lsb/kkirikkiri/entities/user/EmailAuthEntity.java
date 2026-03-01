package com.lsb.kkirikkiri.entities.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmailAuthEntity {
    private String email;
    private String token;
    private boolean isUsed;
    private LocalDateTime createdAt;
}
