package com.lsb.kkirikkiri.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private String storeContact;
    private String operatingHours;
    private String storeImagePath;
    private String licenseUrl;
    private String reportCardUrl;
    private LocalDateTime termPolicyAt;
    private LocalDateTime termPrivacyAt;
    private LocalDateTime termThirdPartyAt;
    private LocalDateTime termBusinessVerifyAt;
    private LocalDateTime termDocSubmissionAt;
    private LocalDateTime termMarketingAt;
    private String approvalStatus;
    private String rejectReason;
    private LocalDateTime appliedAt;
    private LocalDateTime approvedAt;
    @JsonIgnore
    private UserEntity user;
}
