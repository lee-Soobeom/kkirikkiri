package com.lsb.kkirikkiri.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminStoreDTO {
    private String email;
    private String ownerName;
    private String storeName;
    private String businessNumber;
    private String approvalStatus;
    private String licenseUrl;
    private String reportCardUrl;
    private LocalDateTime appliedAt;
}
