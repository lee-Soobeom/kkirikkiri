package com.lsb.kkirikkiri.entities;

import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Service
@EqualsAndHashCode(of = "id")
public class WalletEntity {
    private int id;
    private String userEmail;
    private int cash;
    private LocalDateTime lastCharge;
    private String customerKey;

    public WalletEntity(String userEmail, int cash, LocalDateTime lastCharge, String customerKey) {
        this.userEmail = userEmail;
        this.cash = cash;
        this.lastCharge = lastCharge;
        this.customerKey = customerKey;
    }
}
