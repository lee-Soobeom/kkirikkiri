package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.ArticleValidator;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import com.lsb.kkirikkiri.vos.BoardSearchVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleMapper articleMapper;

    public ArticleEntity getArticleById(int id) {
        if (id < 1) {
            return null;
        }
        this.articleMapper.incrementView(id);
        return this.articleMapper.selectById(id);
    }

    public ArticleVo[] getAllByBoardId(BoardPageVo boardPageVo, String boardId) {
        if (boardId == null) {
            return new ArticleVo[0];
        }

        if ("view".equals(boardPageVo.getSort())) {
            // 인기순
            return this.articleMapper.selectAllByBoardIdOrderByView(boardPageVo, boardId);
        }
        // 최신순
        return this.articleMapper.selectAllByBoardIdOrderByCreatedAt(boardPageVo, boardId);
    }


    public ArticleVo[] getAllBoardSearch(BoardPageVo boardPageVo, BoardSearchVo boardSearchVo) {
        if (boardSearchVo == null ||
                boardSearchVo.getKeyword() == null ||
                boardSearchVo.getId() == null ||
                boardSearchVo.getBy() == null ||
                !List.of("titleContent", "title", "nickname").contains(boardSearchVo.getBy())) {
            return new ArticleVo[0];
        }
        return this.articleMapper.selectAllByBoardSearch(boardPageVo, boardSearchVo);
    }

    public int getCountByBoardId(String boardId) {
        if (boardId == null) {
            return 0;
        }
        return this.articleMapper.selectCountByBoardId(boardId);
    }

    public int getCountByBoardSearch(BoardSearchVo boardSearchVo) {
        if (boardSearchVo == null ||
                boardSearchVo.getId() == null ||
                boardSearchVo.getBy() == null ||
                !List.of("titleContent", "title", "nickname").contains(boardSearchVo.getBy())) {
            return 0;
        }
        return this.articleMapper.selectCountByBoardSearch(boardSearchVo);
    }

    public Pair<CommonResult, ArticleEntity> write(ArticleEntity articleEntity) {
        if (articleEntity == null ||
                !ArticleValidator.validateBoardId(articleEntity) ||
                !ArticleValidator.validateTitle(articleEntity) ||
                !ArticleValidator.validateMenu(articleEntity) ||
                !ArticleValidator.validateMenuName(articleEntity) ||
                !ArticleValidator.validateMinOrderPrice(articleEntity) ||
                !ArticleValidator.validateOrderPrice(articleEntity) ||
                !ArticleValidator.validateDeliveryPrice(articleEntity) ||
                !ArticleValidator.validateOrderTime(articleEntity) ||
                !ArticleValidator.validateRestaurant(articleEntity) ||
                !ArticleValidator.validatePickupTime(articleEntity) ||
                !ArticleValidator.validateAddressSecondary(articleEntity) ||
                !ArticleValidator.validateContent(articleEntity)) {
            System.out.println("null or content");
            return Pair.of(CommonResult.FAILURE, null);
        }

        articleEntity.setCreatedAt(LocalDateTime.now());
        return this.articleMapper.insert(articleEntity) > 0
                ? Pair.of(CommonResult.SUCCESS, articleEntity)
                : Pair.of(CommonResult.FAILURE, null);
    }
}
