package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.LocationEntity;
import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.services.ServiceService;
import com.lsb.kkirikkiri.vos.ServiceBoardPageVo;
import com.lsb.kkirikkiri.vos.ServiceVo;
import lombok.RequiredArgsConstructor;
import com.lsb.kkirikkiri.entities.BoardEntity;
import com.lsb.kkirikkiri.services.ArticleService;
import com.lsb.kkirikkiri.services.BoardService;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import com.lsb.kkirikkiri.vos.BoardSearchVo;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/board")
@RequiredArgsConstructor
public class BoardController {
    private final ServiceService serviceService;
    private final ArticleService articleService;
    private final BoardService boardService;

    @RequestMapping(value = "/list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "id", required = false) String id,
                                @RequestParam(value = "page", defaultValue = "1") int requestPage,
                                @RequestParam(value = "sort", required = false) String sort,
                                @RequestParam(value = "menu", required = false, defaultValue = "all") String menu,
                                LocationEntity pos,
                                BoardSearchVo boardSearchVo, ModelAndView modelAndView) {
        BoardEntity board = this.boardService.getBoardById(id);
        modelAndView.addObject("board", board);
        if (board != null) {
            boolean isSearching = boardSearchVo.getBy() != null && boardSearchVo.getKeyword() != null;
            int totalCount = isSearching
                    ? this.articleService.getCountByBoardSearch(boardSearchVo)
                    : ("all".equals(menu)
                    ? this.articleService.getCountByBoardId(id)
                    : this.articleService.getCountByBoardIdAndMenu(id, menu));

            BoardPageVo boardPageVo = new BoardPageVo(requestPage, totalCount, sort);

            ArticleVo[] articles = isSearching
                    ? this.articleService.getAllBoardSearch(boardPageVo, boardSearchVo)
                    : this.articleService.getAllByBoardIdAndMenu(boardPageVo, id, menu);

            modelAndView.addObject("boardPageVo", boardPageVo);
            modelAndView.addObject("boardSearchVo", boardSearchVo);
            modelAndView.addObject("articles", articles);
            modelAndView.addObject("selectedMenu", menu);
        }
        modelAndView.setViewName("board/list");
        return modelAndView;
    }

    @RequestMapping(value = "/service",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getService(@RequestParam(value = "page", required = false, defaultValue = "1") int requestPage,
                                   @RequestParam(value = "filter", required = false, defaultValue = "all") String filter,
                                   ModelAndView modelAndView) {
        int totalCount = filter.equals("all")
                ? this.serviceService.getAllCount()
                : this.serviceService.getAllCountByFilter(filter);
        ServiceBoardPageVo serviceBoardPageVo = new ServiceBoardPageVo(totalCount, requestPage);
        ServiceVo[] services = filter.equals("all")
                ? this.serviceService.getAllServices(serviceBoardPageVo)
                : this.serviceService.getAllServicesByFilter(serviceBoardPageVo, filter);
        modelAndView.addObject("filter", filter);
        modelAndView.addObject("services", services);
        modelAndView.addObject("boardPageVo", serviceBoardPageVo);
        modelAndView.setViewName("board/service");
        return modelAndView;
    }
}
