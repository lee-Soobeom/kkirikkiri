package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.BoardEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BoardValidator {
    public static final String ID_REGEX = "^([\\da-zA-Z\\-_]{1,25})$";

    public static boolean validateId(String id) {
        return id != null && id.matches(ID_REGEX);
    }

    public static boolean validateId(@NonNull BoardEntity board) {
        return validateId(board.getId());
    }
}
