package com.example.reseau_social.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.reseau_social.websocket.ConversationWebSocketHandler;

// Configuration class to set up WebSocket endpoints
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ConversationWebSocketHandler conversationWebSocketHandler;

    @Value("${app.cors.allowed-origins:http://localhost:9000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(conversationWebSocketHandler, "/ws/conversations")
                .setAllowedOrigins(allowedOrigins);
    }
}
