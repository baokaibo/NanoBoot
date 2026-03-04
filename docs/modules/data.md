# Data Module

The data module provides database connectivity and data access capabilities for NanoBoot applications. It includes support for MySQL, Redis, and optional MyBatis integration.

## Overview

The data module implements a comprehensive data access layer that supports both relational and NoSQL databases. It provides:

- MySQL database connectivity with connection pooling
- Redis integration for caching and session management
- MyBatis support for SQL mapping (optional)
- Configuration management for data sources

## Key Features

### MySQL Integration

The data module provides a configurable MySQL DataSource with connection pooling:

```java
@Component
public class UserService {

    @Autowired
    private DataSource dataSource;  // Auto-configured MySQL DataSource

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

### Redis Integration

Integrated Redis client for caching and data storage:

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

### MyBatis Support

Optional MyBatis integration for ORM:

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

## Configuration

### MySQL Configuration

Configure MySQL settings in `application.properties`:

```properties
# MySQL Configuration
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/myapp
database.mysql.username=root
database.mysql.password=password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver

# Connection Pool Settings
database.mysql.maximumPoolSize=20
database.mysql.minimumIdle=5
database.mysql.connectionTimeout=30000
database.mysql.idleTimeout=600000
database.mysql.maxLifetime=1800000
database.mysql.leakDetectionThreshold=60000

# Pool Behavior
database.mysql.autoCommit=true
database.mysql.poolName=MyAppHikariPool
database.mysql.dataSource.cachePrepStmts=true
database.mysql.dataSource.prepStmtCacheSize=250
database.mysql.dataSource.prepStmtCacheSqlLimit=2048
```

### Redis Configuration

Configure Redis settings in `application.properties`:

```properties
# Redis Configuration
redis.host=localhost
redis.port=6379
redis.timeout=2000
redis.database=0
redis.password=

# Connection Pool
redis.pool.maxTotal=20
redis.pool.maxIdle=10
redis.pool.minIdle=2
redis.pool.maxWaitMillis=3000
redis.pool.testOnBorrow=true
redis.pool.testOnReturn=false
redis.pool.testWhileIdle=true
```

## MySQL Integration Details

### DataSource Configuration

The module automatically configures a HikariCP DataSource:

```java
@Configuration
public class DataSourceConfig {

    @Value("${database.mysql.jdbcUrl}")
    private String jdbcUrl;

    @Value("${database.mysql.username}")
    private String username;

    @Value("${database.mysql.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Connection pool settings
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
```

### Transaction Management

For transaction management, you can implement transactional services:

```java
@Service
@Transactional
public class UserService {

    @Autowired
    private DataSource dataSource;

    @Transactional
    public void createUserWithProfile(User user, UserProfile profile) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);

            // Insert user
            String userSql = "INSERT INTO users (name, email) VALUES (?, ?)";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, user.getName());
                userStmt.setString(2, user.getEmail());
                userStmt.executeUpdate();

