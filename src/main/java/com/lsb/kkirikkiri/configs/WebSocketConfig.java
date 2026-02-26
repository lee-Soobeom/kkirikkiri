package com.lsb.kkirikkiri.configs;

import com.lsb.kkirikkiri.interceptors.WsHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // for broadcast message
        config.enableSimpleBroker("/topic");
        // for get message
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // for connection
        registry.addEndpoint("/article-chat")
                .addInterceptors(new WsHandshakeInterceptor())
                .setAllowedOrigins("http://localhost:8080")
                .withSockJS();
    }
}
