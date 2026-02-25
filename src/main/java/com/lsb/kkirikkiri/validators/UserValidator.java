package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class UserValidator {
    private static final DateTimeFormatter birthFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PASSWORD_REGEX = "^[\\da-zA-Z`~!@#$%^&*()\\-_=+\\[{\\]}\\\\|;:'\",<.>\\/?]{6,50}$";
    public static final String NAME_REGEX = "^[가-힣]{2,5}$";
    public static final String NICKNAME_REGEX = "^[\\da-zA-Z가-힣]{1,10}$";
    public static final String TELECOM_REGEX = "^(SKT|KT|LGU|SKM|KTM|LGM)$";
    public static final String CONTACT_REGEX = "^\\d{8,11}$"; // 일반/가게 전화번호 통합
    public static final String ADDRESS_PRIMARY_REGEX = "^.{1,200}$";
    public static final String ADDRESS_SECONDARY_REGEX = "^.{0,100}$";

    public static boolean validateEmail(@NonNull UserEntity user) {
        return validateEmail(user.getEmail());
    }

    public static boolean validateEmail(String email) {
        return email != null &&
                ValidatorUtils.isLengthInBetween(email, 8, 50) &&
                email.trim().matches(EMAIL_REGEX);
    }

    public static boolean validatePassword(@NonNull UserEntity user) {
        return validatePassword(user.getPassword());
    }

    public static boolean validatePassword(String password) {
        return password != null &&
                ValidatorUtils.isLengthInBetween(password, 6, 50) &&
                password.matches(PASSWORD_REGEX);
    }

    public static boolean validateNickname(@NonNull UserEntity user) {
        return validateNickname(user.getNickname());
    }

    public static boolean validateNickname(String nickname) {
        return nickname != null &&
                ValidatorUtils.isLengthInBetween(nickname, 1, 10) &&
                nickname.matches(NICKNAME_REGEX);
    }

    public static boolean validateName(@NonNull UserEntity user) {
        return validateName(user.getName());
    }

    public static boolean validateName(String name) {
        return name != null &&
                ValidatorUtils.isLengthInBetween(name, 2, 5) &&
                name.matches(NAME_REGEX);
    }

    public static boolean validateBirth(@NonNull UserEntity user) {
        return validateBirth(user.getBirth());
    }

    public static boolean validateBirth(String birthStr) {
        try {
            LocalDate.parse(birthStr, birthFormatter);
            return true;
        } catch (DateTimeParseException ignored) {
            return false;
        }
    }

    public static boolean validateBirth(LocalDate birth) {
        return birth != null;
    }

    public static boolean validateTelecom(@NonNull UserEntity user) {
        return validateTelecom(user.getTelecom());
    }

    public static boolean validateTelecom(String telecom) {
        return telecom != null && telecom.matches(TELECOM_REGEX);
    }

    public static boolean validateContact(@NonNull UserEntity user) {
        return validateContact(user.getContact());
    }

    public static boolean validateContact(String contact) {
        return contact != null &&
                contact.matches(CONTACT_REGEX);
    }

    public static boolean validateAddressPrimary(@NonNull UserEntity user) {
        return validateAddressPrimary(user.getAddressPrimary());
    }

    public static boolean validateAddressPrimary(String address) {
        return address != null &&
                ValidatorUtils.isLengthInBetween(address, 1, 200) &&
                address.matches(ADDRESS_PRIMARY_REGEX);
    }

    public static boolean validateAddressSecondary(@NonNull UserEntity user) {
        return validateAddressSecondary(user.getAddressSecondary());
    }

    public static boolean validateAddressSecondary(String address) {
        return address != null && !address.isBlank() &&
                ValidatorUtils.isLengthInBetween(address, 1, 100) &&
                address.matches(ADDRESS_SECONDARY_REGEX);
    }

}
