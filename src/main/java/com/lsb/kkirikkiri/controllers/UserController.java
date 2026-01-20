package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.UserEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        if (sessionUser != null) {
            return "redirect:/user/";
        }

        return "user/unsigned";
    }
}
