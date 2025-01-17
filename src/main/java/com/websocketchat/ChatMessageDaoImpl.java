package com.websocketchat;

import com.websocketchat.ChatMessageDao;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class ChatMessageDaoImpl implements ChatMessageDao {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public ChatMessageDaoImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveMessage(String sender, String receiver, String message) {
        String key = generateKey(sender, receiver);
        redisTemplate.opsForList().rightPush(key, message);
        // 設置1天過期
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    @Override
    public List<String> getHistoryMessages(String sender, String receiver) {
        String key = generateKey(sender, receiver);
        return redisTemplate.opsForList().range(key, 0, -1)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExpiredMessages() {
        // Redis會自動處理過期的鍵，不需要額外實現
    }

    private String generateKey(String sender, String receiver) {
        return String.format("chat:%s:%s", sender, receiver);
    }
}