# 示例和教程

实用的示例和分步教程，帮助您学习和有效使用 NanoBoot 框架。

## 入门示例

### Hello World 应用程序

让我们创建一个简单的"Hello World"应用程序：

```java
// 主应用程序类
@NanoBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        System.out.println("正在启动 Hello World 应用程序...");
        NanoBootApplicationRunner.run(HelloWorldApplication.class, args);
    }
}
```

```java
// Hello World 端点的控制器
@Controller
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/hello/{name}")
    public String sayHelloTo(@PathVariable String name) {
        return "Hello, " + name + "!";
    }
}
```

运行应用程序并访问 `http://localhost:8080/hello` 查看 "Hello, World!"。

### REST API 示例

创建一个简单的用户管理 API：

```java
// 主应用程序
@NanoBootApplication
public class UserApiApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(UserApiApplication.class, args);
    }
}
```

```java
// 用户模型
public class User {
    private Long id;
    private String name;
    private String email;

    // 构造函数、getter 和 setter
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getter 和 setter...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

```java
// 服务层
@Service
public class UserService {

    private List<User> users = new ArrayList<>();
    private Long nextId = 1L;

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getUserById(Long id) {
        return users.stream()
                   .filter(user -> user.getId().equals(id))
                   .findFirst()
                   .orElse(null);
    }

    public User createUser(String name, String email) {
        User user = new User(name, email);
        user.setId(nextId++);
        users.add(user);
        return user;
    }

    public User updateUser(Long id, String name, String email) {
        User existingUser = getUserById(id);
        if (existingUser != null) {
            existingUser.setName(name);
            existingUser.setEmail(email);
        }
        return existingUser;
    }

    public boolean deleteUser(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
}
```

```java
// REST 控制器
@RestController  // @RestController 结合了 @Controller 和 @ResponseBody
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public User createUser(@RequestBody Map<String, String> requestBody) {
        String name = requestBody.get("name");
        String email = requestBody.get("email");
        return userService.createUser(name, email);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
        @PathVariable Long id,
        @RequestBody Map<String, String> requestBody) {

        String name = requestBody.get("name");
        String email = requestBody.get("email");

        User updatedUser = userService.updateUser(id, name, email);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", deleted);

        if (deleted) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

### 配置示例

使用外部化配置：

```java
@Service
public class AppConfigService {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.port:8080}")
    private int serverPort;

    public String getAppInfo() {
        return String.format("%s v%s running on port %d", appName, appVersion, serverPort);
    }
}
```

application.properties 中的配置：
```properties
app.name=My REST API
app.version=2.1.0
server.port=9090
```

## 高级示例

### 数据库集成示例

使用直接 JDBC 与 MySQL 集成：

```java
@Service
public class DatabaseUserService {

    @Autowired
    private DataSource dataSource;

    public User findById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                return user;
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
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
            return users;
        }
    }

    public User save(User user) throws SQLException {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) throws SQLException {
        String sql = "INSERT INTO users(name, email, created_at) VALUES(?, ?, NOW())";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
            return user;
        }
    }

    private User update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setLong(3, user.getId());
            stmt.executeUpdate();

            return user;
        }
    }
}
```

### WebSocket 聊天示例

创建实时聊天应用程序：

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    private static Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        sessions.add(session);
        logger.info("新聊天会话已打开: {}", session.getId());

        // 发送欢迎消息
        try {
            String welcomeMsg = "{\"type\":\"system\",\"message\":\"欢迎来到聊天！\",\"timestamp\":" +
                               System.currentTimeMillis() + "}";
            session.sendMessage(welcomeMsg);
        } catch (IOException e) {
            logger.error("发送欢迎消息时出错", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到来自 {} 的消息: {}", session.getId(), message);

        try {
            // 解析传入消息
            ObjectMapper mapper = new ObjectMapper();
            JsonNode msgNode = mapper.readTree(message);

            String msgType = msgNode.get("type").asText();
            String content = msgNode.get("content").asText();
            String user = msgNode.has("user") ? msgNode.get("user").asText() : "匿名";

            // 创建响应消息
            String response = String.format(
                "{\"type\":\"chat\",\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                user, content, System.currentTimeMillis()
            );

            // 广播给所有其他会话
            broadcastToOthers(response, session);
        } catch (Exception e) {
            logger.error("处理消息时出错", e);

            try {
                String errorMsg = "{\"type\":\"error\",\"message\":\"无效的消息格式\"}";
                session.sendMessage(errorMsg);
            } catch (IOException ioEx) {
                logger.error("发送错误消息时出错", ioEx);
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
        logger.info("聊天会话已关闭: {} 原因: {}", session.getId(), closeReason.getReasonPhrase());

        // 通知其他人用户离开
        try {
            String leaveMsg = String.format(
                "{\"type\":\"system\",\"message\":\"用户已离开聊天\",\"timestamp\":%d}",
                System.currentTimeMillis()
            );
            broadcastToOthers(leaveMsg, session);
        } catch (IOException e) {
            logger.error("通知会话关闭时出错", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("聊天会话 {} 中出错", session.getId(), throwable);
        sessions.remove(session);
    }

    private void broadcastToOthers(String message, Session excludeSession) {
        Iterator<Session> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            Session s = iterator.next();
            if (s.isOpen() && !s.equals(excludeSession)) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    logger.error("广播到会话 {} 时出错", s.getId(), e);
                    iterator.remove(); // 移除损坏的会话
                }
            }
        }
    }
}
```

### 使用 Redis 缓存示例

使用 Redis 进行缓存：

```java
@Service
public class CachedUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisClient redisClient;

    private static final int USER_CACHE_TTL = 300; // 5 分钟
    private static final String USER_CACHE_KEY = "user:%d";

    public User findUserWithCache(Long id) {
        String cacheKey = String.format(USER_CACHE_KEY, id);

        // 首先尝试从缓存获取
        String cachedUserJson = redisClient.get(cacheKey);
        if (cachedUserJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(cachedUserJson, User.class);
            } catch (Exception e) {
                // 缓存未命中或数据无效
            }
        }

        // 缓存未命中 - 从数据库获取
        User user = userService.findById(id);
        if (user != null) {
            // 存储在缓存中
            try {
                String userJson = new ObjectMapper().writeValueAsString(user);
                redisClient.setex(cacheKey, USER_CACHE_TTL, userJson);
            } catch (Exception e) {
                // 忽略缓存存储错误
            }
        }

        return user;
    }

    public void invalidateUserCache(Long id) {
        String cacheKey = String.format(USER_CACHE_KEY, id);
        redisClient.del(cacheKey);
    }
}
```

## 分步教程

### 教程 1：创建博客 API

**步骤 1：** 使用 CLI 创建新项目：
```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api
```

**步骤 2：** 导航到项目目录并在 IDE 中打开它。

**步骤 3：** 创建 Post 模型：
```java
public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // 构造函数、getter 和 setter
    // ...
}
```

**步骤 4：** 创建 Post 服务：
```java
@Service
public class PostService {
    private List<Post> posts = new ArrayList<>();
    private Long nextId = 1L;

    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    public Post getPostById(Long id) {
        return posts.stream()
                   .filter(post -> post.getId().equals(id))
                   .findFirst()
                   .orElse(null);
    }

