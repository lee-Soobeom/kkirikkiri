package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.BoardEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.enums.BoardId;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.ArticleService;
import com.lsb.kkirikkiri.services.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final BoardService boardService;
    // 글 작성 페이지
    @RequestMapping(
            value = "/{boardType}/write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ModelAndView getWrite(
            @PathVariable String boardType,
            @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
            ModelAndView modelAndView
    ) {
        BoardEntity board = this.boardService.getBoardById(boardType);

        modelAndView.addObject("board", board);
        modelAndView.addObject("sessionUser", sessionUser);
        modelAndView.addObject("redirect", "/article/" + boardType + "/write");
        modelAndView.addObject("article", new ArticleEntity());
        modelAndView.setViewName(BoardId.from(boardType).writeView);

        return modelAndView;
    }
    // 게시글 조회
    @RequestMapping(value = "/{boardType}",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getShareArticle(@PathVariable String boardType,
                                        @RequestParam int id,
                                        ModelAndView modelAndView) {
        modelAndView.addObject("article", this.articleService.getArticleById(id));
        modelAndView.setViewName(BoardId.from(boardType).articleView);
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
    @RequestMapping(
            value = "/{boardType}/write",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public Map<String, Object> postWrite(
            @PathVariable String boardType,
            ArticleEntity articleEntity,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser
    ) {
        Map<String, Object> response = new HashMap<>();

        // 로그인 안 한 경우
        if (sessionUser == null) {
            response.put("result", CommonResult.FAILURE.name());
            return response;
        }

        // 게시판 설정
        articleEntity.setBoardId(boardType);
        articleEntity.setNickname(sessionUser.getNickname());

        Pair<CommonResult, ArticleEntity> result =
                this.articleService.write(files, articleEntity);

        response.put("result", result.getLeft().name());
        if (result.getLeft() == CommonResult.SUCCESS) {
            response.put("id", result.getRight().getId());
        }

        return response;
    }
}
