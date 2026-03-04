# 注解

NanoBoot 框架中所有可用注解的详细参考。

## 核心注解

### @NanoBootApplication

标记主应用程序类并启用框架功能。此注解应应用于您的 NanoBoot 应用程序的主类。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE（类）

**运行时保留**: 是

#### 描述

`@NanoBootApplication` 注解是一个方便注解，添加了以下功能：
- 在主应用程序类所在的包和子包中启用组件扫描
- 启用配置属性处理
- 向框架指示这是应用程序的入口点

#### 用法

```java
@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

#### 要求

- 必须放在主应用程序类上
- 主应用程序类必须有 public static void main 方法
- 每个应用程序只能有一个类带有此注解

---

### @Component

表示带注解的类是一个"组件"。这些类将被自动检测并注册为 Bean。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE（类）

**运行时保留**: 是

#### 描述

`@Component` 注解将一个类标记为应由依赖注入容器管理的组件。它是任何 Spring 管理组件的通用原型注解。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean 的可选名称。如果未指定，Bean 名称将遵循 Spring 的命名约定从类名派生。 |

#### 用法

```java
@Component
public class UserService {
    // 服务实现
}

@Component("customUserService")  // 命名组件
public class CustomUserService {
    // 自定义服务实现
}
```

---

### @Service

用于表示带注解的类是服务组件的原型注解。这是 @Component 的特化。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE（类）

**运行时保留**: 是

#### 描述

`@Service` 注解是 @Component 在功能上等效的特化，为服务层组件添加了语义含义。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean 的可选名称。 |

#### 用法

```java
@Service
public class UserService {
    // 业务逻辑实现
}
```

---

### @Controller

用于表示带注解的类是 Web 控制器的原型注解。这是 @Component 的特化。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE（类）

**运行时保留**: 是

#### 描述

`@Controller` 注解是 @Component 的特化，用于 Web 控制器。它将一个类标记为处理 Web 请求的控制器。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | Bean 的可选名称。 |

#### 用法

```java
@Controller
public class UserController {
    // Web 端点方法
}
```

---

### @RestController

方便注解，本身带有 `@Controller` 和 `@ResponseBody`。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE（类）

**运行时保留**: 是

#### 描述

`@RestController` 注解是 `@Controller` 和 `@ResponseBody` 的组合注解。它表示该类是一个 Web 控制器，所有处理程序方法直接返回数据而不是视图名称。

#### 用法

```java
@RestController
public class ApiController {
    // 所有方法直接返回数据
    @GetMapping("/data")
    public List<DataItem> getData() {
        return dataService.getItems();
    }
}
```

---

## 依赖注入注解

### @Autowired

将构造函数、字段、setter 方法或配置方法标记为由容器自动装配。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: CONSTRUCTOR, FIELD, METHOD, ANNOTATION_TYPE

**运行时保留**: 是

#### 描述

`@Autowired` 注解提供了对依赖注入位置和方式的细粒度控制。它可以用于构造函数、字段、setter 方法或配置方法。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | 指定依赖是否必需。如果为 false，容器将尝试满足依赖，但如果无法满足不会抛出异常。 |

#### 用法

```java
@Service
public class OrderService {

    @Autowired  // 字段注入
    private UserService userService;

    @Autowired  // 构造函数注入
    public OrderService(UserService userService) {
        this.userService = userService;
    }

    @Autowired  // setter 注入
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

---

### @Value

在字段或方法/构造函数参数级别注解，表示受影响参数的默认值表达式。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: FIELD, METHOD, PARAMETER, ANNOTATION_TYPE

**运行时保留**: 是

#### 描述

`@Value` 注解用于将属性或 SpEL 表达式的值注入到字段或参数中。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | 无 | 要注入的值表达式。可以是属性占位符如 ${property.key} 或默认值如 ${property.key:default_value}。 |

#### 用法

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")  // 带默认值的属性
    private String appName;

    @Value("${server.port:8080}")
    private int port;

    @Value("${database.url}")  // 必需的属性
    private String databaseUrl;
}
```

---

### @Qualifier

此注解可在字段或参数上用作自动装配时候选 Bean 的限定符。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: FIELD, METHOD, PARAMETER, TYPE

**运行时保留**: 是

#### 描述

`@Qualifier` 注解可用于在有多个相同类型的候选 Bean 时消除 Bean 引用歧义。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | 要注入的 Bean 名称。 |

#### 用法

```java
@Component("primaryDataSource")
public class PrimaryDataSource { /* 实现 */ }

@Component("secondaryDataSource")
public class SecondaryDataSource { /* 实现 */ }

@Service
public class DataService {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;
}
```

---

## 生命周期注解

### @PostConstruct

PostConstruct 注解用于在依赖注入完成后需要执行的方法上以执行任何初始化。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 描述

`@PostConstruct` 注解用于在依赖注入完成后需要执行的方法上。它在所有依赖注入后调用。

#### 用法

```java
@Component
public class DatabaseInitializer {

