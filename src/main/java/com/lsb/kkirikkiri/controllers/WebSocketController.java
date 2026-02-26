package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.WSMessage;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping(value = "/chat/{articleId}")
    public void sendMessage(@DestinationVariable int articleId,
                            SimpMessageHeaderAccessor accessor,
                            WSMessage message) {
        String nickname = (String) accessor.getSessionAttributes().get("nickname");
        message.setSender(nickname);
        simpMessagingTemplate.convertAndSend("/topic/message/" + articleId, message);
    }
}
