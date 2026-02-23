package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.ParticipantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ParticipantMapper {
    int insert(@Param(value = "participant")ParticipantEntity participant);

    int update(@Param(value = "participant") ParticipantEntity participant);

    int deleteByArticleId(@Param("articleId") int articleId);

    ParticipantEntity selectById(@Param(value = "articleId") int articleId);
}
