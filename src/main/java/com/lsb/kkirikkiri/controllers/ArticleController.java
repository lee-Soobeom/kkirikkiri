package com.lsb.kkirikkiri.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/article")
public class ArticleController {
    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public String getArticle() {
        return "article/write";
    }
}
