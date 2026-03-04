# Web 开发

NanoBoot 提供了一个基于嵌入式 HTTP 服务器的综合 Web 框架。它支持 REST API、请求映射、参数绑定和 JSON 序列化，开箱即用。

## Web 开发入门

### 控制器类

使用 `@Controller` 注解创建 Web 端点：

```java
import org.nanoboot.annotation.Annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 控制器方法
}
```

### 请求映射

使用各种注解将 HTTP 请求映射到控制器方法：

```java
@Controller
@RequestMapping("/api/users")  // 此控制器所有方法的基准路径
public class UserController {

    @GetMapping("/{id}")  // 映射到 GET /api/users/{id}
    public String getUser(@PathVariable Long id) {
        return userService.findById(id).toJson();
    }

    @PostMapping  // 映射到 POST /api/users
    public String createUser(@RequestBody UserDto userDto) {
        return userService.create(userDto).toJson();
    }

    @PutMapping("/{id}")  // 映射到 PUT /api/users/{id}
    public String updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.update(id, userDto).toJson();
    }

    @DeleteMapping("/{id}")  // 映射到 DELETE /api/users/{id}
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "{\"success\": true}";
    }
}
```

## HTTP 方法注解

NanoBoot 为不同的 HTTP 方法提供了便捷的注解：

### GET 请求
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

### POST 请求
```java
@PostMapping("/users")
public String createUser(@RequestBody CreateUserRequest request) {
    User user = userService.createUser(request);
    return user.toJson();
}
```

### PUT 请求
```java
@PutMapping("/users/{id}")
public String updateUser(
    @PathVariable Long id,
    @RequestBody UpdateUserRequest request) {
    User updated = userService.updateUser(id, request);
    return updated.toJson();
}
```

### DELETE 请求
```java
@DeleteMapping("/users/{id}")
public String deleteUser(@PathVariable Long id) {
    boolean deleted = userService.deleteUser(id);
    return "{\"deleted\": " + deleted + "}";
}
```

## 参数绑定

### 路径变量
使用 `@PathVariable` 绑定 URI 模板变量：

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public String getOrder(
    @PathVariable Long userId,
    @PathVariable Long orderId) {
    return orderService.getOrder(userId, orderId).toJson();
}
```

### 请求参数
使用 `@RequestParam` 绑定查询参数：

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

### 请求体
使用 `@RequestBody` 将请求体绑定到对象：

```java
@PostMapping("/users")
public String createUser(@RequestBody CreateUserRequest request) {
    User user = userService.create(request);
    return user.toJson();
}
```

### 请求头
使用 `@RequestHeader` 访问 HTTP 请求头：

```java
@GetMapping("/api/status")
public String getStatus(@RequestHeader("User-Agent") String userAgent) {
    return "{\"status\": \"ok\", \"client\": \"" + userAgent + "\"}";
}
```

## JSON 序列化

NanoBoot 自动将返回值序列化为 JSON 用于 Web 响应。对象会转换为 JSON 格式：

```java
@RestController  // @Controller 的替代方案，用于 JSON API
public class ApiController {

    @GetMapping("/api/data")
    public Map<String, Object> getData() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "healthy");
        return response;  // 自动序列化为 JSON
    }
}
```

## 响应处理

### 自定义响应状态
返回适当的 HTTP 状态码：

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

### 错误处理
优雅地处理异常：

```java
@ControllerAdvice  // 全局异常处理器（如果可用）
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity.notFound(e.getMessage());
    }
}
```

## 高级 Web 功能

### 跨域资源共享（CORS）
根据需要配置 CORS（实现依赖）：

```java
@CrossOrigin(origins = "*")  // 为此端点启用 CORS
@GetMapping("/api/public")
public String getPublicData() {
    return publicDataService.getData();
}
```

### 表单数据
处理多部分表单数据：

```java
@PostMapping("/upload")
public String uploadFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("description") String description) {

    return fileUploadService.saveFile(file, description);
}
```

### 会话管理
管理会话（框架特定实现）：

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

## Web 配置

### 服务器配置
在 `application.properties` 中配置服务器属性：

```properties
# 服务器配置
server.port=8080
server.context-path=/api
server.max-connections=100
server.thread-pool-size=10
```

### 内容协商
处理不同的内容类型：

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

## 最佳实践

### 1. 使用适当的 HTTP 方法
- GET：检索数据
- POST：创建新资源
- PUT：更新现有资源
- DELETE：删除资源
- PATCH：部分更新

### 2. 设计 RESTful URL
- 使用名词而非动词
- 集合使用复数名词
- 使用 HTTP 方法表示动作
- 保持一致的 URL 结构

### 3. 优雅地处理错误
- 返回适当的 HTTP 状态码
- 提供有意义的错误消息
- 记录错误以便调试

### 4. 验证输入
- 验证请求参数和请求体
- 清理用户输入
- 实现正确的错误响应

### 5. 使用 DTO
- 为 API 输入/输出创建单独的类
- 不要直接暴露内部域对象
- 适当验证 DTO

## 安全考虑

### 输入验证
始终验证和清理用户输入以防止注入攻击。

### 身份验证
为受保护的资源实施适当的身份验证机制。

### 限流
考虑实施限流以防止滥用。

NanoBoot 中的 Web 框架为构建 REST API 和 Web 应用程序提供了坚实的基础，具有熟悉的 Spring 式注解和约定。
