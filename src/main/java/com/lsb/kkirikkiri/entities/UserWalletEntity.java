package com.lsb.kkirikkiri.entities;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserWalletEntity {
    private int id;
    private String userEmail;
    private int cash;
    private LocalDateTime lastCharge;
    private LocalDateTime lastPay;
    private String customerKey;

    public UserWalletEntity(String userEmail, int cash, LocalDateTime lastCharge, LocalDateTime lastPay, String customerKey) {
        this.userEmail = userEmail;
        this.cash = cash;
        this.lastCharge = lastCharge;
        this.lastPay = lastPay;
        this.customerKey = customerKey;
    }
}
