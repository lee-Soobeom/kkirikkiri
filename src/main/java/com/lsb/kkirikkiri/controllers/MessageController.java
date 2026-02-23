package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.MessageEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.services.MessageService;
import com.lsb.kkirikkiri.services.ParticipantService;
import com.lsb.kkirikkiri.vos.MessageVo;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final ParticipantService participantService;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getMessage(HttpSession session,
                                          @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                          @SessionAttribute(value = "messageTimestamp", required = false) LocalDateTime timestamp) {
        Map<String, Object> response = new HashMap<>();
        Pair<CommonResult, MessageVo[]> result = this.messageService.getValidMessage(sessionUser, timestamp);
        response.put("result", result.getLeft().name());
        if (result.getLeft().equals(CommonResult.SUCCESS)) {
            response.put("messages", result.getRight());
        }
        session.setAttribute("messageTimestamp", LocalDateTime.now());
        return response;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postMessage(MessageVo messageVo,
                                           @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();
        Result result = this.messageService.writeMessage(messageVo, sessionUser);
        response.put("result", result.name());
        return response;
    }

    @RequestMapping(value = "/checked", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postChecked(@RequestParam(value = "messageId", required = false, defaultValue = "0") int messageId,
                                          @SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser) {
        Map<String, Object> response = new HashMap<>();
        CommonResult result = this.messageService.modifyIsChecked(messageId, sessionUser);
        response.put("result", result.name());
        return response;
    }

    @RequestMapping(value = "/participants", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> postParticipants(@SessionAttribute(value = "sessionUser", required = false) UserEntity sessionUser,
                                                MessageVo messageVo) {
        Map<String, Object> response = new HashMap<>();
        CommonResult result = this.participantService.modifyParticipants(sessionUser, messageVo);

        response.put("result", result.name());
        return response;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduledMessage() {
        this.messageService.scheduleMessage();
    }
}
