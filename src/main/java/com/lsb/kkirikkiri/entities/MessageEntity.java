package com.lsb.kkirikkiri.entities;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class MessageEntity {
    private int id;
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;
    private Boolean IsChecked;
}
