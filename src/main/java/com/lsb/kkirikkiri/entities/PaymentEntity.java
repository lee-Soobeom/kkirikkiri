package com.lsb.kkirikkiri.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class PaymentEntity {
    private int id;
    private String orderId;
    private String customerKey;
    private String userEmail;
    private String userName;
    private int amount;
    private String status;
    private String paymentKey;
}
