# API Reference

This section provides detailed reference documentation for the core APIs and annotations available in the NanoBoot framework.

## Annotations

### Core Annotations

#### @NanoBootApplication
Marks the main application class and enables framework features.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan
@EnableConfigurationProperties
public @interface NanoBootApplication {
    // Configuration attributes
}
```

**Usage:**
```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

#### @Component
Indicates that an annotated class is a "component". Such classes are service layer components, and will be auto-detected and registered as beans.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";  // Optional name for the bean
}
```

**Usage:**
```java
@Component
public class UserService {
    // Service implementation
}

@Component("customUserService")  // Named component
public class CustomUserService {
    // Custom service implementation
}
```

#### @Service
Stereotype for indicating that an annotated class is a service component. This is a specialization of @Component.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
    String value() default "";
}
```

**Usage:**
```java
@Service
public class UserService {
    // Business logic implementation
}
```

#### @Controller
Stereotype for indicating that an annotated class is a web controller. This is a specialization of @Component.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Controller {
    String value() default "";
}
```

**Usage:**
```java
@Controller
public class UserController {
    // Web endpoint methods
}
```

### Dependency Injection Annotations

#### @Autowired
Marks a constructor, field, setter method or config method as to be autowired by the Spring container.

```java
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}
```

**Usage:**
```java
@Service
public class OrderService {

    @Autowired  // Field injection
    private UserService userService;

    @Autowired  // Constructor injection
    public OrderService(UserService userService) {
        this.userService = userService;
    }

    @Autowired  // Setter injection
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

#### @Value
Annotation at the field or method/constructor parameter level that indicates a default value expression for the affected argument.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}
```

**Usage:**
```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${server.port:8080}")
    private int port;

    @Value("${database.url}")
    private String databaseUrl;
}
```

#### @Qualifier
This annotation may be used on a field or parameter as a qualifier for candidate beans when autowiring.

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    String value() default "";
}
```

**Usage:**
```java
@Component("primaryDataSource")
public class PrimaryDataSource {
    // Implementation
}

@Component("secondaryDataSource")
public class SecondaryDataSource {
    // Implementation
}

@Service
public class DataService {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;
}
```

### Lifecycle Annotations

#### @PostConstruct
The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
    // No attributes
}
```

**Usage:**
```java
@Component
public class DatabaseInitializer {

    @PostConstruct
    public void init() {
        // Initialization code executed after dependencies are injected
        System.out.println("Database initialized");
    }
}
```

#### @PreDestroy
The PreDestroy annotation is used on methods as a callback notification to signal that the instance is in the process of being removed by the container.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
    // No attributes
}
```

**Usage:**
```java
@Component
public class ConnectionPoolManager {

    @PreDestroy
    public void cleanup() {
        // Cleanup code executed before bean destruction
        System.out.println("Cleaning up resources");
    }
}
```

### Web Annotations

#### @RequestMapping
Provides mapping for HTTP request paths.

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String[] value() default {};
    RequestMethod[] method() default {};
    String[] produces() default {};
    String[] consumes() default {};
}
```

**Usage:**
```java
@Controller
@RequestMapping("/api/users")  // Base mapping for all methods
public class UserController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listUsers() {
        return "user-list";
    }
}
```

#### @GetMapping
Convenience annotation that is itself annotated with @RequestMapping(method = RequestMethod.GET).

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping {
    String[] value() default {};
    String[] produces() default {};
}
```

**Usage:**
```java
@RestController
public class UserController {

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

#### @PostMapping
Convenience annotation that is itself annotated with @RequestMapping(method = RequestMethod.POST).

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.POST)
public @interface PostMapping {
    String[] value() default {};
    String[] consumes() default {};
    String[] produces() default {};
}
```

**Usage:**
```java
@RestController
public class UserController {

    @PostMapping("/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }
}
```

#### @PutMapping
Convenience annotation for PUT requests.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.PUT)
public @interface PutMapping {
    String[] value() default {};
    String[] consumes() default {};
    String[] produces() default {};
}
```

#### @DeleteMapping
Convenience annotation for DELETE requests.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(method = RequestMethod.DELETE)
public @interface DeleteMapping {
    String[] value() default {};
}
```

### Parameter Binding Annotations

#### @PathVariable
Annotation which indicates that a method parameter should be bound to a URI template variable.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value() default "";
    boolean required() default true;
}
```

**Usage:**
```java
@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(
    @PathVariable Long userId,
    @PathVariable("orderId") Long id) {
    return orderService.get(userId, id);
}
```

#### @RequestParam
Annotation which indicates that a method parameter should be bound to a web request parameter.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
    boolean required() default true;
    String defaultValue() default "";
}
```

**Usage:**
```java
@GetMapping("/users")
public List<User> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String search) {
    return userService.search(search, page, size);
}
```

#### @RequestBody
Annotation indicating a method parameter should be bound to the body of the web request.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
    boolean required() default true;
}
```

**Usage:**
```java
@PostMapping("/users")
public User createUser(@RequestBody CreateUserRequest request) {
    return userService.create(request);
}
```

#### @RequestHeader
Annotation which indicates that a method parameter should be bound to a web request header.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {
    String value() default "";
    boolean required() default true;
    String defaultValue() default "";
}
```

