package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.ArticleEntity;
import com.lsb.kkirikkiri.entities.BoardEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.enums.BoardId;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final BoardService boardService;
    private final ParticipantService participantService;
    private final WalletService walletService;
    private final FileService fileService;

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

        if (board == null) {
            modelAndView.addObject("board", null);
            modelAndView.setViewName("article/share/write");
            return modelAndView;
        }

        if (board.isAdminOnly() && (sessionUser == null || !sessionUser.isAdmin())) {
            modelAndView.setViewName("redirect:/access-denied");
            return modelAndView;
        }

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
        modelAndView.addObject("boardType", boardType);
        ArticleEntity dbArticle = this.articleService.getArticleById(id);
        modelAndView.addObject("article", dbArticle);
        if (boardType.equals("share")) {
            modelAndView.addObject("participants", this.participantService.getParticipantByArticleId(id));
            modelAndView.addObject("groupWallet", this.walletService.getGroupWalletByArticleId(id));
            long diff = Duration.between(LocalDateTime.now(), dbArticle.getOrderTime()).toMinutes();
            System.out.println(diff);
            if (diff >= 0 && diff <= 20) {
                modelAndView.addObject("deadline", true);
            } else {
                modelAndView.addObject("deadline", false);
            }
        }
        if (boardType.equals("promote")) {
            modelAndView.addObject("files", this.fileService.getFilesByArticleId(id));
        }

        modelAndView.setViewName(BoardId.from(boardType).articleView);
        return modelAndView;
    }

    // 게시글 수정
    @RequestMapping(value = "/{boardType}/modify",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@PathVariable String boardType,
                                  @RequestParam int id,
                                  @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                  ModelAndView modelAndView) {
        ArticleEntity article = this.articleService.getArticleById(id);

        if (article == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
        if (sessionUser == null || !article.getUserId().equals(sessionUser.getEmail())) {
            modelAndView.setViewName("redirect:/access-denied");
            return modelAndView;
        }

        modelAndView.addObject("article", article);
        modelAndView.addObject("board", this.boardService.getBoardById(boardType));
        modelAndView.addObject("sessionUser", sessionUser);
        modelAndView.setViewName(BoardId.from(boardType).writeView);

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
        BoardEntity board = this.boardService.getBoardById(boardType);

        if (board != null && board.isAdminOnly() && (sessionUser == null || !sessionUser.isAdmin())) {
            response.put("articleResult", CommonResult.FAILURE);
            response.put("fileResult", CommonResult.FAILURE);
            return response;
        }
        // 게시판 설정
        articleEntity.setBoardId(boardType);

        Pair<Map<String, Object>, ArticleEntity> result =
                this.articleService.write(sessionUser, articleEntity, files);
        if (result.getLeft().get("articleResult") == CommonResult.SUCCESS) {
            response.put("id", result.getRight().getId());
        }
        response.put("articleResult", result.getLeft().get("articleResult"));
        if ("share".equals(boardType)) {
            response.put("participants", this.participantService.getParticipantByArticleId(result.getRight().getId()));
        }
        response.put("result", result.getLeft().get("articleResult"));
        response.put("fileResult", result.getLeft().get("fileResult"));
        if (result.getLeft().get("fileResult") == CommonResult.SUCCESS) {
            response.put("fileResultList", result.getLeft().get("fileResultList"));
        }
        return response;
    }

    @RequestMapping(value = "/{boardType}/modify",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> PostModify(@PathVariable String boardType,
                                          ArticleEntity articleEntity,
                                          @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();

        articleEntity.setBoardId(boardType);

        CommonResult result = this.articleService.modify(sessionUser, articleEntity);
        response.put("result", result);
        return response;
    }

    @RequestMapping(value = "/{boardType}/delete",
    method = RequestMethod.POST)
    public String postDelete(@PathVariable String boardType,
                             @RequestParam int id,
                             @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser == null) {
            return "redirect:/access-denied";
        }
        CommonResult result = this.articleService.delete(sessionUser, id);

        if (result == CommonResult.SUCCESS) {
            return "redirect:/board/list?id=" + boardType;
        }
        return "redirect:/board/list?id=" + boardType;
    }
}
