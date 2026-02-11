package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.vos.MessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {
    MessageVo[] selectByEmail(@Param(value = "email") String email,
                              @Param(value = "timestamp") LocalDateTime timestamp);

    MessageVo[] selectAllByEmail(@Param(value = "email") String email);

    MessageEntity selectById(@Param(value = "id") int id);

    int modify(@Param(value = "message") MessageEntity messageEntity);

    int insert(@Param(value = "message")  MessageEntity messageEntity);
}
