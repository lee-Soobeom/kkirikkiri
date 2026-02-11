package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.enums.MessageType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageValidator {
    private static final String CONTENT_REGEX = "^[\\s\\S]{1,300}$";

    public static boolean validateContent(@NonNull MessageEntity messageEntity) {
        return validateContent(messageEntity.getContent());
    }

    public static boolean validateContent(String content) {
        return content != null
                && content.matches(CONTENT_REGEX)
                && ValidatorUtils.isLengthInBetween(content, 1, 300);
    }

    public static boolean validateUsage(@NonNull MessageEntity messageEntity) {
        return validateUsage(messageEntity.getUsage());
    }

    public static boolean validateUsage(String usage) {
        if (usage == null) {
            return false;
        }
        boolean result = false;
        for (MessageType u : MessageType.values()) {
            if (usage.equals(u.code)) {
                result = true;
                break;
            }
        }
        return result;
    }
}
