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
    	// 從WebSocket URL中提取用戶名
        String userName = extractUserName(session.getUri().getPath());
        sessions.put(userName, session);

        // 廣播在線用戶列表
        broadcastUserList();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
            String sender = chatMessage.getSender();
            String receiver = chatMessage.getReceiver();

            // 處理歷史消息請求
            if ("history".equals(chatMessage.getType())) {
                List<String> history = chatService.getHistoryMessages(sender, receiver);
                ChatMessage historyMessage = new ChatMessage(
                    "history",
                    sender,
                    receiver,
                    gson.toJson(history)
                );
                session.sendMessage(new TextMessage(gson.toJson(historyMessage)));
                return;
            }

            // 處理私人消息
            WebSocketSession receiverSession = sessions.get(receiver);
            if (receiverSession != null && receiverSession.isOpen()) {
                // 發送訊息給接收者
                receiverSession.sendMessage(message);
                // 發送者也要收到訊息（用於UI更新）
                session.sendMessage(message);
                // 儲存訊息到Redis
                chatService.saveMessage(sender, receiver, message.getPayload());
            } else {
                // 如果接收者不在線，僅儲存訊息
                chatService.saveMessage(sender, receiver, message.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // 發送錯誤訊息給客戶端
                ChatMessage errorMessage = new ChatMessage(
                    "error",
                    "system",
                    "all",
                    "處理訊息時發生錯誤"
                );
                session.sendMessage(new TextMessage(gson.toJson(errorMessage)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    	// 從WebSocket URL中提取用戶名
        String userName = extractUserName(session.getUri().getPath());
        // 移除離線的session
        sessions.remove(userName);

        // 廣播更新後的用戶列表
        broadcastUserList();
    }

    /**
     * 廣播在線用戶列表給所有連接的用戶
     */
    private void broadcastUserList() {
        Set<String> userNames = sessions.keySet();
        State stateMessage = new State("state", "", userNames);
        String stateJson = gson.toJson(stateMessage);
        
        sessions.values().forEach(ws -> {
            try {
                if (ws.isOpen()) {
                    ws.sendMessage(new TextMessage(stateJson));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 從WebSocket URL路徑中提取用戶名
     */
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