# Web Module

The web module provides HTTP server functionality and web development capabilities for NanoBoot applications. It includes request routing, parameter binding, and REST API support.

## Overview

The web module implements a lightweight HTTP server that can handle web requests and responses. It provides Spring MVC-like functionality with familiar annotations for building web applications and REST APIs.

## Key Features

### Embedded HTTP Server

The module includes an embedded HTTP server that starts automatically:

```java
@NanoBootApplication
public class WebAppApplication {

    public static void main(String[] args) {
        NanoBootApplicationRunner.run(WebAppApplication.class, args);
        // HTTP server starts automatically on configured port
    }
}
```

### Request Routing

Route HTTP requests using annotations:

```java
@Controller
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return user.toJson();
    }

    @PostMapping
    public String createUser(@RequestBody CreateUserRequest request) {
        User user = userService.create(request);
        return user.toJson();
    }
}
```

### Parameter Binding

Bind request parameters to method arguments:

```java
@GetMapping("/search")
public String searchUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String q) {

    Page<User> users = userService.search(q, page, size);
    return users.toJson();
}
```

## HTTP Method Support

The web module supports standard HTTP methods:

### GET Requests
```java
@GetMapping("/users")
public String getAllUsers() {
    return userService.getAllUsers().toJson();
}

@GetMapping("/users/{id}")
public String getUser(@PathVariable String id) {
    return userService.getUser(id).toJson();
}
```

### POST Requests
```java
@PostMapping("/users")
public String createUser(@RequestBody UserDto userDto) {
    User user = userService.create(userDto);
    return user.toJson();
}
```

### PUT Requests
```java
@PutMapping("/users/{id}")
public String updateUser(
    @PathVariable Long id,
    @RequestBody UserDto userDto) {

    User updated = userService.update(id, userDto);
    return updated.toJson();
}
```

### DELETE Requests
```java
@DeleteMapping("/users/{id}")
public String deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return "{\"success\": true}";
}
```

## Advanced Routing

### Path Variables
```java
@GetMapping("/users/{userId}/orders/{orderId}")
public String getOrder(
    @PathVariable("userId") Long userId,
    @PathVariable("orderId") Long orderId) {

    return orderService.getOrder(userId, orderId).toJson();
}
```

### Request Parameters
```java
@GetMapping("/users")
public String getUsers(
    @RequestParam("page") int page,
    @RequestParam(value = "size", defaultValue = "20") int size,
    @RequestParam(required = false) String sort) {

    return userService.getUsers(page, size, sort).toJson();
}
```

### Request Body
```java
@PostMapping("/users/batch")
public String createUsers(@RequestBody List<UserDto> users) {
    List<User> createdUsers = userService.createBatch(users);
    return createdUsers.toJson();
}
```

### Request Headers
```java
@GetMapping("/api/version")
public String getVersion(@RequestHeader("User-Agent") String userAgent) {
    return "{\"version\": \"1.0.0\", \"userAgent\": \"" + userAgent + "\"}";
}
```

## Controller Patterns

### REST Controller
```java
@RestController  // Shorthand for @Controller + @ResponseBody
public class ApiController {

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
```

### Request Scope
```java
@Controller
@Scope("request")  // New instance per request
public class RequestScopedController {

    private final String requestId = UUID.randomUUID().toString();

    @GetMapping("/request-id")
    public String getRequestId() {
        return "{\"requestId\": \"" + requestId + "\"}";
    }
}
```

## Request Processing Pipeline

### Handler Mapping
```java
// Internal structure (for understanding)
public class HandlerMapping {
    private Map<RequestMethod, Map<String, HandlerMethod>> mappings = new HashMap<>();

    public HandlerMethod getHandler(String path, RequestMethod method) {
        Map<String, HandlerMethod> methodMappings = mappings.get(method);
        return methodMappings != null ? methodMappings.get(path) : null;
    }
}
```

