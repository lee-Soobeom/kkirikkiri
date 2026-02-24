package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.services.ArticleService;
import com.lsb.kkirikkiri.services.WalletService;
import com.lsb.kkirikkiri.vos.ArticleVo;
import com.lsb.kkirikkiri.vos.BoardPageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;
    private final WalletService walletService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView index(ModelAndView modelAndView,
                              @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                              @RequestParam(value = "menu", required = false) String menu,
                              @RequestParam(value = "sort", required = false) String sort) {
        if (sessionUser != null) {
             modelAndView.addObject("wallet",this.walletService.getUserWallet(sessionUser.getEmail()));
        }

        ArticleVo[] articles = this.articleService.getImminentShareArticles(menu);
        modelAndView.addObject("articles", articles);
        modelAndView.addObject("menu", menu);
        modelAndView.addObject("sort", sort);
        modelAndView.setViewName("home/home");
        return modelAndView;
    }
}
