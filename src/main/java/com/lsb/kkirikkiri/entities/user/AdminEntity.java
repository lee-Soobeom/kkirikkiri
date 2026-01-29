package com.lsb.kkirikkiri.entities.user;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AdminEntity {
    private String email;
    private String accessIp;
    private UserEntity user;
}
