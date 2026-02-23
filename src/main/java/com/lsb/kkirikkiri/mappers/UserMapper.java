package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(@Param(value = "user") UserEntity user);

    int deleteUserByEmail(@Param("email") String email);

    UserEntity selectByEmail(@Param(value = "email") String email);

    UserEntity selectByNickname(@Param(value = "nickname") String nickname);

    int update(@Param(value = "user") UserEntity user);
}
