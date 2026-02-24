package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.WSMessage;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value = "/chat/{articleId}")
    public void sendMessage(@DestinationVariable int articleId,
                            @SessionAttribute(value = "sessionUser") UserEntity sessionUser,
                            WSMessage message) {
        message.setSender(sessionUser.getNickname());
        simpMessagingTemplate.convertAndSend("/topic/message/" + articleId, message);
    }
}
