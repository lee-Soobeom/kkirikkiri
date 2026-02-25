package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.user.StoreEntity;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

public class StoreValidate {
    public static final String ADDRESS_PRIMARY_REGEX = "^.{1,200}$";
    public static final String BUSINESS_NUMBER_REGEX = "^\\d{10}$";
    public static final String STORE_NAME_REGEX = "^.{1,50}$";
    public static final String BUSINESS_TYPE_REGEX = "^(치킨|피자|햄버거|찜/탕|일식|중식|양식|디저트/카페)$";
    public static final String STORE_CONTACT_REGEX = "^\\d{8,11}$";
    public static final String OPERATING_HOURS_REGEX = "^.{1,100}$";
    public static final String LICENSE_URL_REGEX = "^.{1,255}$";
    public static final String REPORT_CARD_URL_REGEX = "^.{1,255}$";
    public static final String APPROVAL_STATUS_REGEX = "^(PENDING|APPROVED|REJECTED)$";
    public static final String REJECT_REASON_REGEX = "^.{0,255}$";



    public static boolean validateBusinessNumber(@NonNull StoreEntity store) {
        return validateBusinessNumber(store.getBusinessNumber());
    }

    public static boolean validateBusinessNumber(String businessNumber) {
        return businessNumber != null &&
                businessNumber.matches(BUSINESS_NUMBER_REGEX);
    }

    public static boolean validateStoreName(@NonNull StoreEntity store) {
        return validateStoreName(store.getStoreName());
    }

    public static boolean validateStoreName(String storeName) {
        return storeName != null &&
                ValidatorUtils.isLengthInBetween(storeName, 1, 50) &&
                storeName.matches(STORE_NAME_REGEX);
    }

    public static boolean validateBusinessType(@NonNull StoreEntity store) {
        return validateBusinessType(store.getBusinessType());
    }

    public static boolean validateBusinessType(String businessType) {
        return businessType != null &&
                ValidatorUtils.isLengthInBetween(businessType, 2, 20) &&
                businessType.matches(BUSINESS_TYPE_REGEX);
    }

    public static boolean validateStoreContact(@NonNull StoreEntity store) {
        return validateStoreContact(store.getStoreContact());
    }

    public static boolean validateStoreContact(String storeContact) {
        return storeContact != null &&
                storeContact.matches(STORE_CONTACT_REGEX);
    }

    public static boolean validateOperatingHours(@NonNull StoreEntity store) {
        return validateOperatingHours(store.getOperatingHours());
    }

    public static boolean validateOperatingHours(String operatingHours) {
        return operatingHours != null &&
                ValidatorUtils.isLengthInBetween(operatingHours, 1, 100) &&
                operatingHours.matches(OPERATING_HOURS_REGEX);
    }

    public static boolean validateLicenseUrl(@NonNull StoreEntity store) {
        return validateLicenseUrl(store.getLicenseUrl());
    }

    public static boolean validateLicenseUrl(String url) {
        return url != null &&
                ValidatorUtils.isLengthInBetween(url, 1, 255) &&
                url.matches(LICENSE_URL_REGEX);
    }

    public static boolean validateReportCardUrl(@NonNull StoreEntity store) {
        return validateReportCardUrl(store.getReportCardUrl());
    }

    public static boolean validateReportCardUrl(String url) {
        return url != null &&
                ValidatorUtils.isLengthInBetween(url, 1, 255) &&
                url.matches(REPORT_CARD_URL_REGEX);
    }

    public static boolean validateApprovalStatus(@NonNull StoreEntity store) {
        return validateApprovalStatus(store.getApprovalStatus());
    }

    public static boolean validateApprovalStatus(String status) {
        return status != null &&
                status.matches(APPROVAL_STATUS_REGEX);
    }

    public static boolean validateRejectReason(@NonNull StoreEntity store) {
        return validateRejectReason(store.getRejectReason());
    }

    public static boolean validateRejectReason(String reason) {
        // 반려 사유는 없을 수 있으므로 0~255자 허용
        return reason == null || (
                ValidatorUtils.isLengthInBetween(reason, 0, 255) &&
                        reason.matches(REJECT_REASON_REGEX));
    }
}
