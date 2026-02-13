package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.WalletEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WalletMapper {
    int insert(@Param(value = "wallet") WalletEntity wallet);
}
