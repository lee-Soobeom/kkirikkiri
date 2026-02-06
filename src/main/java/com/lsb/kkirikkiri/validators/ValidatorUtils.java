package com.lsb.kkirikkiri.validators;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatorUtils {
    static boolean isLengthInBetween(@NonNull String str, int min, int max) {
        return str.length() >= min && str.length() <= max;
    }
}
