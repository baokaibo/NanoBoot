# 核心模块

核心模块是 NanoBoot 框架的基础，提供依赖注入、组件扫描和属性管理等基本功能。

## 概述

核心模块实现了依赖注入和应用程序上下文管理所需的基本功能，提供：

- 组件扫描和注册
- 依赖注入引擎
- 属性管理
- 注解处理
- 生命周期管理

## 关键功能

### 组件扫描

核心模块自动扫描并注册具有以下注解的组件：

- `@Component` - 通用组件
- `@Service` - 服务层组件
- `@Controller` - Web 控制器
- `@Repository` - 数据访问组件

### 依赖注入

依赖注入引擎解析使用 `@Autowired` 注解的依赖：

```java
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
}
```

### 属性管理

可以使用 `@Value` 注解注入属性：

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${server.port:8080}")
    private int port;
}
```

### 生命周期管理

组件可以使用注解挂接到其生命周期：

```java
@Component
public class DatabaseInitializer {

    @PostConstruct
    public void init() {
        // 依赖注入后运行
    }

    @PreDestroy
    public void cleanup() {
        // Bean 销毁前运行
    }
}
```

## 核心组件

### ApplicationContext

应用程序中所有 Bean 的中央注册表：

```java
public class ApplicationContext {
    private Map<Class<?>, Object> singletonBeans = new ConcurrentHashMap<>();
    private Map<String, Object> namedBeans = new ConcurrentHashMap<>();

    // 获取 Bean 的方法
    public <T> T getBean(Class<T> type) { ... }
    public Object getBean(String name) { ... }
}
```

### ComponentScanner

负责查找和注册注解类：

```java
public class ComponentScanner {
    public void scanAndRegister(String packagePath, ApplicationContext context) { ... }
}
```

### DependencyInjector

处理依赖注入：

```java
public class DependencyInjector {
    public void injectDependencies(Object bean, ApplicationContext context) { ... }
}
```

## 配置

### 组件扫描

在您的主应用程序中配置组件扫描：

```java
@NanoBootApplication
@ComponentScan(basePackages = {"com.example.service", "com.example.controller"})
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### 属性文件

将配置放在 `application.properties` 中：

```properties
# 应用程序属性
app.name=My Application
app.version=1.0.0

# 自定义属性
custom.feature.enabled=true
custom.timeout.seconds=30
```

## 最佳实践

### 1. 使用适当的类型注解

为每种组件类型使用正确的类型注解：
- `@Service` 用于业务逻辑
- `@Controller` 用于 Web 端点
- `@Repository` 用于数据访问（可用时）

### 2. 外部化配置属性

将配置值存储在属性文件中，而不是硬编码。

### 3. 使用构造函数注入

尽可能对必需的依赖使用构造函数注入。

### 4. 正确处理生命周期事件

适当使用 `@PostConstruct` 和 `@PreDestroy` 进行初始化和清理。

## 扩展核心

核心模块可以使用自定义注解和处理器进行扩展：

```java
// 自定义注解
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomInjection {
    String value() default "";
}

// 自定义处理器
public class CustomAnnotationProcessor {
    public void processCustomAnnotations(Object bean, ApplicationContext context) {
        // 自定义处理逻辑
    }
}
```

核心模块提供了所有其他模块构建的基础设施，确保整个框架中一致的依赖注入和组件管理。
