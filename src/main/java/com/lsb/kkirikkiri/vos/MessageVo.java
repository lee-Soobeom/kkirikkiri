package com.lsb.kkirikkiri.vos;

import com.lsb.kkirikkiri.entities.MessageEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class MessageVo extends MessageEntity {
    private String senderNickname;
    private String receiverNickname;
}
