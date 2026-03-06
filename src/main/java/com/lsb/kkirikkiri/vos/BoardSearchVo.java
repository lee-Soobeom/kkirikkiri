package com.lsb.kkirikkiri.vos;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BoardSearchVo {
    private String id;
    private String by;
    private String keyword;
    private String sort;
}
