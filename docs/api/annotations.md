# Annotations

Detailed reference for all annotations available in the NanoBoot framework.

## Core Annotations

### @NanoBootApplication

Marks the main application class and enables framework features. This annotation should be applied to the main class of your NanoBoot application.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE (classes)

**Runtime retention**: Yes

#### Description

The `@NanoBootApplication` annotation is a convenience annotation that adds the following functionality:
- Enables component scanning in the same package and sub-packages as the main application class
- Enables configuration property processing
- Signals to the framework that this is the entry point of the application

#### Usage

```java
@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

#### Requirements

- Must be placed on the main application class
- The main application class must have a public static void main method
- Only one class per application should be annotated with this annotation

---

### @Component

Indicates that an annotated class is a "component". Such classes are service layer components, and will be auto-detected and registered as beans.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE (classes)

**Runtime retention**: Yes

#### Description

The `@Component` annotation marks a class as a component that should be managed by the dependency injection container. It's a generic stereotype for any Spring-managed component.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional name for the bean. If not specified, the bean name is derived from the class name following Spring's naming conventions. |

#### Usage

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

---

### @Service

Stereotype for indicating that an annotated class is a service component. This is a specialization of @Component.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE (classes)

**Runtime retention**: Yes

#### Description

The `@Service` annotation is a specialization of @Component for service layer components. While functionally equivalent to @Component in terms of functionality, it adds semantic meaning to your codebase.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional name for the bean. |

#### Usage

```java
@Service
public class UserService {
    // Business logic implementation
}

@Service("customUserService")
public class CustomUserService {
    // Custom service implementation
}
```

---

### @Controller

Stereotype for indicating that an annotated class is a web controller. This is a specialization of @Component.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE (classes)

**Runtime retention**: Yes

#### Description

The `@Controller` annotation is a specialization of @Component for web controllers. It marks a class as a controller that handles web requests.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional name for the bean. |

#### Usage

```java
@Controller
public class UserController {
    // Web endpoint methods
}

@Controller("customUserController")
public class CustomUserController {
    // Custom controller implementation
}
```

---

### @RestController

Convenience annotation that is itself annotated with `@Controller` and `@ResponseBody`.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE (classes)

**Runtime retention**: Yes

#### Description

The `@RestController` annotation is a convenience annotation that combines `@Controller` and `@ResponseBody`. It indicates that the class is a web controller where all handler methods return data directly rather than view names.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Optional name for the bean. |

#### Usage

```java
@RestController
public class ApiController {
    // All methods return data directly
    @GetMapping("/data")
    public List<DataItem> getData() {
        return dataService.getItems();
    }
}
```

---

## Dependency Injection Annotations

### @Autowired

Marks a constructor, field, setter method or config method as to be autowired by the container.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: CONSTRUCTOR, FIELD, METHOD, ANNOTATION_TYPE

**Runtime retention**: Yes

#### Description

The `@Autowired` annotation provides fine-grained control over where and how dependency injection should be accomplished. It can be used on constructors, fields, setter methods, or configuration methods.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | Specifies whether the dependency is required. If false, the container will try to satisfy the dependency, but won't throw an exception if it can't. |

#### Usage

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

---

### @Value

Annotation at the field or method/constructor parameter level that indicates a default value expression for the affected argument.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: FIELD, METHOD, PARAMETER, ANNOTATION_TYPE

**Runtime retention**: Yes

#### Description

The `@Value` annotation is used to inject values from properties or SpEL expressions into fields or parameters.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | None | The value expression to be injected. Can be a property placeholder like ${property.key} or a default value like ${property.key:default_value}. |

#### Usage

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")  // Property with default
    private String appName;

    @Value("${server.port:8080}")
    private int port;

    @Value("${database.url}")  // Required property
    private String databaseUrl;
}
```

---

### @Qualifier

This annotation may be used on a field or parameter as a qualifier for candidate beans when autowiring.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: FIELD, METHOD, PARAMETER, TYPE

**Runtime retention**: Yes

#### Description

The `@Qualifier` annotation can be used to disambiguate bean references when multiple candidates of the same type are available.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The name of the bean to inject. |

#### Usage

```java
@Component("primaryDataSource")
public class PrimaryDataSource { /* implementation */ }

@Component("secondaryDataSource")
public class SecondaryDataSource { /* implementation */ }

@Service
public class DataService {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;
}
```

---

## Lifecycle Annotations

### @PostConstruct

The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@PostConstruct` annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization. It's called after all dependencies have been injected.

#### Usage

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

---

### @PreDestroy

The PreDestroy annotation is used on methods as a callback notification to signal that the instance is in the process of being removed by the container.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@PreDestroy` annotation is used to signal that a method should be called before the bean is destroyed by the container.

#### Usage

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

---

## Web Annotations

### @RequestMapping

Provides mapping for HTTP request paths.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: TYPE, METHOD

**Runtime retention**: Yes

#### Description