### Parameter Resolution
The web module resolves method parameters based on annotations:

```java
public class ParameterResolver {

    public Object resolveParameter(Parameter param, HttpServletRequest request) {
        if (param.isAnnotationPresent(RequestBody.class)) {
            return parseRequestBody(request);
        } else if (param.isAnnotationPresent(RequestParam.class)) {
            return resolveRequestParam(param, request);
        } else if (param.isAnnotationPresent(PathVariable.class)) {
            return resolvePathVariable(param, request);
        }
        // Other parameter types...
        return null;
    }
}
```

## Response Handling

### JSON Serialization
```java
@GetMapping("/api/data")
public List<DataItem> getData() {
    return dataService.getItems();
    // Automatically serialized to JSON
}

@GetMapping("/api/stats")
public Map<String, Object> getStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", userService.count());
    stats.put("totalOrders", orderService.count());
    return stats;
    // Automatically serialized to JSON
}
```

### Custom Responses
```java
@GetMapping("/api/download")
public ResponseEntity<byte[]> downloadFile() {
    byte[] fileContent = fileService.getFile();

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/octet-stream");
    headers.put("Content-Disposition", "attachment; filename=data.zip");

    return new ResponseEntity<>(fileContent, headers, 200);
}
```

### Error Responses
```java
@GetMapping("/api/sensitive")
public String getSensitiveData(@RequestHeader("Authorization") String auth) {
    if (!isValidToken(auth)) {
        throw new UnauthorizedException("Invalid token");
    }
    return sensitiveDataService.getData();
}
```

## Configuration

### Server Configuration

Configure the web server in `application.properties`:

```properties
# Server configuration
server.port=8080
server.host=localhost
server.context-path=/
server.max-connections=100
server.thread-pool-size=10
server.request-timeout=30000
server.response-timeout=30000

# SSL Configuration (if supported)
server.ssl.enabled=false
server.ssl.port=8443
server.ssl.keystore-path=path/to/keystore
server.ssl.keystore-password=password
```

### CORS Configuration
```java
@Configuration
public class WebConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

## Middleware and Interceptors

### Request Interceptor
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("Request: " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("Response: " + response.getStatus());
    }
}
```

### Global Exception Handler
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(404).body("{\"error\": \"" + e.getMessage() + "\"}");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidation(ValidationException e) {
        return ResponseEntity.status(400).body("{\"error\": \"" + e.getMessage() + "\"}");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception e) {
        System.err.println("Unexpected error: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
    }
}
```

## Static Resources

Serve static resources like CSS, JS, and images:

```java
@Configuration
public class WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```

## Best Practices

### 1. RESTful Design
- Use nouns instead of verbs in URLs
- Use HTTP methods to indicate actions
- Maintain consistent URL structure
- Use proper HTTP status codes

### 2. Error Handling
- Return appropriate HTTP status codes
- Provide meaningful error messages
- Log errors for debugging

### 3. Input Validation
- Validate request parameters and bodies
- Sanitize user input
- Implement proper error responses

### 4. Security
- Validate authentication headers
- Implement rate limiting
- Sanitize output for XSS protection

### 5. Performance
- Use appropriate response caching
- Implement request/response compression
- Optimize for mobile clients

## Advanced Features

### Async Processing
```java
@GetMapping("/api/async")
public CompletableFuture<String> processAsync() {
    return CompletableFuture.supplyAsync(() -> {
        // Perform long-running operation
        return "{\"result\": \"processed asynchronously\"}";
    });
}
```

### File Upload
```java
@PostMapping("/api/upload")
public String uploadFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("description") String description) {

    String fileId = fileService.save(file, description);
    return "{\"fileId\": \"" + fileId + "\", \"size\": " + file.getSize() + "}";
}
```

The web module provides a complete HTTP server and web development platform that enables developers to build REST APIs and web applications with familiar Spring-like annotations and conventions.