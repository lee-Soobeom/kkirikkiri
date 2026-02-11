package com.lsb.kkirikkiri.entities;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class ParticipantEntity {
    private int id;
    private int articleId;
    private String leader;
    private String participants;
    private int count;

    public ParticipantEntity(int articleId, String leader, String participants, int count) {
        this.articleId = articleId;
        this.leader = leader;
        this.participants = participants;
        this.count = count;
    }
}
