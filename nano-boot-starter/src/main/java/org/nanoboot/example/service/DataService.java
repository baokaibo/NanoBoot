package org.nanoboot.example.service;

import org.nanoboot.annotation.Annotation.Autowired;
import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Value;
import org.nanoboot.data.redis.RedisClient;

@Service
public class DataService {

    @Autowired(required = false) // 允许RedisClient不存在
    private RedisClient redisClient;

    @Value("${app.name:NanoBootApp}")
    private String appName;

    public String getAppName() {
        return appName;
    }

    public void saveData(String key, String value) {
        // 使用Redis存储数据（如果可用）
        if (redisClient != null) {
            redisClient.set(key, value);
        }

        // 同时也可以存储到MySQL（如果需要）
        // 这里只是示例，实际实现会更复杂
    }

    public String getData(String key) {
        // 首先尝试从Redis获取（如果可用）
        if (redisClient != null) {
            String data = redisClient.get(key);
            if (data != null) {
                return data;
            }
        }
        // 如果Redis不可用或数据不存在，返回默认值
        return "Default value for " + key;
    }

    public boolean existsInCache(String key) {
        return redisClient != null && redisClient.exists(key);
    }
}