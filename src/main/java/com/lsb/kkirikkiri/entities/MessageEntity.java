package com.lsb.kkirikkiri.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
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
    private Integer articleId;
    private String usage;
}
