package com.lsb.kkirikkiri.vos;

import com.lsb.kkirikkiri.entities.PaymentEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class PaymentVo extends PaymentEntity {
    private String mId;
    private List<CancelVo> cancels;
    private Long totalAmount;
    private Long balanceAmount;
}
