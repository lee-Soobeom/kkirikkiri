package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.validators.ArticleValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleMapper articleMapper;

    public ArticleEntity getArticleById(int id) {
        ArticleEntity dbArticleEntity = this.articleMapper.selectById(id);
        dbArticleEntity.setView(dbArticleEntity.getView() + 1);
        this.articleMapper.update(dbArticleEntity);
        return dbArticleEntity;
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
