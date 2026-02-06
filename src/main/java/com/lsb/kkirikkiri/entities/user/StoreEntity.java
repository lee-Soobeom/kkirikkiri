package com.lsb.kkirikkiri.entities.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class StoreEntity {
    private String email;
    private String businessNumber;
    private String storeName;
    private String businessType;
    private String addressPrimary;
    private String addressSecondary;
    private String storeContact;
    private String operatingHours;
    private String licenseUrl;
    private String reportCardUrl;
    private LocalDateTime termPolicyAt;
    private LocalDateTime termPrivacyAt;
    private LocalDateTime termThirdParty;
    private LocalDateTime termBusinessVerifyAt;
    private LocalDateTime termDocSubmissionAt;
    private LocalDateTime termMarketingAt;
    private String approvalStatus;
    private String rejectReason;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    private UserEntity user;
}
