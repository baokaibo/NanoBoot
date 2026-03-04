# Web 模块

Web 模块为 NanoBoot 应用程序提供 HTTP 服务器功能和 Web 开发能力。它包括请求路由、参数绑定和 REST API 支持。

## 概述

Web 模块实现了一个轻量级 HTTP 服务器，可以处理 Web 请求和响应。它提供类似 Spring MVC 的功能，使用熟悉的注解构建 Web 应用程序和 REST API。

## 关键功能

### 嵌入式 HTTP 服务器

该模块包含一个嵌入式 HTTP 服务器，自动启动：

```java
@NanoBootApplication
public class WebAppApplication {

    public static void main(String[] args) {
        NanoBootApplicationRunner.run(WebAppApplication.class, args);
        // HTTP 服务器在配置的端口上自动启动
    }
}
```

### 请求路由

使用注解路由 HTTP 请求：

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

### 参数绑定

将请求参数绑定到方法参数：

```java
public String
@GetMapping("/searchUsers(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(required = false) String q) {

    Page<User> users = userService.search(q, page, size);
    return users.toJson();
}
```

## HTTP 方法支持

Web 模块支持标准 HTTP 方法：

### GET 请求
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

### POST 请求
```java
@PostMapping("/users")
public String createUser(@RequestBody UserDto userDto) {
    User user = userService.create(userDto);
    return user.toJson();
}
```

### PUT 请求
```java
@PutMapping("/users/{id}")
public String updateUser(
    @PathVariable Long id,
    @RequestBody UserDto userDto) {

    User updated = userService.update(id, userDto);
    return updated.toJson();
}
```

### DELETE 请求
```java
@DeleteMapping("/users/{id}")
public String deleteUser(@PathVariable Long id) {
    userService.delete(id);
    return "{\"success\": true}";
}
```

## 高级路由

### 路径变量
```java
@GetMapping("/users/{userId}/orders/{orderId}")
public String getOrder(
    @PathVariable("userId") Long userId,
    @PathVariable("orderId") Long orderId) {

    return orderService.getOrder(userId, orderId).toJson();
}
```

### 请求参数
```java
@GetMapping("/users")
public String getUsers(
    @RequestParam("page") int page,
    @RequestParam(value = "size", defaultValue = "20") int size,
    @RequestParam(required = false) String sort) {

    return userService.getUsers(page, size, sort).toJson();
}
```

### 请求体
```java
@PostMapping("/users/batch")
public String createUsers(@RequestBody List<UserDto> users) {
    List<User> createdUsers = userService.createBatch(users);
    return createdUsers.toJson();
}
```

### 请求头
```java
@GetMapping("/api/version")
public String getVersion(@RequestHeader("User-Agent") String userAgent) {
    return "{\"version\": \"1.0.0\", \"userAgent\": \"" + userAgent + "\"}";
}
```

## 控制器模式

### REST 控制器
```java
@RestController  // @Controller + @ResponseBody 的简写
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

### 请求作用域
```java
@Controller
@Scope("request")  // 每个请求一个新实例
public class RequestScopedController {

    private final String requestId = UUID.randomUUID().toString();

    @GetMapping("/request-id")
    public String getRequestId() {
        return "{\"requestId\": \"" + requestId + "\"}";
    }
}
```

## 请求处理管道

### 处理器映射
```java
// 内部结构（用于理解）
public class HandlerMapping {
    private Map<RequestMethod, Map<String, HandlerMethod>> mappings = new HashMap<>();

    public HandlerMethod getHandler(String path, RequestMethod method) {
        Map<String, HandlerMethod> methodMappings = mappings.get(method);
        return methodMappings != null ? methodMappings.get(path) : null;
    }
}
```

### 参数解析
Web 模块根据注解解析方法参数：

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
        // 其他参数类型...
        return null;
    }
}
```

## 响应处理

### JSON 序列化
```java
@GetMapping("/api/data")
public List<DataItem> getData() {
    return dataService.getItems();
    // 自动序列化为 JSON
}

@GetMapping("/api/stats")
public Map<String, Object> getStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalUsers", userService.count());
    stats.put("totalOrders", orderService.count());
    return stats;
    // 自动序列化为 JSON
}
```

### 自定义响应
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

### 错误响应
```java
@GetMapping("/api/sensitive")
public String getSensitiveData(@RequestHeader("Authorization") String auth) {
    if (!isValidToken(auth)) {
        throw new UnauthorizedException("无效的令牌");
    }
    return sensitiveDataService.getData();
}
```

## 配置

### 服务器配置

在 `application.properties` 中配置 Web 服务器：

```properties
# 服务器配置
server.port=8080
server.host=localhost
server.context-path=/
server.max-connections=100
server.thread-pool-size=10
server.request-timeout=30000
server.response-timeout=30000

# SSL 配置（如果支持）
server.ssl.enabled=false
server.ssl.port=8443
server.ssl.keystore-path=path/to/keystore
server.ssl.keystore-password=password
```

### CORS 配置
```java
@Configuration
public class WebConfig {

    @Bean
   Filter() {
        public CorsFilter cors CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
```

## 中间件和拦截器

### 请求拦截器
```java
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("请求: " + request.getMethod() + " " + request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        System.out.println("响应: " + response.getStatus());
    }
}
```

### 全局异常处理器
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
        System.err.println("意外错误: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body("{\"error\": \"内部服务器错误\"}");
    }
}
```

## 静态资源

提供 CSS、JS 和图像等静态资源：

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

## 最佳实践

### 1. RESTful 设计
- 在 URL 中使用名词而非动词
- 使用 HTTP 方法表示动作
- 保持一致的 URL 结构
- 使用正确的 HTTP 状态码

### 2. 错误处理
- 返回适当的 HTTP 状态码
- 提供有意义的错误消息
- 记录错误以便调试

### 3. 输入验证
- 验证请求参数和请求体
- 清理用户输入
- 实现正确的错误响应

### 4. 安全
- 验证认证头
- 实施限流
- 清理输出以防止 XSS

### 5. 性能
- 使用适当的响应缓存
- 实施请求/响应压缩
- 为移动客户端优化

## 高级功能

### 异步处理
```java
@GetMapping("/api/async")
public CompletableFuture<String> processAsync() {
    return CompletableFuture.supplyAsync(() -> {
        // 执行长时间运行的操作
        return "{\"result\": \"异步处理完成\"}";
    });
}
```

### 文件上传
```java
@PostMapping("/api/upload")
public String uploadFile(
    @RequestParam("file") MultipartFile file,
    @RequestParam("description") String description) {

    String fileId = fileService.save(file, description);
    return "{\"fileId\": \"" + fileId + "\", \"size\": " + file.getSize() + "}";
}
```

Web 模块提供了一个完整的 HTTP 服务器和 Web 开发平台，使开发人员能够使用熟悉的 Spring 式注解和约定构建 REST API 和 Web 应用程序。
