package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.BoardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BoardMapper {
    BoardEntity selectById(@Param(value = "id") String id);
    BoardEntity[] getAll();
}
