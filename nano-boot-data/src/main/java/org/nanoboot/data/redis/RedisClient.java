package org.nanoboot.data.redis;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis客户端配置和管理
 */
@Component
public class RedisClient {

    @Value("${redis.host:localhost}")
    private String host;

    @Value("${redis.port:6379}")
    private int port;

    @Value("${redis.password:}")
    private String password;

    @Value("${redis.database:0}")
    private int database;

    private JedisPool jedisPool;

    public void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 可以添加更多连接池配置
        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password, database);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port);
        }
    }

    public Jedis getResource() {
        return jedisPool.getResource();
    }

    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close(); // Jedis 3.x+ 版本使用 close() 来返回连接到池中
        }
    }

    public String get(String key) {
        try (Jedis jedis = getResource()) {
            return jedis.get(key);
        }
    }

    public void set(String key, String value) {
        try (Jedis jedis = getResource()) {
            jedis.set(key, value);
        }
    }

    public void setex(String key, int seconds, String value) {
        try (Jedis jedis = getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public Boolean exists(String key) {
        try (Jedis jedis = getResource()) {
            return jedis.exists(key);
        }
    }

    public Long del(String... keys) {
        try (Jedis jedis = getResource()) {
            return jedis.del(keys);
        }
    }

    public void destroy() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(int database) {
        this.database = database;
    }
}