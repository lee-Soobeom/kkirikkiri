package com.lsb.kkirikkiri.vos;


import com.lsb.kkirikkiri.entities.ParticipantEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ParticipantVo extends ParticipantEntity {
    private String leaderNickname;
    private String firstUserNickname;
    private String secondUserNickname;
    private String thirdUserNickname;
    private String fourthUserNickname;
}
