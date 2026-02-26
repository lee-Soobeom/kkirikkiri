package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.LocationEntity;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import com.lsb.kkirikkiri.vos.BoardSearchVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ArticleMapper {
    int insert(ArticleEntity articleEntity);

    ArticleVo selectById(@Param(value = "id") int id);

    ArticleEntity[] selectAllByOrderTime();

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

    ArticleVo[] selectAllBoardIdAndMenuOrderByCreatedAt(
            @Param("boardPageVo") BoardPageVo boardPageVo,
            @Param("boardId") String boardId,
            @Param("menu") String menu
    );

    ArticleVo[] selectAllBoardIdAndMenuOrderByView(
            @Param("boardPageVo") BoardPageVo boardPageVo,
            @Param("boardId") String boardId,
            @Param("menu") String menu
    );

    ArticleVo[] selectImminentShareArticles(
            @Param("boardId") String boardId,
            @Param("menu") String menu,
            @Param("location")LocationEntity pos
            );

    Integer selectCountByBoardId(@Param("boardId") String boardId);

    int selectCountByBoardIdAndMenu(@Param(value = "boardId") String boardId,
                                    @Param("menu") String menu);

    int selectCountByBoardSearch(@Param(value = "boardSearch") BoardSearchVo boardSearchVo);

    int deleteById(@Param("id") int id);


}
