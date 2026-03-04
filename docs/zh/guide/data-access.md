# 数据访问

NanoBoot 通过数据模块提供强大的数据访问功能。它支持关系型数据库（MySQL）和 NoSQL 数据库（Redis），并可选集成 MyBatis 作为 ORM 框架。

## 设置数据访问

### 添加依赖项

要使用数据访问功能，请将数据模块添加到您的 `pom.xml`：

```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-data</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- 您可能需要的其他依赖项 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

### 配置

在 `application.properties` 中配置您的数据源：

```properties
# MySQL 配置
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/mydb
database.mysql.username=root
database.mysql.password=password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver
database.mysql.maximumPoolSize=10
database.mysql.minimumIdle=2

# Redis 配置
redis.host=localhost
redis.port=6379
redis.timeout=2000
redis.pool.maxTotal=10
redis.pool.maxIdle=5
```

## MySQL 数据库集成

### 数据源配置

框架自动配置 MySQL 数据源：

```java
@Component
public class DatabaseConfig {

    @Value("${database.mysql.jdbcUrl}")
    private String jdbcUrl;

    @Value("${database.mysql.username}")
    private String username;

    @Value("${database.mysql.password}")
    private String password;

    @PostConstruct
    public void init() {
        // 配置自动进行
        // 数据源可通过依赖注入获取
    }
}
```

### 使用数据源

在您的服务中注入和使用数据源：

```java
@Service
public class UserService {

    @Autowired
    private DataSource dataSource;

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

    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
```

### 事务管理

对于事务管理，您可以使用事务帮助类：

```java
@Service
public class UserService {

    @Autowired
    private DataSource dataSource;

    public void createUserWithProfile(User user, Profile profile) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            // 插入用户
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

            // 插入个人资料
            String profileSql = "INSERT INTO profiles (user_id, bio) VALUES (?, ?)";
            try (PreparedStatement profileStmt = conn.prepareStatement(profileSql)) {
                profileStmt.setLong(1, user.getId());
                profileStmt.setString(2, profile.getBio());
                profileStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}
```

## Redis 集成

### Redis 客户端使用

框架提供了一个用于缓存和会话存储的 Redis 客户端：

```java
@Service
public class CacheService {

    @Autowired
    private RedisClient redisClient;

    public void setUserCache(String key, User user, int expireSeconds) {
        redisClient.setex(key, expireSeconds, user.toJson());
    }

    public User getUserFromCache(String key) {
        String json = redisClient.get(key);
        if (json != null) {
            return User.fromJson(json);
        }
        return null;
    }

    public void invalidateUserCache(String userId) {
        redisClient.del("user:" + userId);
    }

    public void setUserListCache(List<User> users) {
        redisClient.setex("all_users", 300, users.toJson()); // 5 分钟
    }

    public List<User> getUserListFromCache() {
        String json = redisClient.get("all_users");
        if (json != null) {
            return User.listFromJson(json);
        }
        return null;
    }
}
```

### 使用 Redis 进行会话管理

使用 Redis 进行会话存储：

```java
@Service
public class SessionService {

    @Autowired
    private RedisClient redisClient;

    private static final int SESSION_TIMEOUT = 3600; // 1 小时

    public void createSession(String sessionId, Map<String, Object> sessionData) {
        redisClient.setex("session:" + sessionId, SESSION_TIMEOUT,
                         new ObjectMapper().writeValueAsString(sessionData));
    }

    public Map<String, Object> getSession(String sessionId) {
        String sessionJson = redisClient.get("session:" + sessionId);
        if (sessionJson != null) {
            return new ObjectMapper().readValue(sessionJson, Map.class);
        }
        return null;
    }

    public void extendSession(String sessionId) {
        redisClient.expire("session:" + sessionId, SESSION_TIMEOUT);
    }

    public void destroySession(String sessionId) {
        redisClient.del("session:" + sessionId);
    }
}
```

## MyBatis 集成（可选）

如果您更喜欢使用 MyBatis 进行 ORM，框架提供了集成：

### MyBatis 配置

```java
@Component
public class MyBatisConfig {

    @Autowired
    private MySQLDataSource dataSource;

    @PostConstruct
    public void configure() {
        // MyBatis 配置自动进行
        // SQL 映射从 resources/mybatis/ 加载
    }
}
```

### 映射器接口

```java
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User selectById(Long id);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User selectByEmail(String email);

    @Insert("INSERT INTO users(name, email, created_at) VALUES(#{name}, #{email}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET name = #{name}, email = #{email} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(Long id);

    @Select("SELECT * FROM users LIMIT #{offset}, #{limit}")
    List<User> selectRange(@Param("offset") int offset, @Param("limit") int limit);
}
```

### 使用映射器

```java
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    public User create(User user) {
        userMapper.insert(user);
        return user;
    }

    public User update(User user) {
        userMapper.update(user);
        return user;
    }

    public boolean delete(Long id) {
        return userMapper.delete(id) > 0;
    }

    public List<User> findAll(int page, int size) {
        int offset = page * size;
        return userMapper.selectRange(offset, size);
    }
}
```

## 仓库模式

您可以实现仓库模式以获得更清晰的数据访问：

```java
@Repository  // 假设 @Repository 可用或使用 @Component
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
            throw new RuntimeException("Error finding user by id", e);
        }
    }

    public List<User> findByActive(boolean active) {
        String sql = "SELECT * FROM users WHERE active = ? ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, active);
            ResultSet rs = stmt.executeQuery();

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding active users", e);
        }
    }

    public void save(User user) {
        if (user.getId() == null) {
            insert(user);
        } else {
            update(user);
        }
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
            throw new RuntimeException("Error inserting user", e);
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
            throw new RuntimeException("Error updating user", e);
        }
    }
}
```

## 最佳实践

### 1. 连接管理
- 始终使用 try-with-resources 进行适当的连接清理
- 利用连接池（HikariCP 自动配置）
- 不要长时间持有连接

### 2. 防止 SQL 注入
- 始终使用预编译语句
- 永远不要将用户输入直接拼接到 SQL 中
- 验证和清理所有输入

### 3. 事务管理
- 对相关操作使用事务
- 正确处理回滚场景
- 保持事务尽可能短

### 4. 缓存策略
- 使用 Redis 缓存频繁访问的数据
- 实现适当的缓存失效
- 考虑缓存过期时间

### 5. 错误处理
- 处理数据库连接失败
- 为临时故障实施重试逻辑
- 记录数据库错误以便监控

## 性能考虑

### 连接池
框架使用 HikariCP 进行高效的连接池管理。根据应用程序的负载配置池大小。

### 查询优化
- 适当使用索引
- 使用 EXPLAIN 优化查询
- 对大型数据集使用分页

### 缓存策略
- 缓存读取密集型数据
- 实现缓存旁路模式
- 使用适当的 TTL 值

NanoBoot 的数据访问模块提供了灵活的数据库集成选项，无论您喜欢直接 JDBC、MyBatis 还是其他 ORM 解决方案，同时提供内置的 Redis 支持用于缓存和会话管理。
