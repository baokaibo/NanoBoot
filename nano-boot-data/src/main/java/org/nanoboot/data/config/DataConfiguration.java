package org.nanoboot.data.config;

import org.nanoboot.annotation.Annotation.Autowired;
import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.PostConstruct;
import org.nanoboot.data.datasource.MySQLDataSource;
import org.nanoboot.data.redis.RedisClient;
import org.nanoboot.data.mybatis.MyBatisConfiguration;

import javax.annotation.PreDestroy;

/**
 * 数据访问配置类，负责初始化数据源和相关组件
 */
@Component
public class DataConfiguration {

    @Autowired(required = false)
    private MySQLDataSource mysqlDataSource;

    @Autowired(required = false)
    private RedisClient redisClient;

    @Autowired(required = false)
    private MyBatisConfiguration myBatisConfig;

    @PostConstruct
    public void init() {
        try {
            // 初始化Redis连接池（如果存在）
            if (redisClient != null) {
                redisClient.init();
            }

            // 配置MyBatis（如果存在）
            if (myBatisConfig != null && mysqlDataSource != null) {
                myBatisConfig.configure(mysqlDataSource);
            }

            System.out.println("Data access components initialized successfully.");
        } catch (Exception e) {
            System.err.println("Warning: Error initializing data access components: " + e.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        // 关闭Redis连接池（如果存在）
        if (redisClient != null) {
            try {
                redisClient.destroy();
            } catch (Exception e) {
                System.err.println("Error destroying Redis client: " + e.getMessage());
            }
        }
        System.out.println("Data access components destroyed successfully.");
    }
}