package com.lsb.kkirikkiri.entities;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
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
    private String participantsNickname;
    private int count;

    public ParticipantEntity(int articleId, String leader, String participants, String participantsNickname, int count) {
        this.articleId = articleId;
        this.leader = leader;
        this.participants = participants;
        this.participantsNickname = participantsNickname;
        this.count = count;
    }
}
