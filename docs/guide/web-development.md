# Web Development

NanoBoot provides a comprehensive web framework built on top of an embedded HTTP server. It supports REST APIs, request mapping, parameter binding, and JSON serialization out of the box.

## Getting Started with Web Development

### Controller Classes

Create web endpoints using the `@Controller` annotation:

```java
import org.nanoboot.annotation.Annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // Controller methods go here
}
```

### Request Mapping

Map HTTP requests to controller methods using various annotations:

```java
@Controller
@RequestMapping("/api/users")  // Base path for all methods in this controller
public class UserController {

    @GetMapping("/{id}")  // Maps to GET /api/users/{id}
    public String getUser(@PathVariable Long id) {
        return userService.findById(id).toJson();
    }

    @PostMapping  // Maps to POST /api/users
    public String createUser(@RequestBody UserDto userDto) {
        return userService.create(userDto).toJson();
    }

    @PutMapping("/{id}")  // Maps to PUT /api/users/{id}
    public String updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto).toJson();
    }

    @DeleteMapping("/{id}")  // Maps to DELETE /api/users/{id}
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "{\"success\": true}";
    }
}
```

## HTTP Method Annotations

NanoBoot provides convenient annotations for different HTTP methods:

### GET Requests
```java
@GetMapping("/users")
public String getAllUsers() {
    return userService.findAll().toJson();
}

@GetMapping("/users/{id}")
public String getUser(@PathVariable String id) {
    return userService.findById(id).toJson();
}
```

### POST Requests
```java
@PostMapping("/users")
public String createUser(@RequestBody CreateUserRequest request) {
    User user = userService.createUser(request);
    return user.toJson();
}
```

### PUT Requests
```java
@PutMapping("/users/{id}")
public String updateUser(
    @PathVariable Long id,
    @RequestBody UpdateUserRequest request) {
    User updated = userService.updateUser(id, request);
    return updated.toJson();
}
```

### DELETE Requests
```java
@DeleteMapping("/users/{id}")
public String deleteUser(@PathVariable Long id) {
    boolean deleted = userService.deleteUser(id);
    return "{\"deleted\": " + deleted + "}";
}
```

## Parameter Binding

### Path Variables
Use `@PathVariable` to bind URI template variables:

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public String getOrder(
    @PathVariable Long userId,
    @PathVariable Long orderId) {
    return orderService.getOrder(userId, orderId).toJson();
}
```

### Request Parameters
Use `@RequestParam` to bind query parameters:

```java
@GetMapping("/users")
public String getUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String search) {

    Page<User> users = userService.findUsers(page, size, search);
    return users.toJson();
}
```

### Request Body
Use `@RequestBody` to bind the request body to an object:

```java
@PostMapping("/users")
public String createUser(@RequestBody CreateUserRequest request) {
    User user = userService.create(request);
    return user.toJson();
}
```

### Headers
Access HTTP headers with `@RequestHeader`:

```java
@GetMapping("/api/status")
public String getStatus(@RequestHeader("User-Agent") String userAgent) {
    return "{\"status\": \"ok\", \"client\": \"" + userAgent + "\"}";
}
```

## JSON Serialization

NanoBoot automatically serializes return values to JSON for web responses. Objects are converted to JSON format:

```java
@RestController  // Alternative to @Controller for JSON APIs
public class ApiController {

    @GetMapping("/api/data")
    public Map<String, Object> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "healthy");
        return response;  // Automatically serialized to JSON
    }
}
```

## Response Handling

### Custom Response Status
Return appropriate HTTP status codes:

```java
@PostMapping("/users")
public ResponseEntity<String> createUser(@RequestBody UserDto user) {
    try {
        User created = userService.create(user);
        return ResponseEntity.ok(created.toJson());
    } catch (ValidationException e) {
        return ResponseEntity.badRequest(e.getMessage());
    }
}
```

### Error Handling
Handle exceptions gracefully:

```java
@ControllerAdvice  // Global exception handler (if available)
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.notFound(e.getMessage());
    }
}
```

## Advanced Web Features

### Cross-Origin Resource Sharing (CORS)
Configure CORS if needed (implementation-dependent):

```java
@CrossOrigin(origins = "*")  // Enable CORS for this endpoint
@GetMapping("/api/public")
public String getPublicData() {
    return publicDataService.getData();
}
```

### Form Data
Handle multipart form data:

```java
@PostMapping("/upload")
public String uploadFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("description") String description) {

    return fileUploadService.saveFile(file, description);
}
```

### Session Management
Manage sessions (framework-specific implementation):

```java
@GetMapping("/dashboard")
public String dashboard(HttpSession session) {
    String userId = (String) session.getAttribute("userId");
    if (userId == null) {
        return "{\"error\": \"Not authenticated\"}";
    }
    return userService.getDashboardData(userId);
}
```

## Web Configuration

### Server Configuration
Configure server properties in `application.properties`:

```properties
# Server configuration
server.port=8080
server.context-path=/api
server.max-connections=100
server.thread-pool-size=10
```

### Content Negotiation
Handle different content types:

```java
@PostMapping(value = "/users", consumes = "application/json")
public String createUserJson(@RequestBody UserDto user) {
    return userService.create(user).toJson();
}

@PostMapping(value = "/users", consumes = "application/xml")
public String createUserXml(@RequestBody String xml) {
    User user = XmlUtils.fromXml(xml);
    return userService.create(user).toJson();
}
```

## Best Practices

### 1. Use Proper HTTP Methods
- GET: Retrieve data
- POST: Create new resources
- PUT: Update existing resources
- DELETE: Remove resources
- PATCH: Partial updates

### 2. Design RESTful URLs
- Use nouns instead of verbs
- Use plural nouns for collections
- Use HTTP methods to indicate action
- Maintain consistent URL structure

### 3. Handle Errors Gracefully
- Return appropriate HTTP status codes
- Provide meaningful error messages
- Log errors for debugging

### 4. Validate Input
- Validate request parameters and bodies
- Sanitize user input
- Implement proper error responses

### 5. Use DTOs
- Create separate classes for API input/output
- Don't expose internal domain objects directly
- Validate DTOs appropriately

## Security Considerations

### Input Validation
Always validate and sanitize user input to prevent injection attacks.

### Authentication
Implement proper authentication mechanisms for protected resources.

### Rate Limiting
Consider implementing rate limiting to prevent abuse.

The web framework in NanoBoot provides a solid foundation for building REST APIs and web applications with familiar Spring-like annotations and conventions.