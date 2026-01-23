package com.lsb.kkirikkiri.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/article")
public class ArticleController {
    @RequestMapping(value = "/write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite (ModelAndView modelAndView) {
        modelAndView.setViewName("article/write");
        return modelAndView;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getArticle(ModelAndView modelAndView) {
        modelAndView.setViewName("article/article");
        return modelAndView;
    }

    @RequestMapping(value = "/modify",
    method = RequestMethod.GET,
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify (ModelAndView modelAndView) {
        modelAndView.setViewName("article/modify");
        return modelAndView;
    }
}