                ResultSet rs = userStmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }

            // Insert profile
            String profileSql = "INSERT INTO user_profiles (user_id, bio, avatar_url) VALUES (?, ?, ?)";
            try (PreparedStatement profileStmt = conn.prepareStatement(profileSql)) {
                profileStmt.setLong(1, user.getId());
                profileStmt.setString(2, profile.getBio());
                profileStmt.setString(3, profile.getAvatarUrl());
                profileStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

## Redis Integration Details

### Redis Client

The module provides a Redis client wrapper:

```java
@Component
public class RedisClient {

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(20);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);

        this.jedisPool = new JedisPool(poolConfig, "localhost", 6379, 2000);
    }

    @PreDestroy
    public void destroy() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        }
    }

    public String setex(String key, int seconds, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.setex(key, seconds, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        }
    }

    public Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        }
    }

    public Long del(String... keys) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(keys);
        }
    }
}
```

### Redis Operations

Perform various Redis operations:

```java
@Service
public class RedisOperations {

    @Autowired
    private RedisClient redisClient;

    // String operations
    public void setString(String key, String value, int ttl) {
        redisClient.setex(key, ttl, value);
    }

    public String getString(String key) {
        return redisClient.get(key);
    }

    // Hash operations
    public void setHash(String key, String field, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, field, value);
        }
    }

    public String getHash(String key, String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        }
    }

    // List operations
    public void pushToList(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.lpush(key, value);
        }
    }

    public List<String> getListRange(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        }
    }

    // Set operations
    public void addToSet(String key, String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd(key, member);
        }
    }

    public Set<String> getSetMembers(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        }
    }
}
```

## MyBatis Integration

### Configuration

Enable MyBatis support by configuring the SqlSessionFactory:

```java
@Configuration
@ConditionalOnClass(SqlSessionFactory.class)
public class MyBatisConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeAliasesPackage("com.example.entity");

        // Add mapper locations
        Resource[] mapperResources = getResourcePatternResolver()
            .getResources("classpath*:mapper/*.xml");
        sessionFactory.setMapperLocations(mapperResources);

        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

### Mapper Interface

Create MyBatis mapper interfaces:

```java
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Insert("INSERT INTO users(name, email, created_at) VALUES(#{name}, #{email}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT * FROM users ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    List<User> selectRange(@Param("offset") int offset, @Param("limit") int limit);

    // Dynamic SQL with XML mapping
    List<User> selectByCriteria(@Param("criteria") UserSearchCriteria criteria);
}
```

### XML Mapper

Create XML mapper files (optional):

```xml
<!-- resources/mapper/UserMapper.xml -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.mapper.UserMapper">

    <select id="selectByCriteria" resultType="com.example.entity.User">
        SELECT * FROM users
        <where>
            <if test="criteria.name != null and criteria.name != ''">
                AND name LIKE CONCAT('%', #{criteria.name}, '%')
            </if>
            <if test="criteria.email != null and criteria.email != ''">
                AND email = #{criteria.email}
            </if>
            <if test="criteria.active != null">
                AND active = #{criteria.active}
            </if>
        </where>
        ORDER BY created_at DESC
    </select>

</mapper>
```

## Data Access Patterns

### Repository Pattern

Implement the repository pattern for clean data access:

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

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all users", e);
        }
    }

    public User save(User user) {
        if (user.getId() == null) {
            insert(user);
        } else {
            update(user);
        }
        return user;
    }

    private void insert(User user) {
        String sql = "INSERT INTO users(name, email, active, created_at) VALUES(?, ?, ?, NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setBoolean(3, user.isActive());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user", e);
        }
    }

    private void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, active = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setBoolean(3, user.isActive());
            stmt.setLong(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating user", e);
        }
    }
}
```

## Caching Strategies

### Application-level Caching

Combine database and Redis caching:

```java
@Service
public class CachedUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisClient redisClient;

    private static final int CACHE_TTL_SECONDS = 300; // 5 minutes
    private static final String USER_CACHE_PREFIX = "user:";

    public User findById(Long id) {
        String cacheKey = USER_CACHE_PREFIX + id;

        // Try to get from cache first
        String cachedUser = redisClient.get(cacheKey);
        if (cachedUser != null) {
            return JsonUtils.fromJson(cachedUser, User.class);
        }

        // Load from database
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            // Cache the result
            redisClient.setex(cacheKey, CACHE_TTL_SECONDS, JsonUtils.toJson(user.get()));
            return user.get();
        }

        return null;
    }

    public void update(User user) {
        // Save to database
        userRepository.save(user);

        // Update cache
        String cacheKey = USER_CACHE_PREFIX + user.getId();
        redisClient.setex(cacheKey, CACHE_TTL_SECONDS, JsonUtils.toJson(user));
    }

    public void delete(Long id) {
        // Delete from database
        userRepository.deleteById(id);

        // Invalidate cache
        String cacheKey = USER_CACHE_PREFIX + id;
        redisClient.del(cacheKey);
    }
}
```

## Best Practices

### 1. Connection Management
- Always use try-with-resources for proper cleanup
- Leverage connection pooling (HikariCP)
- Close connections promptly
- Monitor pool metrics

### 2. SQL Security
- Always use prepared statements
- Never concatenate user input directly
- Validate and sanitize inputs
- Implement parameter validation

### 3. Transaction Handling
- Use transactions for related operations
- Handle rollback scenarios
- Keep transactions short
- Consider isolation levels

### 4. Caching Strategy
- Cache frequently accessed data
- Implement proper cache invalidation
- Use appropriate TTL values
- Monitor cache hit rates

### 5. Error Handling
- Handle connection failures gracefully
- Implement retry logic
- Log database errors
- Use circuit breaker pattern for resilience

## Performance Considerations

### Connection Pooling
The data module uses HikariCP for efficient connection management. Configure pool sizes based on your application's needs.

### Query Optimization
- Use indexes appropriately
- Implement pagination for large datasets
- Optimize queries with EXPLAIN
- Consider read replicas for read-heavy operations

### Caching
- Implement multi-level caching (application + Redis)
- Use cache-aside pattern for read operations
- Implement write-through for critical data
- Consider cache warming strategies

The data module provides a comprehensive solution for data access needs, supporting both traditional SQL databases and modern NoSQL solutions with integrated caching capabilities.