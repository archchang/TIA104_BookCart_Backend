package com.websocketchat;

import com.google.gson.Gson;
import com.websocketchat.ChatMessage;
import com.websocketchat.State;
import com.websocketchat.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FriendWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ChatService chatService;
    private final Gson gson;

    public FriendWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
        this.gson = new Gson();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 從 URI 中獲取用戶名
        String userName = extractUserName(session.getUri().getPath());
        sessions.put(userName, session);

        // 發送在線用戶狀態
        Set<String> userNames = sessions.keySet();
        State stateMessage = new State("open", userName, userNames);
        String stateMessageJson = gson.toJson(stateMessage);
        
        sessions.values().forEach(webSocketSession -> {
            try {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(stateMessageJson));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            String sender = chatMessage.getSender();
            String receiver = chatMessage.getReceiver();

            // 處理歷史消息請求
            if ("history".equals(chatMessage.getType())) {
                handleHistoryRequest(session, sender, receiver);
                return;
            }

            // 處理普通消息
            WebSocketSession receiverSession = sessions.get(receiver);
            if (receiverSession != null && receiverSession.isOpen()) {
                receiverSession.sendMessage(message);
                session.sendMessage(message);
                chatService.saveMessage(sender, receiver, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userName = extractUserName(session.getUri().getPath());
        sessions.remove(userName);

        // 通知其他用戶有人離線
        Set<String> userNames = sessions.keySet();
        State stateMessage = new State("close", userName, userNames);
        String stateMessageJson = gson.toJson(stateMessage);

        sessions.values().forEach(webSocketSession -> {
            try {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(stateMessageJson));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleHistoryRequest(WebSocketSession session, String sender, String receiver) {
        try {
            List<String> messages = chatService.getHistoryMessages(sender, receiver);
            ChatMessage historyMessage = new ChatMessage(
                "history",
                sender,
                receiver,
                gson.toJson(messages)
            );
            session.sendMessage(new TextMessage(gson.toJson(historyMessage)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractUserName(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        exception.printStackTrace();
        try {
            session.close(CloseStatus.SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}