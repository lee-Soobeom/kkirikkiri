package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {
    int insert(@Param(value = "article") ArticleEntity articleEntity);

    ArticleEntity selectById(@Param(value = "id") int id);

    int update(@Param(value = "article")  ArticleEntity articleEntity);
}
