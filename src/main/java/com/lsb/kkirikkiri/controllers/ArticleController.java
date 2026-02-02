package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.BoardEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.ArticleService;
import com.lsb.kkirikkiri.services.BoardService;
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
    private final BoardService boardService;
    // 글 작성 페이지
    @RequestMapping(value = "/write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@RequestParam(value = "boardId", required = false) String boardId,
                                 @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                 ModelAndView modelAndView) {
        BoardEntity board = this.boardService.getBoardById(boardId);

        modelAndView.addObject("board", board);
        modelAndView.addObject("sessionUser", sessionUser);
        modelAndView.setViewName("article/write");
        return modelAndView;
    }
    // 게시글 조회
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getArticle(int id, ModelAndView modelAndView) {
        modelAndView.addObject("article", this.articleService.getArticleById(id));
        modelAndView.setViewName("article/article");
        return modelAndView;
    }
    // 게시글 수정
    @RequestMapping(value = "/modify",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(ModelAndView modelAndView) {
        modelAndView.setViewName("article/modify");
        return modelAndView;
    }
    // 글 작성 처리
    @RequestMapping(value = "/write",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postWrite(ArticleEntity articleEntity) {
        // 임시 닉네임
        articleEntity.setNickname("testUser");
        Pair<CommonResult, ArticleEntity> result = this.articleService.write(articleEntity);
        Map<String, Object> response = new HashMap<>();
        response.put("result", result.getLeft().name());
        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("id", articleEntity.getId());
        }
        return response;
    }
}
