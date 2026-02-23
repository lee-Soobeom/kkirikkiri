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

    public MessageEntity(String sender, String receiver, String content, LocalDateTime timestamp, Boolean isChecked, Integer articleId, String usage) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
        IsChecked = isChecked;
        this.articleId = articleId;
        this.usage = usage;
    }
}
