package com.lsb.kkirikkiri.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {
    int insertAuth(@Param("email") String email, @Param("token") String token);

    int countValidToken(@Param(value = "email") String email, @Param(value = "token") String token);

    int updateAuthUsed(@Param(value = "email") String email, @Param(value = "token") String token);
}
