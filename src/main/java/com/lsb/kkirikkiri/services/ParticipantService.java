package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ParticipantEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.ParticipantMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.vos.MessageVo;
import com.lsb.kkirikkiri.vos.ParticipantVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final UserMapper userMapper;
    private final ParticipantMapper participantMapper;

    public CommonResult createParticipants(int articleId, String leader, String leaderNickname) {
        ParticipantEntity participant = new ParticipantEntity(articleId, leader, String.join(",", new String[]{"", "", "", ""}), String.join(",", new String[]{leaderNickname, "", "", "", ""}), 1);
        return this.participantMapper.insert(participant) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public CommonResult modifyParticipants(UserEntity sessionUser, int articleId, String participant, String participantNickname) {
        if (sessionUser == null) {
            return CommonResult.FAILURE;
        }
        ParticipantEntity dbParticipantEntity = this.participantMapper.selectById(articleId);
        if (dbParticipantEntity == null) {
            return CommonResult.FAILURE;
        }
        String[] participants = dbParticipantEntity.getParticipants().split(",", -1);
        // participants 참가자 배열에 순서대로 추가 로직
        int count = 0;
        for (int i = 0; i < participants.length; i++) {
            if (!participants[i].isEmpty()) {
                count++;
            }
            break;
        }
        if (count < participants.length) {
            participants[count] = participant;
            count++;
        }
        dbParticipantEntity.setParticipants(String.join(",", participants));
        String[] dbParticipantsNickname = dbParticipantEntity.getParticipantsNickname().split(",", -1);
        dbParticipantsNickname[count] = participantNickname;
        dbParticipantEntity.setParticipantsNickname(String.join(",", dbParticipantsNickname));
        dbParticipantEntity.setCount(count + 1);
        return this.participantMapper.update(dbParticipantEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ParticipantVo getParticipantByArticleId(int articleId) {
        if (articleId < 0) {
            return null;
        }
        ParticipantEntity dbParticipantEntity = this.participantMapper.selectById(articleId);
        if (dbParticipantEntity == null) {
            return null;
        }
        String[] dbParticipantsNickname = dbParticipantEntity.getParticipantsNickname().split(",", -1);
        ParticipantVo participantVo = new ParticipantVo();
        participantVo.setId(dbParticipantEntity.getId());
        participantVo.setArticleId(dbParticipantEntity.getArticleId());
        participantVo.setLeader(dbParticipantEntity.getLeader());
        participantVo.setParticipants(dbParticipantEntity.getParticipants());
        participantVo.setParticipantsNickname(dbParticipantEntity.getParticipantsNickname());
        participantVo.setCount(dbParticipantEntity.getCount());
        participantVo.setLeaderNickname(dbParticipantsNickname[0]);
        participantVo.setFirstUserNickname(dbParticipantsNickname[1]);
        participantVo.setSecondUserNickname(dbParticipantsNickname[2]);
        participantVo.setThirdUserNickname(dbParticipantsNickname[3]);
        participantVo.setFourthUserNickname(dbParticipantsNickname[4]);
        return participantVo;
    }
}
