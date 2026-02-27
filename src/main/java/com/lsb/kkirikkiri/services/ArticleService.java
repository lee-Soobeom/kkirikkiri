package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.LocationEntity;
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
    private final WalletService walletService;
    private final ParticipantService participantService;
    private final ArticleMapper articleMapper;
    private final ParticipantMapper participantMapper;

    public ArticleEntity getArticleById(int id) {
        if (id < 1) {
            return null;
        }
        this.articleMapper.incrementView(id);
        return this.articleMapper.selectById(id);
    }

    public List<ArticleVo> getNoticeList (String boardId) {
        if (boardId == null) {
            return List.of();
        }
        return this.articleMapper.selectNoticeList(boardId);
    }

    public ArticleVo[] getAllByBoardIdAndMenu(BoardPageVo boardPageVo, String boardId, String menu) {
        if (boardId == null) {
            return new ArticleVo[0];
        }

        if (menu == null || menu.isEmpty() || "all".equals(menu)) {
            if ("view".equals(boardPageVo.getSort())) {
                return this.articleMapper.selectAllByBoardIdOrderByView(boardPageVo, boardId);
            }
            return this.articleMapper.selectAllByBoardIdOrderByCreatedAt(boardPageVo, boardId);
        }

        if ("view".equals(boardPageVo.getSort())) {
            return this.articleMapper.selectAllBoardIdAndMenuOrderByView(boardPageVo, boardId, menu);
        }
        return this.articleMapper.selectAllBoardIdAndMenuOrderByCreatedAt(boardPageVo, boardId, menu);
    }

    public ArticleVo[] getImminentShareArticles(String menu, LocationEntity pos) {
        return this.articleMapper.selectImminentShareArticles("share", menu, pos);
    }

    public ArticleVo[] getAllBoardSearch(BoardPageVo boardPageVo, BoardSearchVo boardSearchVo) {
        if (boardSearchVo == null ||
                boardSearchVo.getKeyword() == null ||
                boardSearchVo.getId() == null ||
                boardSearchVo.getBy() == null ||
                !List.of("titleContent", "title", "userId").contains(boardSearchVo.getBy())) {
            return new ArticleVo[0];
        }
        return this.articleMapper.selectAllByBoardSearch(boardPageVo, boardSearchVo);
    }

    public int getCountByBoardId(String boardId) {
        if (boardId == null) return 0;
        return this.articleMapper.selectCountByBoardId(boardId);
    }

    public int getCountByBoardIdAndMenu(String boardId, String menu) {
        if (boardId == null) return 0;
        return this.articleMapper.selectCountByBoardIdAndMenu(boardId, menu);
    }


    public int getCountByBoardSearch(BoardSearchVo boardSearchVo) {
        if (boardSearchVo == null ||
                boardSearchVo.getId() == null ||
                boardSearchVo.getBy() == null ||
                !List.of("titleContent", "title", "userId").contains(boardSearchVo.getBy())) {
            return 0;
        }
        return this.articleMapper.selectCountByBoardSearch(boardSearchVo);
    }



    @Transactional
    public Pair<Map<String, Object>, ArticleEntity> write(UserEntity sessionUser, ArticleEntity articleEntity, List<MultipartFile> files) {
        System.out.println("넘어온 boardId = [" + articleEntity.getBoardId() + "]");
        Map<String, Object> result = new HashMap<>();
        if (sessionUser == null) {
            System.out.println("session");
            result.put("articleResult", CommonResult.FAILURE);
            return Pair.of(result, null);
        }
        if (articleEntity == null ||
                !ArticleValidator.validateBoardId(articleEntity) ||
                !ArticleValidator.validateTitle(articleEntity) ||
                !ArticleValidator.validateContent(articleEntity)) {
            System.out.println("basic");
            result.put("articleResult", CommonResult.FAILURE);
            return Pair.of(result, null);
        }
        String boardId = articleEntity.getBoardId();
        // 공구 게시판 (share)
        if ("share".equals(boardId)) {
            System.out.println("boardId: " + articleEntity.getBoardId());
            System.out.println("title: " + articleEntity.getTitle());
            System.out.println("menu: " + articleEntity.getMenu());
            System.out.println("menuName: " + articleEntity.getMenuName());
            System.out.println("orderPrice: " + articleEntity.getOrderPrice());
            System.out.println("deliveryPrice: " + articleEntity.getDeliveryPrice());
            System.out.println("orderTime: " + articleEntity.getOrderTime());
            System.out.println("pickupTime: " + articleEntity.getPickupTime());
            System.out.println("restaurant: " + articleEntity.getRestaurant());
            System.out.println("addressSecondary: " + articleEntity.getAddressSecondary());
            if (!ArticleValidator.validateMenu(articleEntity) ||
                    !ArticleValidator.validateMenuName(articleEntity) ||
                    !ArticleValidator.validateMinOrderPrice(articleEntity) ||
                    !ArticleValidator.validateOrderPrice(articleEntity) ||
                    !ArticleValidator.validateDeliveryPrice(articleEntity) ||
                    !ArticleValidator.validateOrderTime(articleEntity) ||
                    !ArticleValidator.validatePickupTime(articleEntity) ||
                    !ArticleValidator.validateRestaurant(articleEntity) ||
                    !ArticleValidator.validateAddressSecondary(articleEntity)) {
                result.put("articleResult", CommonResult.FAILURE);
                return Pair.of(result, null);
            }
            articleEntity.setUserId(sessionUser.getEmail());
            articleEntity.setCreatedAt(LocalDateTime.now());
            articleEntity.setShareChecked(false);
            articleEntity.setEntryChecked(false);
            if (this.articleMapper.insert(articleEntity) > 0) {
                result.put("articleResult", CommonResult.SUCCESS);
            } else {
                result.put("articleResult", CommonResult.FAILURE);
            }
            result.put("participantResult", this.participantService.createParticipants(articleEntity.getId(), sessionUser.getEmail(), sessionUser.getNickname()));
            result.put("walletResult", this.walletService.createGroupWallet(articleEntity.getId()));
            if (result.get("articleResult") != CommonResult.SUCCESS
                    ||  result.get("participantResult") != CommonResult.SUCCESS
                    ||  result.get("walletResult") != CommonResult.SUCCESS) {
                // 게시글 작성 성공하면 참여자 테이블 & 공구 지갑 만들기 >> 하나라도 실패하면
                throw new TransactionalException(CommonResult.FAILURE);
            }

        }
        // 홍보 게시판 (promote)
        if ("promote".equals(boardId)) {
            if (!sessionUser.isBoss()) {
                result.put("articleResult", CommonResult.FAILURE);
                return Pair.of(result, null);
            }
            if (!ArticleValidator.validateRestaurant(articleEntity)) {
                result.put("articleResult", CommonResult.FAILURE);
                return Pair.of(result, null);
            }
            articleEntity.setUserId(sessionUser.getEmail());
            articleEntity.setCreatedAt(LocalDateTime.now());
            articleEntity.setUpdatedAt(LocalDateTime.now());
            articleEntity.setView(0);
            articleEntity.setShareChecked(false);
            articleEntity.setEntryChecked(false);

            if (this.articleMapper.insert(articleEntity) > 0) {
                result.put("articleResult", CommonResult.SUCCESS);
            } else {
                result.put("articleResult", CommonResult.FAILURE);
            }

        }
        // 공지 게시판 (notice)
        // → 제목 + 내용만 있으면 OK (추가 검증 없음)
        if ("notice".equals(boardId)) {
            if (!sessionUser.isAdmin()) {
                result.put("articleResult", CommonResult.FAILURE);
                return Pair.of(result, null);
            }

            articleEntity.setUserId(sessionUser.getEmail());
            articleEntity.setCreatedAt(LocalDateTime.now());
            articleEntity.setUpdatedAt(LocalDateTime.now());
            articleEntity.setView(0);

            articleEntity.setShareChecked(false);
            articleEntity.setEntryChecked(false);

            if (this.articleMapper.insert(articleEntity) > 0) {
                result.put("articleResult", CommonResult.SUCCESS);
            } else {
                result.put("articleResult", CommonResult.FAILURE);
            }
        }

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

    // 게시글 수정
    @Transactional
    public CommonResult modify(UserEntity sessionUser, ArticleEntity articleEntity){
        if (sessionUser == null || articleEntity == null) {
            return CommonResult.FAILURE;
        }
        ArticleVo dbArticle = this.articleMapper.selectById(articleEntity.getId());
        if (dbArticle == null) {
            return CommonResult.FAILURE;
        }
        // 작성자 본인 체크
        if (!dbArticle.getUserId().equals(sessionUser.getEmail())) {
            return CommonResult.FAILURE;
        }

        articleEntity.setUserId(sessionUser.getEmail());
        articleEntity.setUpdatedAt(LocalDateTime.now());
        if (articleEntity.getShareChecked() == null) {
            articleEntity.setShareChecked(false);
        }
        if (articleEntity.getEntryChecked() == null) {
            articleEntity.setEntryChecked(false);
        }
        return this.articleMapper.update(articleEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 게시글 삭제
    @Transactional
    public CommonResult delete(UserEntity sessionUser, int id) {
        if (sessionUser == null || id < 1) {
            return CommonResult.FAILURE;
        }

        ArticleVo dbArticle = this.articleMapper.selectById(id);

        if (dbArticle == null) {
            return CommonResult.FAILURE;
        }

        if (!dbArticle.getUserId().equals(sessionUser.getEmail())) {
            return CommonResult.FAILURE;
        }
        if ("share".equals(dbArticle.getBoardId())) {
            this.participantMapper.deleteByArticleId(id);
        }
        return this.articleMapper.deleteById(id) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
