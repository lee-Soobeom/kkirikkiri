package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.user.EmailTokenEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmailTokenValidator {
    public static final String EMAIL_REGEX = UserValidator.EMAIL_REGEX;
    public static final String CODE_REGEX = "^\\d{6}$";
    public static final String SALT_REGEX = "^\\$(2[abxy])\\$(0[4-9]|[1-2][0-9]|3[0-1])\\$([\\da-zA-Z./]{53})$";

    public static boolean validateEmail(@NonNull EmailTokenEntity emailToken) {
        return validateEmail(emailToken.getEmail());
    }

    public static boolean validateEmail(String email) {
        return UserValidator.validateEmail(email);
    }

    public static boolean validateCode(@NonNull EmailTokenEntity emailToken) {
        return validateCode(emailToken.getCode());
    }

    public static boolean validateCode(String code) {
        return code != null &&
                ValidatorUtils.isLengthInBetween(code, 6, 6) &&
                code.trim().matches(CODE_REGEX);
    }

    public static boolean validateSalt(@NonNull EmailTokenEntity emailToken) {
        return validateSalt(emailToken.getSalt());
    }

    public static boolean validateSalt(String salt) {
        return salt != null &&
                ValidatorUtils.isLengthInBetween(salt, 60, 128) &&
                salt.matches(SALT_REGEX);
    }
}
