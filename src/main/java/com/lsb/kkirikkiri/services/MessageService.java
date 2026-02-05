package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.MessageMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public Pair<CommonResult, MessageEntity[]> getValidMessage(UserEntity sessionUser, LocalDateTime messageTimestamp) {
        if (sessionUser == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        if (this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        MessageEntity[] dbMessages = messageTimestamp == null
                ? this.messageMapper.selectAllByEmail(sessionUser.getEmail())
                : this.messageMapper.selectByEmail(sessionUser.getEmail(), messageTimestamp);
        return Pair.of(CommonResult.SUCCESS, dbMessages);
    }
}
