package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {
    int insert(@Param(value = "user") UserEntity user);

    int deleteUserByEmail(@Param("email") String email);

    UserEntity selectByEmail(@Param(value = "email") String email);

    UserEntity selectByNickname(@Param(value = "nickname") String nickname);

    int update(@Param(value = "user") UserEntity user);

    int updatePassword(@Param(value = "email") String email, @Param(value = "password") String password);

    String selectEmailByContact(@Param(value = "contact") String contact);

    UserEntity findBySocialId(@Param(value = "socialTypeCode") String socialTypeCode, @Param(value = "socialId") String socialId);

    void insertSocialUser(@Param("user") UserEntity user);

    int updateUserExtraInfo(UserEntity user);

    UserEntity findByEmail(@Param(value = "email") String email);

    int updateSocialInfo(UserEntity user);
}
