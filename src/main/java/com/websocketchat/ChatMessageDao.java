package com.websocketchat;

import java.util.List;

public interface ChatMessageDao {
    void saveMessage(String sender, String receiver, String message);
    List<String> getHistoryMessages(String sender, String receiver);
    void deleteExpiredMessages();
}