    public Post createPost(String title, String content, String author) {
        Post post = new Post();
        post.setId(nextId++);
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setUpdatedAt(post.getCreatedAt());
        posts.add(post);
        return post;
    }

    // 更新、删除等其他方法
}
```

**步骤 5：** 创建 REST 控制器：
```java
@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Post createPost(@RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        String content = requestBody.get("content");
        String author = requestBody.get("author");
        return postService.createPost(title, content, author);
    }
}
```

**步骤 6：** 运行应用程序并测试端点。

### 教程 2：添加数据库支持

**步骤 1：** 在 pom.xml 中添加数据模块依赖：
```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-data</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**步骤 2：** 在 application.properties 中配置数据库连接：
```properties
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/blog_db
database.mysql.username=blog_user
database.mysql.password=secure_password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver
database.mysql.maximumPoolSize=20
```

**步骤 3：** 更新服务以使用数据库：
```java
@Service
public class DatabasePostService {

    @Autowired
    private DataSource dataSource;

    public List<Post> getAllPosts() throws SQLException {
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                posts.add(mapRowToPost(rs));
            }
            return posts;
        }
    }

    public Post getPostById(Long id) throws SQLException {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRowToPost(rs);
            }
            return null;
        }
    }

    private Post mapRowToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setAuthor(rs.getString("author"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setUpdatedAt(rs.getTimestamp("updated_at"));
        return post;
    }
}
```

## 常见模式和最佳实践

### 服务层模式
```java
@Service
public class BusinessService {

    @Autowired
    private Repository repository;

    @Autowired
    private ExternalApiService externalApi;

    @Transactional
    public Result performBusinessOperation(Input input) {
        // 验证输入
        if (input == null || input.isValid()) {
            throw new IllegalArgumentException("无效的输入");
        }

        // 执行操作
        Entity entity = transformInput(input);
        Entity savedEntity = repository.save(entity);

        // 如需要调用外部服务
        externalApi.notify(savedEntity);

        // 返回结果
        return createResult(savedEntity);
    }
}
```

### 异常处理
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse error = new ErrorResponse("BUSINESS_ERROR", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("发生意外错误", e);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "发生意外错误");
        return ResponseEntity.status(500).body(error);
    }
}
```

这些示例展示了 NanoBoot 开发的各个方面，从简单的应用程序到更复杂的功能，包括数据库、缓存和实时通信。
