package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileMapper {
    int insert(@Param(value = "file")FileEntity fileEntity);
}
