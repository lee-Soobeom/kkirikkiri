package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @RequestMapping(value = "/write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(ModelAndView modelAndView) {
        modelAndView.setViewName("article/write");
        return modelAndView;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getArticle(int id, ModelAndView modelAndView) {
        modelAndView.addObject("article", this.articleService.getArticleById(id));
        modelAndView.setViewName("article/article");
        return modelAndView;
    }

    @RequestMapping(value = "/modify",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(ModelAndView modelAndView) {
        modelAndView.setViewName("article/modify");
        return modelAndView;
    }

    @RequestMapping(value = "/write",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postWrite(ArticleEntity articleEntity) {
        Pair<CommonResult, ArticleEntity> result = this.articleService.write(articleEntity);
        Map<String, Object> response = new HashMap<>();
        response.put("result", result.getLeft().name());
        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("id", articleEntity.getId());
        }
        return response;
    }
}
