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

    public CommonResult modifyParticipants(UserEntity sessionUser, MessageVo messageVo) {
        if (sessionUser == null
                || this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return CommonResult.FAILURE;
        }
        ParticipantEntity dbParticipantEntity = this.participantMapper.selectById(messageVo.getArticleId());
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
            participants[count] = messageVo.getReceiver();
            count++;
        }
        dbParticipantEntity.setParticipants(String.join(",", participants));
        String[] dbParticipantsNickname = dbParticipantEntity.getParticipantsNickname().split(",", -1);
        dbParticipantsNickname[count] = messageVo.getReceiverNickname();
        dbParticipantEntity.setParticipantsNickname(String.join(",", dbParticipantsNickname));
        dbParticipantEntity.setCount(count);
        return this.participantMapper.update(dbParticipantEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ParticipantVo getParticipantByArticleId(int articleId) {
        if (articleId < 0) {
            return null;
        }
        ParticipantEntity dbParticipantEntity = this.participantMapper.selectById(articleId);
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