The `@RequestMapping` annotation maps web requests onto specific handler methods. It can be applied to class-level or method-level handlers.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | The path mapping for the request. |
| `method` | `RequestMethod[]` | `{}` | The HTTP methods to map to. |
| `produces` | `String[]` | `{}` | Media types that the method can produce. |
| `consumes` | `String[]` | `{}` | Media types that the method can consume. |

#### Usage

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

---

### @GetMapping

Convenience annotation that is itself annotated with `@RequestMapping(method = RequestMethod.GET)`.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@GetMapping` annotation is a composed annotation that acts as a shortcut for `@RequestMapping(method = RequestMethod.GET)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | The path mapping for the request. |
| `produces` | `String[]` | `{}` | Media types that the method can produce. |

#### Usage

```java
@RestController
public class UserController {

    @GetMapping("/users")  // Equivalent to @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

---

### @PostMapping

Convenience annotation that is itself annotated with `@RequestMapping(method = RequestMethod.POST)`.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@PostMapping` annotation is a composed annotation that acts as a shortcut for `@RequestMapping(method = RequestMethod.POST)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | The path mapping for the request. |
| `consumes` | `String[]` | `{}` | Media types that the method can consume. |
| `produces` | `String[]` | `{}` | Media types that the method can produce. |

#### Usage

```java
@RestController
public class UserController {

    @PostMapping("/users")
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }
}
```

---

### @PutMapping

Convenience annotation that is itself annotated with `@RequestMapping(method = RequestMethod.PUT)`.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@PutMapping` annotation is a composed annotation that acts as a shortcut for `@RequestMapping(method = RequestMethod.PUT)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | The path mapping for the request. |
| `consumes` | `String[]` | `{}` | Media types that the method can consume. |
| `produces` | `String[]` | `{}` | Media types that the method can produce. |

#### Usage

```java
@RestController
public class UserController {

    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }
}
```

---

### @DeleteMapping

Convenience annotation that is itself annotated with `@RequestMapping(method = RequestMethod.DELETE)`.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: METHOD

**Runtime retention**: Yes

#### Description

The `@DeleteMapping` annotation is a composed annotation that acts as a shortcut for `@RequestMapping(method = RequestMethod.DELETE)`.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | The path mapping for the request. |

#### Usage

```java
@RestController
public class UserController {

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## Parameter Binding Annotations

### @PathVariable

Annotation which indicates that a method parameter should be bound to a URI template variable.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: PARAMETER

**Runtime retention**: Yes

#### Description

The `@PathVariable` annotation is used to bind a method parameter to a URI template variable.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The name of the path variable to bind to. |
| `required` | `boolean` | `true` | Whether the path variable is required. |

#### Usage

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(
    @PathVariable Long userId,  // Uses parameter name
    @PathVariable("orderId") Long id) {  // Explicit name mapping
    return orderService.get(userId, id);
}
```

---

### @RequestParam

Annotation which indicates that a method parameter should be bound to a web request parameter.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: PARAMETER

**Runtime retention**: Yes

#### Description

The `@RequestParam` annotation is used to bind a method parameter to a web request parameter (query parameter or form data).

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The name of the request parameter to bind to. |
| `required` | `boolean` | `true` | Whether the parameter is required. |
| `defaultValue` | `String` | `""` | Default value to use as a fallback. |

#### Usage

```java
@GetMapping("/users")
public List<User> getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String search) {
    return userService.search(search, page, size);
}
```

---

### @RequestBody

Annotation indicating a method parameter should be bound to the body of the web request.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: PARAMETER

**Runtime retention**: Yes

#### Description

The `@RequestBody` annotation indicates that a method parameter should be bound to the body of the web request and deserialized from JSON.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | Whether the request body is required. |

#### Usage

```java
@PostMapping("/users")
public User createUser(@RequestBody CreateUserRequest request) {
    return userService.create(request);
}
```

---

### @RequestHeader

Annotation which indicates that a method parameter should be bound to a web request header.

**Package**: `org.nanoboot.annotation.Annotation`

**Target**: PARAMETER

**Runtime retention**: Yes

#### Description

The `@RequestHeader` annotation is used to bind a method parameter to a web request header.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | The name of the header to bind to. |
| `required` | `boolean` | `true` | Whether the header is required. |
| `defaultValue` | `String` | `""` | Default value to use as a fallback. |

#### Usage

```java
@GetMapping("/api/status")
public String getStatus(@RequestHeader("User-Agent") String userAgent) {
    return "{\"status\": \"ok\", \"client\": \"" + userAgent + "\"}";
}
```

---

## WebSocket Annotations

### @ServerEndpoint

An annotation which is used to decorate a Java class that is an implementation of a WebSocket endpoint.

**Package**: `org.nanoboot.websocket.annotation`

**Target**: TYPE

**Runtime retention**: Yes

#### Description

The `@ServerEndpoint` annotation is used to mark a class as a WebSocket endpoint. It specifies the path at which the WebSocket will be available.

#### Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | `String` | None | The URI path at which the WebSocket endpoint will be deployed. |

#### Usage

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

---

This comprehensive reference provides detailed information about all the annotations available in the NanoBoot framework, their attributes, usage patterns, and examples.