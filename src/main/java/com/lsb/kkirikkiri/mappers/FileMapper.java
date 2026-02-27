package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileMapper {
    int insert(@Param(value = "file")FileEntity fileEntity);

    List<FileEntity> selectByArticleId(int articleId);
}
