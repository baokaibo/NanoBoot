# Examples and Tutorials

Practical examples and step-by-step tutorials to help you learn and use the NanoBoot framework effectively.

## Getting Started Examples

### Hello World Application

Let's create a simple "Hello World" application to get started:

```java
// Main application class
@NanoBootApplication
public class HelloWorldApplication {

    public static void main(String[] args) {
        System.out.println("Starting Hello World Application...");
        NanoBootApplicationRunner.run(HelloWorldApplication.class, args);
    }
}
```

```java
// Controller for the hello world endpoint
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

Run the application and visit `http://localhost:8080/hello` to see "Hello, World!".

### REST API Example

Create a simple user management API:

```java
// Main application
@NanoBootApplication
public class UserApiApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(UserApiApplication.class, args);
    }
}
```

```java
// User model
public class User {
    private Long id;
    private String name;
    private String email;

    // Constructors, getters, and setters
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
```

```java
// Service layer
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
// REST controller
@RestController  // @RestController combines @Controller and @ResponseBody
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

### Configuration Example

Use externalized configuration:

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

Configuration in `application.properties`:
```properties
app.name=My REST API
app.version=2.1.0
server.port=9090
```

## Advanced Examples

### Database Integration Example

Integrate with MySQL using direct JDBC:

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

### WebSocket Chat Example

Create a real-time chat application:

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    private static Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Logger logger = LoggerFactory.getLogger(ChatEndpoint.class);

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        sessions.add(session);
        logger.info("New chat session opened: {}", session.getId());

        // Send welcome message
        try {
            String welcomeMsg = "{\"type\":\"system\",\"message\":\"Welcome to the chat!\",\"timestamp\":" +
                               System.currentTimeMillis() + "}";
            session.sendMessage(welcomeMsg);
        } catch (IOException e) {
            logger.error("Error sending welcome message", e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("Received message from {}: {}", session.getId(), message);

        try {
            // Parse the incoming message
            ObjectMapper mapper = new ObjectMapper();
            JsonNode msgNode = mapper.readTree(message);

            String msgType = msgNode.get("type").asText();
            String content = msgNode.get("content").asText();
            String user = msgNode.has("user") ? msgNode.get("user").asText() : "Anonymous";

            // Create response message
            String response = String.format(
                "{\"type\":\"chat\",\"user\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
                user, content, System.currentTimeMillis()
            );

            // Broadcast to all other sessions
            broadcastToOthers(response, session);
        } catch (Exception e) {
            logger.error("Error processing message", e);

            try {
                String errorMsg = "{\"type\":\"error\",\"message\":\"Invalid message format\"}";
                session.sendMessage(errorMsg);
            } catch (IOException ioEx) {
                logger.error("Error sending error message", ioEx);
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        sessions.remove(session);
        logger.info("Chat session closed: {} Reason: {}", session.getId(), closeReason.getReasonPhrase());

        // Notify others about user leaving
        try {
            String leaveMsg = String.format(
                "{\"type\":\"system\",\"message\":\"A user left the chat\",\"timestamp\":%d}",
                System.currentTimeMillis()
            );
            broadcastToOthers(leaveMsg, session);
        } catch (IOException e) {
            logger.error("Error notifying about session close", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Error in chat session {}", session.getId(), throwable);
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
                    logger.error("Error broadcasting to session {}", s.getId(), e);
                    iterator.remove(); // Remove broken session
                }
            }
        }
    }
}
```

### Caching with Redis Example

Use Redis for caching:

```java
@Service
public class CachedUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisClient redisClient;

    private static final int USER_CACHE_TTL = 300; // 5 minutes
    private static final String USER_CACHE_KEY = "user:%d";

    public User findUserWithCache(Long id) {
        String cacheKey = String.format(USER_CACHE_KEY, id);

        // Try to get from cache first
        String cachedUserJson = redisClient.get(cacheKey);
        if (cachedUserJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(cachedUserJson, User.class);
            } catch (Exception e) {
                // Cache miss or invalid data
            }
        }

        // Cache miss - get from database
        User user = userService.findById(id);
        if (user != null) {
            // Store in cache
            try {
                String userJson = new ObjectMapper().writeValueAsString(user);
                redisClient.setex(cacheKey, USER_CACHE_TTL, userJson);
            } catch (Exception e) {
                // Ignore cache storage errors
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

## Step-by-Step Tutorials

### Tutorial 1: Creating a Blog API

**Step 1:** Create a new project using the CLI:
```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api
```

**Step 2:** Navigate to the project directory and open it in your IDE.

**Step 3:** Create a Post model:
```java
public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors, getters, and setters
    // ...
}
```

**Step 4:** Create a Post service:
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

    // Additional methods for update, delete, etc.
}
```

**Step 5:** Create a REST controller:
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

**Step 6:** Run the application and test the endpoints.

### Tutorial 2: Adding Database Support

**Step 1:** Add the data module dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-data</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**Step 2:** Configure database connection in `application.properties`:
```properties
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/blog_db
database.mysql.username=blog_user
database.mysql.password=secure_password
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver
database.mysql.maximumPoolSize=20
```

**Step 3:** Update your service to use the database:
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

## Common Patterns and Best Practices

### Service Layer Pattern
```java
@Service
public class BusinessService {

    @Autowired
    private Repository repository;

    @Autowired
    private ExternalApiService externalApi;

    @Transactional
    public Result performBusinessOperation(Input input) {
        // Validate input
        if (input == null || input.isValid()) {
            throw new IllegalArgumentException("Invalid input");
        }

        // Perform operation
        Entity entity = transformInput(input);
        Entity savedEntity = repository.save(entity);

        // Call external services if needed
        externalApi.notify(savedEntity);

        // Return result
        return createResult(savedEntity);
    }
}
```

### Exception Handling
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
        log.error("Unexpected error occurred", e);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(500).body(error);
    }
}
```

These examples demonstrate various aspects of NanoBoot development, from simple applications to more complex features involving databases, caching, and real-time communication.