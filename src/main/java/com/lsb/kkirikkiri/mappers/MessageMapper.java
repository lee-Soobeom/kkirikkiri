package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.MessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface MessageMapper {
    MessageEntity[] selectByEmail(@Param(value = "email") String email,
                                  @Param(value = "timestamp") LocalDateTime timestamp);

    MessageEntity[] selectAllByEmail(@Param(value = "email") String email);
}
