package com.lsb.kkirikkiri.entities;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WSMessage {
    private String sender;
    private String content;
}
