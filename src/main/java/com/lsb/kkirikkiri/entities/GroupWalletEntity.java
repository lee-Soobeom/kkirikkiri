package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class GroupWalletEntity {
    private int id;
    private int articleId;
    private int wallet;
    private String payedParticipants;

    public GroupWalletEntity(int articleId, int wallet, String payedParticipants) {
        this.articleId = articleId;
        this.wallet = wallet;
        this.payedParticipants = payedParticipants;
    }
}