    @PostConstruct
    public void init() {
        // 在依赖注入后执行的初始化代码
        System.out.println("数据库已初始化");
    }
}
```

---

### @PreDestroy

PreDestroy 注解用于方法上，作为实例正被容器移除的回调通知。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 描述

`@PreDestroy` 注解用于表示该方法应在 Bean 被容器销毁之前调用。

#### 用法

```java
@Component
public class ConnectionPoolManager {

    @PreDestroy
    public void cleanup() {
        // 在 Bean 销毁前执行的清理代码
        System.out.println("正在清理资源");
    }
}
```

---

## Web 注解

### @RequestMapping

提供 HTTP 请求路径的映射。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: TYPE, METHOD

**运行时保留**: 是

#### 描述

`@RequestMapping` 注解将 Web 请求映射到特定的处理方法。它可以应用于类级或方法级处理程序。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String[]` | `{}` | 请求的路径映射。 |
| `method` | `RequestMethod[]` | `{}` | 要映射的 HTTP 方法。 |
| `produces` | `String[]` | `{}` | 方法可产生的媒体类型。 |
| `consumes` | `String[]` | `{}` | 方法可消费的媒体类型。 |

#### 用法

```java
@Controller
@RequestMapping("/api/users")  // 所有方法的基准映射
public class UserController {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listUsers() {
        return "user-list";
    }
}
```

---

### @GetMapping

方便注解，本身带有 @RequestMapping(method = RequestMethod.GET)。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 用法

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

---

### @PostMapping

方便注解，本身带有 @RequestMapping(method = RequestMethod.POST)。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 用法

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

方便注解，本身带有 @RequestMapping(method = RequestMethod.PUT)。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 用法

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

方便注解，本身带有 @RequestMapping(method = RequestMethod.DELETE)。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: METHOD

**运行时保留**: 是

#### 用法

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

## 参数绑定注解

### @PathVariable

注解表示方法参数应绑定到 URI 模板变量。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: PARAMETER

**运行时保留**: 是

#### 描述

`@PathVariable` 注解用于将方法参数绑定到 URI 模板变量。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | 要绑定到的路径变量名称。 |
| `required` | `boolean` | `true` | 路径变量是否必需。 |

#### 用法

```java
@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(
    @PathVariable Long userId,  // 使用参数名
    @PathVariable("orderId") Long id) {  // 显式名称映射
    return orderService.get(userId, id);
}
```

---

### @RequestParam

注解表示方法参数应绑定到 Web 请求参数。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: PARAMETER

**运行时保留**: 是

#### 描述

`@RequestParam` 注解用于将方法参数绑定到 Web 请求参数（查询参数或表单数据）。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | 要绑定到的请求参数名称。 |
| `required` | `boolean` | `true` | 参数是否必需。 |
| `defaultValue` | `String` | `""` | 用作回退的默认值。 |

#### 用法

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

注解表示方法参数应绑定到 Web 请求的 body。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: PARAMETER

**运行时保留**: 是

#### 描述

`@RequestBody` 注解表示方法参数应绑定到 Web 请求的 body 并从 JSON 反序列化。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `required` | `boolean` | `true` | 请求 body 是否必需。 |

#### 用法

```java
@PostMapping("/users")
public User createUser(@RequestBody CreateUserRequest request) {
    return userService.create(request);
}
```

---

### @RequestHeader

注解表示方法参数应绑定到 Web 请求头。

**包**: `org.nanoboot.annotation.Annotation`

**目标**: PARAMETER

**运行时保留**: 是

#### 描述

`@RequestHeader` 注解用于将方法参数绑定到 Web 请求头。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | `""` | 要绑定到的头名称。 |
| `required` | `boolean` | `true` | 头是否必需。 |
| `defaultValue` | `String` | `""` | 用作回退的默认值。 |

#### 用法

```java
@GetMapping("/api/status")
public String getStatus(@RequestHeader("User-Agent") String userAgent) {
    return "{\"status\": \"ok\", \"client\": \"" + userAgent + "\"}";
}
```

---

## WebSocket 注解

### @ServerEndpoint

用于装饰作为 WebSocket 端点实现的 Java 类的注解。

**包**: `org.nanoboot.websocket.annotation`

**目标**: TYPE

**运行时保留**: 是

#### 描述

`@ServerEndpoint` 注解用于将一个类标记为 WebSocket 端点。它指定部署 WebSocket 的路径。

#### 属性

| 属性 | 类型 | 默认值 | 描述 |
|-----------|------|---------|-------------|
| `value` | `String` | 无 | 部署 WebSocket 端点的 URI 路径。 |

#### 用法

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        // 连接打开
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 收到消息
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // 连接关闭
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // 发生错误
    }
}
```

---

此综合参考提供了 NanoBoot 框架中所有可用注解的详细信息，包括它们的属性、用法和示例。
