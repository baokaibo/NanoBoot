# Data Access

NanoBoot provides robust data access capabilities through the data module. It supports relational databases (MySQL) and NoSQL databases (Redis), with optional integration for MyBatis as an ORM framework.

## Setting Up Data Access

### Adding Dependencies

To use data access features, add the data module to your `pom.xml`:

```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-data</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Additional dependencies you might need -->
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

### Configuration

Configure your data sources in `application.properties`:

```properties
# MySQL Configuration
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/mydb
database.mysql.username=root
database.mysql.password=password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver
database.mysql.maximumPoolSize=10
database.mysql.minimumIdle=2

# Redis Configuration
redis.host=localhost
redis.port=6379
redis.timeout=2000
redis.pool.maxTotal=10
redis.pool.maxIdle=5
```

## MySQL Database Integration

### DataSource Configuration

The framework automatically configures a MySQL DataSource:

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
        // Configuration happens automatically
        // DataSource is available via dependency injection
    }
}
```

### Using DataSource

Inject and use the DataSource in your services:

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

### Transaction Management

For transaction management, you can use a transaction helper:

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

## Redis Integration

### Redis Client Usage

The framework provides a Redis client for caching and session storage:

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
        redisClient.setex("all_users", 300, users.toJson()); // 5 minutes
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

### Session Management with Redis

Using Redis for session storage:

```java
@Service
public class SessionService {

    @Autowired
    private RedisClient redisClient;

    private static final int SESSION_TIMEOUT = 3600; // 1 hour

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

## MyBatis Integration (Optional)

If you prefer using MyBatis for ORM, the framework provides integration:

### MyBatis Configuration

```java
@Component
public class MyBatisConfig {

    @Autowired
    private MySQLDataSource dataSource;

    @PostConstruct
    public void configure() {
        // MyBatis configuration happens automatically
        // SQL mappings are loaded from resources/mybatis/
    }
}
```

### Mapper Interface

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

### Using the Mapper

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

## Repository Pattern

You can implement the repository pattern for cleaner data access:

```java
@Repository  // Assuming @Repository is available or use @Component
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

## Best Practices

### 1. Connection Management
- Always use try-with-resources for proper connection cleanup
- Leverage connection pooling (HikariCP configured automatically)
- Don't hold connections longer than necessary

### 2. SQL Injection Prevention
- Always use prepared statements
- Never concatenate user input directly into SQL
- Validate and sanitize all inputs

### 3. Transaction Management
- Use transactions for related operations
- Handle rollback scenarios properly
- Keep transactions as short as possible

### 4. Caching Strategy
- Use Redis for frequently accessed data
- Implement proper cache invalidation
- Consider cache expiration times

### 5. Error Handling
- Handle database connection failures
- Implement retry logic for transient failures
- Log database errors for monitoring

## Performance Considerations

### Connection Pooling
The framework uses HikariCP for efficient connection pooling. Configure pool sizes based on your application's load.

### Query Optimization
- Use indexes appropriately
- Optimize queries with EXPLAIN
- Use pagination for large datasets

### Cache Strategies
- Cache read-heavy data
- Implement cache-aside pattern
- Use appropriate TTL values

The data access module in NanoBoot provides flexible options for database integration, whether you prefer direct JDBC, MyBatis, or other ORM solutions, while providing built-in Redis support for caching and session management.