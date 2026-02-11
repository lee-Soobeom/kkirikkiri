package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.entities.ParticipantEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.enums.MessageType;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.mappers.MessageMapper;
import com.lsb.kkirikkiri.mappers.ParticipantMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.MessageValidator;
import com.lsb.kkirikkiri.validators.UserValidator;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.MessageVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final ArticleMapper articleMapper;
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ParticipantMapper participantMapper;

    public Pair<CommonResult, MessageVo[]> getValidMessage(UserEntity sessionUser, LocalDateTime messageTimestamp) {
        if (sessionUser == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        if (this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        MessageVo[] dbMessages = messageTimestamp == null
                ? this.messageMapper.selectAllByEmail(sessionUser.getEmail())
                : this.messageMapper.selectByEmail(sessionUser.getEmail(), messageTimestamp);
        return Pair.of(CommonResult.SUCCESS, dbMessages);
    }

    public CommonResult writeMessage(MessageVo messageVo, UserEntity sessionUser) {
        // null & usage & session 공통 검사
        if (messageVo == null ||
                !MessageValidator.validateUsage(messageVo)) {
            return CommonResult.FAILURE;
        }
        if (sessionUser == null ||
                this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return CommonResult.FAILURE;
        }

        if (messageVo.getUsage().equals(MessageType.TALK.code)) {
            if (!MessageValidator.validateContent(messageVo)) {
                return CommonResult.FAILURE;
            }
            return CommonResult.FAILURE;
        } else if (messageVo.getUsage().equals(MessageType.CONFIRM.code)) {
            if (messageVo.getArticleId() < 0) {
                return CommonResult.FAILURE;
            }
            ArticleVo dbArticleVo = this.articleMapper.selectById(messageVo.getArticleId());
            if (dbArticleVo == null) {
                return CommonResult.FAILURE;
            }
            messageVo.setSender(sessionUser.getEmail());
            messageVo.setSenderNickname(sessionUser.getNickname());
            messageVo.setReceiver(dbArticleVo.getUserId());
            messageVo.setReceiverNickname(dbArticleVo.getNickname());
            messageVo.setContent(sessionUser.getNickname() + " 님이 참가 요청을 보냈습니다. 수락하시겠습니까?");
            messageVo.setTimestamp(LocalDateTime.now());
            messageVo.setIsChecked(false);
            messageVo.setArticleId(messageVo.getArticleId());
        } else if (messageVo.getUsage().equals(MessageType.ACCEPT.code)) {
            if (messageVo.getArticleId() < 0 ||
                    !UserValidator.validateEmail(messageVo.getSender()) ||
                    !UserValidator.validateNickname(messageVo.getSenderNickname()) ||
                    !UserValidator.validateEmail(messageVo.getReceiver()) ||
                    !UserValidator.validateNickname(messageVo.getReceiverNickname())) {
                return CommonResult.FAILURE;
            }
            messageVo.setContent(messageVo.getSenderNickname() + " 님이 참가 요청을 수락했습니다.");
            messageVo.setTimestamp(LocalDateTime.now());
            messageVo.setIsChecked(false);
        } else if (messageVo.getUsage().equals(MessageType.DENY.code)) {
            if (messageVo.getArticleId() < 0 ||
                    !UserValidator.validateEmail(messageVo.getSender()) ||
                    !UserValidator.validateNickname(messageVo.getSenderNickname()) ||
                    !UserValidator.validateEmail(messageVo.getReceiver()) ||
                    !UserValidator.validateNickname(messageVo.getReceiverNickname())) {
                return CommonResult.FAILURE;
            }
            messageVo.setContent(messageVo.getSenderNickname() + " 님이 참가 요청을 거절했습니다.");
            messageVo.setTimestamp(LocalDateTime.now());
            messageVo.setIsChecked(false);
        }

        if (this.messageMapper.insert(messageVo) > 0) {
            return CommonResult.SUCCESS;
        } else {
            return CommonResult.FAILURE;
        }
    }

    public CommonResult modifyIsChecked(int messageId, UserEntity sessionUser) {
        if (sessionUser == null ||
                this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return CommonResult.FAILURE;
        }
        if (messageId < 1) {
            return CommonResult.FAILURE;
        }
        MessageEntity dbMessageEntity = this.messageMapper.selectById(messageId);
        if (dbMessageEntity == null) {
            return CommonResult.FAILURE;
        }
        dbMessageEntity.setIsChecked(true);
        this.messageMapper.modify(dbMessageEntity);
        return CommonResult.SUCCESS;
    }

    public CommonResult modifyParticipants(UserEntity sessionUser, String sender, int articleId) {
        if (sessionUser == null
                || this.userMapper.selectByEmail(sessionUser.getEmail()) == null
                || this.userMapper.selectByEmail(sender) == null) {
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
            participants[count] = sender;
            count++;
        }
        dbParticipantEntity.setParticipants(String.join(",", participants));
        dbParticipantEntity.setCount(count);
        return this.participantMapper.update(dbParticipantEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
