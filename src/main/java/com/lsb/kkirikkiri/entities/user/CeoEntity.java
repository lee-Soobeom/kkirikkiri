package com.lsb.kkirikkiri.entities.user;

import java.time.LocalDateTime;

public class CeoEntity {
    private String email;
    private String businessNumber;
    private String storeName;
    private String businessType;
    private String storeAddress;
    private String storeContact;
    private String operatingHours;
    private String licenseUrl;
    private String reportCardUrl;
    private String approvalStatus;
    private String rejectReason;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private UserEntity user;
}
