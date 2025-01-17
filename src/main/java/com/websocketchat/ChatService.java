package com.websocketchat;

import java.util.List;

public interface ChatService {
    void handleMessage(String message);
    
    List<String> getHistoryMessages(String sender, String receiver);
    
    void saveMessage(String sender, String receiver, String message);
}