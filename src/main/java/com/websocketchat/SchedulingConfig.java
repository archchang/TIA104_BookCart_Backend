package com.websocketchat;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    
    private final ChatMessageDao chatMessageDao;
    
    public SchedulingConfig(ChatMessageDao chatMessageDao) {
        this.chatMessageDao = chatMessageDao;
    }

    @Scheduled(cron = "0 0 0 * * ?") // 每天凌晨執行
    public void cleanExpiredMessages() {
        chatMessageDao.deleteExpiredMessages();
    }
}