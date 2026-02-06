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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final FileService fileService;
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
            return this.articleMapper.selectAllByBoardIdOrderByView(boardPageVo, boardId);
        }
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

    public Pair<CommonResult, ArticleEntity> write(List<MultipartFile> files, ArticleEntity articleEntity) {
        if (articleEntity == null ||
                !ArticleValidator.validateBoardId(articleEntity) ||
                !ArticleValidator.validateTitle(articleEntity) ||
                !ArticleValidator.validateContent(articleEntity)) {
            return Pair.of(CommonResult.FAILURE, null);
        }

        String boardId = articleEntity.getBoardId();

        // 공구 게시판 (share)
        if ("share".equals(boardId)) {
            if (!ArticleValidator.validateMenu(articleEntity) ||
                    !ArticleValidator.validateMenuName(articleEntity) ||
                    !ArticleValidator.validateMinOrderPrice(articleEntity) ||
                    !ArticleValidator.validateOrderPrice(articleEntity) ||
                    !ArticleValidator.validateDeliveryPrice(articleEntity) ||
                    !ArticleValidator.validateOrderTime(articleEntity) ||
                    !ArticleValidator.validatePickupTime(articleEntity) ||
                    !ArticleValidator.validateRestaurant(articleEntity) ||
                    !ArticleValidator.validateAddressSecondary(articleEntity)) {

                return Pair.of(CommonResult.FAILURE, null);
            }
        }

        // 홍보 게시판 (promote)
        if ("promote".equals(boardId)) {
            if (!ArticleValidator.validateRestaurant(articleEntity) ||
                    !ArticleValidator.validateAddressSecondary(articleEntity)) {

                return Pair.of(CommonResult.FAILURE, null);
            }
        }

        // 공지 게시판 (notice)
        // → 제목 + 내용만 있으면 OK (추가 검증 없음)

        articleEntity.setCreatedAt(LocalDateTime.now());

        int articleResult = this.articleMapper.insert(articleEntity);
        // file upload: filesEntity + articleId + userEmail
        CommonResult fileResult = this.fileService.postFile(files);
        return articleResult > 0
                ? Pair.of(CommonResult.SUCCESS, articleEntity)
                : Pair.of(CommonResult.FAILURE, null);
    }
}
