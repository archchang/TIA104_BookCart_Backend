package com.websocketchat;

import com.google.gson.Gson;

import com.websocketchat.ChatMessageDao;
import com.websocketchat.ChatMessage;
import com.websocketchat.ChatService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChatServiceImpl implements ChatService {
    
    private final ChatMessageDao chatMessageDao;
    private final Gson gson;
    
    public ChatServiceImpl(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
        this.gson = new Gson();
    }

    @Override
    public void handleMessage(String message) {
        ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
        if ("history".equals(chatMessage.getType())) {
            return;
        }
        saveMessage(chatMessage.getSender(), chatMessage.getReceiver(), message);
    }

    @Override
    public List<String> getHistoryMessages(String sender, String receiver) {
        return chatMessageDao.getHistoryMessages(sender, receiver);
    }

    @Override
    public void saveMessage(String sender, String receiver, String message) {
        chatMessageDao.saveMessage(sender, receiver, message);
    }
}