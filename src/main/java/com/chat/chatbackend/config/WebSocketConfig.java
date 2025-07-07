package com.chat.chatbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // we will send updates to /topic/receiverName
        config.setApplicationDestinationPrefixes("/app"); // frontend will send to /app/send
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // frontend connects to this
                .setAllowedOriginPatterns("*")
                .withSockJS(); // fallback support
    }
}