**Usage:**
```java
@GetMapping("/api/status")
public String getStatus(@RequestHeader("User-Agent") String userAgent) {
    return "{\"status\": \"ok\", \"client\": \"" + userAgent + "\"}";
}
```

### WebSocket Annotations

#### @ServerEndpoint
An annotation which is used to decorate a Java class that is an implementation of a WebSocket endpoint.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerEndpoint {
    String value();
    String[] subprotocols() default {};
    String[] configurator() default {};
}
```

**Usage:**
```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        // Connection opened
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Received message
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // Connection closed
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Error occurred
    }
}
```

## Core Classes

### NanoBootApplicationRunner
Main class responsible for running NanoBoot applications.

```java
public class NanoBootApplicationRunner {

    /**
     * Runs the application with the given main class and arguments
     * @param mainClass the main application class
     * @param args application arguments
     */
    public static void run(Class<?> mainClass, String[] args);

    /**
     * Runs the application with a custom application context
     * @param mainClass the main application class
     * @param args application arguments
     * @param applicationContext custom application context
     */
    public static void runWithCustomContext(Class<?> mainClass, String[] args,
                                          ApplicationContext applicationContext);
}
```

### ApplicationContext
Central interface to provide configuration for an application.

```java
public class ApplicationContext {

    /**
     * Retrieves a bean of the specified type
     * @param type the type of the bean to retrieve
     * @param <T> the type of the bean
     * @return the bean instance
     * @throws NoSuchBeanDefinitionException if no bean of the given type is found
     */
    public <T> T getBean(Class<T> type);

    /**
     * Retrieves a bean with the specified name
     * @param name the name of the bean to retrieve
     * @return the bean instance
     * @throws NoSuchBeanDefinitionException if no bean with the given name is found
     */
    public Object getBean(String name);

    /**
     * Retrieves a bean of the specified type with the specified name
     * @param name the name of the bean to retrieve
     * @param type the type of the bean to retrieve
     * @param <T> the type of the bean
     * @return the bean instance
     */
    public <T> T getBean(String name, Class<T> type);

    /**
     * Checks if a bean with the specified name exists
     * @param name the name of the bean to check
     * @return true if the bean exists, false otherwise
     */
    public boolean containsBean(String name);

    /**
     * Checks if a bean of the specified type exists
     * @param type the type of the bean to check
     * @return true if the bean exists, false otherwise
     */
    public boolean containsBean(Class<?> type);
}
```

### Session (WebSocket)
Interface representing a WebSocket session.

```java
public interface Session {

    /**
     * Gets the unique identifier for this session
     * @return the session ID
     */
    String getId();

    /**
     * Sends a text message to the connected client
     * @param text the text message to send
     * @throws IOException if an I/O error occurs
     */
    void sendMessage(String text) throws IOException;

    /**
     * Sends a binary message to the connected client
     * @param data the binary data to send
     * @throws IOException if an I/O error occurs
     */
    void sendMessage(ByteBuffer data) throws IOException;

    /**
     * Checks if the session is still open
     * @return true if the session is open, false otherwise
     */
    boolean isOpen();

    /**
     * Closes the session
     * @throws IOException if an I/O error occurs
     */
    void close() throws IOException;

    /**
     * Gets the query string from the opening handshake
     * @return the query string, or null if none was provided
     */
    String getQueryString();

    /**
     * Gets user-defined properties associated with this session
     * @return a map of user properties
     */
    Map<String, Object> getUserProperties();
}
```

## Configuration Properties

### Server Configuration
Properties for configuring the HTTP server:

- `server.port`: The port number to listen on (default: 8080)
- `server.host`: The host address to bind to (default: localhost)
- `server.context-path`: The base path for all endpoints (default: /)
- `server.max-connections`: Maximum concurrent connections (default: 100)
- `server.thread-pool-size`: Size of the thread pool (default: 10)

### Database Configuration
Properties for database connections:

- `database.mysql.jdbcUrl`: JDBC URL for MySQL
- `database.mysql.username`: Database username
- `database.mysql.password`: Database password
- `database.mysql.driverClassName`: Driver class name
- `database.mysql.maximumPoolSize`: Maximum connection pool size
- `database.mysql.minimumIdle`: Minimum idle connections

### Redis Configuration
Properties for Redis connections:

- `redis.host`: Redis server host (default: localhost)
- `redis.port`: Redis server port (default: 6379)
- `redis.timeout`: Connection timeout in milliseconds
- `redis.pool.maxTotal`: Maximum total connections
- `redis.pool.maxIdle`: Maximum idle connections
- `redis.pool.minIdle`: Minimum idle connections

### WebSocket Configuration
Properties for WebSocket settings:

- `websocket.enabled`: Whether WebSocket is enabled (default: true)
- `websocket.path.prefix`: Prefix for WebSocket endpoints (default: /websocket)
- `websocket.max.text.message.buffer.size`: Max text message buffer size
- `websocket.max.session.idle.timeout`: Session idle timeout in milliseconds
- `websocket.connection.limits.per.ip`: Max connections per IP address

This API reference provides comprehensive documentation for all the core components and annotations available in the NanoBoot framework.