package com.lsb.kkirikkiri.vos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CancelVo {
    private String cancelReason;
    private String canceledAt;
    private Long cancelAmount;
    private String cancelStatus;
    private String transactionKey;
}
