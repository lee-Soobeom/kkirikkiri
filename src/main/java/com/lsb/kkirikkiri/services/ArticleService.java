package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.mappers.ArticleMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleMapper articleMapper;

    public ArticleEntity getArticleById(int articleId){
        return this.articleMapper.selectById(articleId);
    }

    public CommonResult write(ArticleEntity articleEntity) {
        articleEntity.setCreatedAt(LocalDateTime.now());
        return this.articleMapper.insert(articleEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
