package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.services.MessageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getMessage(HttpSession session,
                                          @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                          @SessionAttribute(value = "messageTimestamp", required = false) LocalDateTime timestamp) {
        Map<String, Object> response = new HashMap<>();
        Pair<CommonResult, MessageEntity[]> result = this.messageService.getValidMessage(sessionUser, timestamp);
        session.setAttribute("messageTimestamp", LocalDateTime.now());
        response.put("result", result.getLeft().name());
        if (result.getLeft().equals(CommonResult.SUCCESS)) {
            response.put("messages", result.getRight());
        }
        return response;
    }
}
