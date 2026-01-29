package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class SocialTypeEntity {
    private String code;
    private String text;
}
