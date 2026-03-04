# Data 模块

Data 模块为 NanoBoot 应用程序提供数据库连接和数据访问能力。它包括对 MySQL、Redis 的支持以及可选的 MyBatis 集成。

## 概述

Data 模块实现了一个综合的数据访问层，支持关系型数据库和 NoSQL 数据库。它提供：

- MySQL 数据库连接与连接池
- Redis 集成用于缓存和会话管理
- MyBatis 支持用于 SQL 映射（可选）
- 数据源的配置管理

## 关键功能

### MySQL 集成

Data 模块提供可配置的 MySQL 数据源，带有连接池：

```java
@Component
public class UserService {

    @Autowired
    private DataSource dataSource;  // 自动配置的 MySQL 数据源

    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }
            return null;
        }
    }
}
```

### Redis 集成

集成 Redis 客户端用于缓存和数据存储：

```java
@Service
public class CacheService {

    @Autowired
    private RedisClient redisClient;

    public void setCache(String key, Object value, int ttlSeconds) {
        redisClient.setex(key, ttlSeconds, serialize(value));
    }

    public <T> T getFromCache(String key, Class<T> type) {
        String value = redisClient.get(key);
        return value != null ? deserialize(value, type) : null;
    }
}
```

### MyBatis 支持

可选的 MyBatis 集成用于 ORM：

```java
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);

    @Insert("INSERT INTO users(name, email) VALUES(#{name}, #{email})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
```

## 配置

### MySQL 配置

在 application.properties 中配置 MySQL 设置：

```properties
# MySQL 配置
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/myapp
database.mysql.username=root
database.mysql.password=password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver

# 连接池设置
database.mysql.maximumPoolSize=20
database.mysql.minimumIdle=5
database.mysql.connectionTimeout=30000
database.mysql.idleTimeout=600000
database.mysql.maxLifetime=1800000
database.mysql.leakDetectionThreshold=60000
```

### Redis 配置

在 application.properties 中配置 Redis 设置：

```properties
# Redis 配置
redis.host=localhost
redis.port=6379
redis.timeout=2000
redis.database=0
redis.password=

# 连接池
redis.pool.maxTotal=20
redis.pool.maxIdle=10
redis.pool.minIdle=2
redis.pool.maxWaitMillis=3000
redis.pool.testOnBorrow=true
redis.pool.testOnReturn=false
redis.pool.testWhileIdle=true
```

## 数据访问模式

### 仓库模式

实现仓库模式以获得更清晰的数据访问：

```java
@Repository
public class UserRepository {

    @Autowired
    private DataSource dataSource;

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user by id", e);
        }
    }
}
```

## 最佳实践

### 1. 连接管理
- 始终使用 try-with-resources 进行适当的清理
- 利用连接池（HikariCP）
- 及时关闭连接

### 2. SQL 安全
- 始终使用预编译语句
- 永远不要直接拼接用户输入

### 3. 事务处理
- 对相关操作使用事务
- 保持事务简短

### 4. 缓存策略
- 缓存频繁访问的数据
- 实现适当的缓存失效
- 使用适当的 TTL 值

### 5. 错误处理
- 优雅地处理连接失败
- 实施重试逻辑
- 记录数据库错误

Data 模块为数据访问需求提供了全面的解决方案，支持传统 SQL 数据库和现代 NoSQL 解决方案，并具有集成的缓存能力。
