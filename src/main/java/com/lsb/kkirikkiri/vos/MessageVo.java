package com.lsb.kkirikkiri.vos;

import com.lsb.kkirikkiri.entities.MessageEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MessageVo extends MessageEntity {
    private String senderNickname;
    private String receiverNickname;

    public MessageVo(String sender, String receiver, String content, LocalDateTime timestamp, Boolean isChecked, Integer articleId, String usage) {
        super(sender, receiver, content, timestamp, isChecked, articleId, usage);
    }
}
