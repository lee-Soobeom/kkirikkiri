package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.PaymentEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidatorUtils {
    private static final String REGEX_UUID = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    static boolean isLengthInBetween(@NonNull String str, int min, int max) {
        return str.length() >= min && str.length() <= max;
    }

    public boolean validateUUID(String uuid) {
        return uuid != null
                && uuid.matches(REGEX_UUID)
                && uuid.length() == 36;
    }
}
