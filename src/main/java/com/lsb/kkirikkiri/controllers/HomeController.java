package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.LocationEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.services.ArticleService;
import com.lsb.kkirikkiri.vos.ArticleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView index(ModelAndView modelAndView,
                              @RequestParam(value = "menu", required = false, defaultValue = "all") String menu,
                              @RequestParam(value = "sort", required = false) String sort) {
        modelAndView.addObject("menu", menu);
        modelAndView.addObject("sort", sort);
        modelAndView.setViewName("home/home");
        return modelAndView;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, ArticleVo[]> postHome(
            @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
            @RequestParam(value = "menu", required = false, defaultValue = "all") String menu,
            LocationEntity pos) {
        Map<String, ArticleVo[]> response = new HashMap<>();
        response.put("articles", this.articleService.getImminentShareArticles(menu, pos));
        return response;
    }
}
