# 依赖注入

NanoBoot 提供了一个强大的依赖注入（DI）容器，用于管理对象的创建和生命周期。该系统自动连接组件，使您的代码更加模块化和可测试。

## 核心概念

### 组件和 Bean

在 NanoBoot 中，任何使用 `@Component` 注解的类都被视为受管 Bean：

```java
import org.nanoboot.annotation.Annotation.Component;

@Component
public class UserService {
    // 该类将由 DI 容器管理
}
```

### 组件类型

NanoBoot 支持几种类型，它们是 `@Component` 的别名：

- `@Service` - 用于服务层组件
- `@Repository` - 用于数据访问组件（即将推出）
- `@Controller` - 用于 Web 控制器

### 使用 @Autowired 进行依赖注入

使用 `@Autowired` 将依赖注入到字段、构造函数或方法中：

```java
@Component
public class OrderService {

    @Autowired
    private UserService userService;  // 字段注入

    @Autowired
    private PaymentService paymentService;

    public String processOrder(Long userId, BigDecimal amount) {
        User user = userService.findById(userId);
        // 使用注入的服务处理订单
        return paymentService.processPayment(user, amount);
    }
}
```

### 构造函数注入

优先使用构造函数注入，因为它确保始终提供必需的依赖：

```java
@Component
public class OrderProcessor {
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired  // 构造函数注入（较新版本中可选）
    public OrderProcessor(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    public void processOrder(Order order) {
        // 使用注入的依赖
    }
}
```

## 属性注入

使用 `@Value` 注入配置属性：

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${database.url:jdbc:h2:mem:testdb}")
    private String databaseUrl;

    // Getter 和 Setter...
}
```

### 属性源

属性可以来自：
- `application.properties` 文件
- 系统属性
- 环境变量
- 命令行参数

## 限定符和命名 Bean

当您有多个相同类型的 Bean 时，使用 `@Qualifier` 指定要注入的哪一个：

```java
// 定义多个实现
@Component("emailNotifier")
public class EmailNotificationService implements NotificationService {
    // 实现
}

@Component("smsNotifier")
public class SmsNotificationService implements NotificationService {
    // 实现
}

// 注入特定实现
@Component
public class OrderService {

    @Autowired
    @Qualifier("emailNotifier")
    private NotificationService notificationService;
}
```

## 生命周期注解

### @PostConstruct

使用 `@PostConstruct` 注解的方法在依赖注入完成后运行：

```java
@Component
public class DatabaseInitializer {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeDatabase() {
        // 初始化数据库架构或种子数据
        // 在所有依赖注入后调用
        System.out.println("数据库已初始化");
    }
}
```

### @PreDestroy

使用 `@PreDestroy` 注解的方法在 Bean 被销毁之前运行：

```java
@Component
public class ConnectionPoolManager {

    private ConnectionPool pool;

    @PreDestroy
    public void cleanup() {
        // 关闭连接并清理资源
        if (pool != null) {
            pool.close();
        }
        System.out.println("连接池已关闭");
    }
}
```

## 作用域和单例模式

默认情况下，NanoBoot 中的所有 Bean 都是单例（每个应用程序一个实例）。容器管理生命周期并确保线程安全访问。

## 高级配置

### 组件扫描

带有 `@NanoBootApplication` 的主应用程序类自动启用组件扫描：

```java
@NanoBootApplication
@ComponentScan(basePackages = {"com.example.service", "com.example.controller"})
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### 条件 Bean 创建

可以根据属性值有条件地创建 Bean：

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
    public FeatureService featureService() {
        return new FeatureServiceImpl();
    }
}
```

## 最佳实践

### 1. 使用构造函数注入
构造函数注入确保始终提供必需的依赖，并使测试更容易。

### 2. 避免循环依赖
设计您的组件以避免循环依赖。如果您有循环依赖，请考虑使用第三方组件进行重构。

### 3. 分组相关组件
根据功能将代码组织到逻辑包中。

### 4. 使用适当的类型注解
适当使用 `@Service`、`@Controller` 等来指示组件的角色。

### 5. 外部化配置属性
使用带有外部配置的 `@Value` 注解，而不是硬编码值。

## 故障排除

### 常见问题

1. **Bean 创建失败**：确保所有 `@Autowired` 依赖存在且已正确注解。

2. **循环依赖**：使用 `@Lazy` 注解或重构以打破循环。

3. **缺少属性**：验证 `application.properties` 中的属性名与 `@Value` 注解匹配。

4. **找不到组件**：检查组件是否已正确注解并在扫描的包中。

NanoBoot 中的依赖注入系统为管理组件关系和配置提供了健壮的基础，使您的应用程序更加模块化、可测试和可维护。
