package com.websocketchat;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final FriendWebSocketHandler friendWebSocketHandler;
    
    // 建構子注入
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler, 
                         FriendWebSocketHandler friendWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.friendWebSocketHandler = friendWebSocketHandler;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 使用注入的實例，而不是創建新的
        registry.addHandler(chatWebSocketHandler, "/TogetherWS/*")
               .setAllowedOrigins("*");
        registry.addHandler(friendWebSocketHandler, "/FriendWS/*")
               .setAllowedOrigins("*");
    }
}