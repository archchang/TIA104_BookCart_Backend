package com.websocketchat;

import com.websocketchat.ChatMessageDao;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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
    	String key1 = generateKey(sender, receiver);
        String key2 = generateKey(receiver, sender);
        
        // 儲存訊息到雙方的聊天記錄
        redisTemplate.opsForList().rightPush(key1, message);
        redisTemplate.opsForList().rightPush(key2, message);
        
        // 設置1天過期時間
        redisTemplate.expire(key1, 1, TimeUnit.DAYS);
        redisTemplate.expire(key2, 1, TimeUnit.DAYS);
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
    	String[] participants = {sender, receiver};
        Arrays.sort(participants); // 確保相同參與者的key一致
    	return String.format("chat:%s:%s", sender, receiver);
    }
}