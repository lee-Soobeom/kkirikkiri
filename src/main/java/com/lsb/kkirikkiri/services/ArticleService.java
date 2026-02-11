package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.ParticipantEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.mappers.ParticipantMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.ArticleValidator;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import com.lsb.kkirikkiri.vos.BoardSearchVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final FileService fileService;
    private final ArticleMapper articleMapper;
    private final ParticipantMapper participantMapper;

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

    @Transactional
    public Pair<Map<String, Object>, ArticleEntity> write(UserEntity sessionUser, ArticleEntity articleEntity, List<MultipartFile> files) {
        Map<String, Object> result = new HashMap<>();
        if (sessionUser == null) {
            System.out.println("session");
            result.put("result", CommonResult.FAILURE);
            return Pair.of(result, null);
        }
        if (articleEntity == null ||
                !ArticleValidator.validateBoardId(articleEntity) ||
                !ArticleValidator.validateTitle(articleEntity) ||
                !ArticleValidator.validateContent(articleEntity)) {
            System.out.println("basic");
            result.put("result", CommonResult.FAILURE);
            return Pair.of(result, null);
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
                result.put("result", CommonResult.FAILURE);
                return Pair.of(result, null);
            }
            articleEntity.setUserId(sessionUser.getEmail());
            articleEntity.setCreatedAt(LocalDateTime.now());
//            articleEntity.setWalletId(null);
//            articleEntity.setParticipantsId(null);
            if (this.articleMapper.insert(articleEntity) > 0) {
                // 게시글 작성 성공하면 참여자 테이블 만들기
                ParticipantEntity participant = new ParticipantEntity(articleEntity.getId(), sessionUser.getEmail(), String.join(",", new String[]{"","","",""}), 1);
                if (this.participantMapper.insert(participant) > 0) {
                    result.put("participantResult", CommonResult.SUCCESS);
                } else {
                    result.put("participantResult", CommonResult.FAILURE);
                    // transactional
                    throw new TransactionalException(CommonResult.FAILURE);
                }
                result.put("articleResult", CommonResult.SUCCESS);
            } else {
                result.put("articleResult", CommonResult.FAILURE);
            }

        }
        // 홍보 게시판 (promote)
        if ("promote".equals(boardId)) {
            if (!ArticleValidator.validateRestaurant(articleEntity) ||
                    !ArticleValidator.validateAddressSecondary(articleEntity)) {
                result.put("result", CommonResult.FAILURE);
                return Pair.of(result, null);
            }

        }
        // 공지 게시판 (notice)
        // → 제목 + 내용만 있으면 OK (추가 검증 없음)

        // file upload: filesEntity + articleId + userEmail
        List<Map<String, CommonResult>> fileResult = this.fileService.postFile(sessionUser, articleEntity, files);
        if (fileResult != null) {
            result.put("fileResult", CommonResult.SUCCESS);
            result.put("fileResultList", fileResult);
        } else {
            result.put("fileResult", CommonResult.FAILURE);
        }
        return Pair.of(result, articleEntity);
    }
}
