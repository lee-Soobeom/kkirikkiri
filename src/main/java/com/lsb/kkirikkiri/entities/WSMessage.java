package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WSMessage {
    private String sender;
    private String content;
}
