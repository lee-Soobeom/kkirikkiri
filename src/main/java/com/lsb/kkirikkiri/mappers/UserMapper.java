package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(@Param(value = "user") UserEntity user);

    UserEntity selectByEmail(@Param(value = "email") String email);
}
