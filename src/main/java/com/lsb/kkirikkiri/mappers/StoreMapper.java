package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.user.StoreEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoreMapper {

    int insert(@Param(value = "store") StoreEntity store);

    int update(@Param(value = "store") StoreEntity store);

    StoreEntity selectByEmail(@Param(value = "email") String email);
}
