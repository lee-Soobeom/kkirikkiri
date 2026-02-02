package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import com.lsb.kkirikkiri.vos.BoardSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {
    int insert(ArticleEntity articleEntity);

    ArticleEntity selectById(@Param(value = "id") int id);

    int update(ArticleEntity articleEntity);

    int incrementView(@Param("id") int id);

    ArticleVo[] selectAllByBoardId(@Param(value = "boardPage")BoardPageVo boardPage,
                                   @Param(value = "boardId") String board);
    ArticleVo[] selectAllByBoardSearch( @Param(value = "boardPage")BoardPageVo boardPage,
                                        @Param(value = "boardSearch")BoardSearchVo boardSearchVo);

    ArticleVo[] selectAllByBoardIdOrderByCreatedAt(@Param("boardPageVo") BoardPageVo boardPageVo,
                                                   @Param("boardId") String boardId);

    ArticleVo[] selectAllByBoardIdOrderByView(@Param("boardPageVo") BoardPageVo boardPageVo,
                                              @Param("boardId") String boardId);

    int selectCountByBoardId(@Param(value = "boardId") String boardId);

    int selectCountByBoardSearch(@Param(value = "boardSearch") BoardSearchVo boardSearchVo);
}
