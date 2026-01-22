package com.lsb.kkirikkiri.controllers;

import com.lsb.kkirikkiri.entities.WSMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping(value = "/chat")
    @SendTo("/topic/messages")
    public WSMessage sendMessage(WSMessage message) throws Exception {
        Thread.sleep(1000);
        return message;
    }
}
