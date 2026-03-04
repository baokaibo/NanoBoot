# NanoBoot 轻量级框架设计文档

## 1. 整体架构

NanoBoot是一个轻量级的Java框架，模仿Spring Boot的设计理念，提供IOC容器、注解驱动、自动包扫描、自动装配和内嵌HTTP服务器等功能。

### 架构层次
```
+----------------------------------+
|         nano-boot-starter        |
|  (启动器 - 集成各模块入口)        |
+----------------------------------+
|           nano-boot-web          |
|    (Web功能 - HTTP服务器)        |
+----------------------------------+
|          nano-boot-core          |
|  (核心功能 - IOC容器、注解等)     |
+----------------------------------+
```

## 2. 模块划分

### 2.1 nano-boot-core
- **annotation包**: 定义框架所需的所有注解
- **container包**: IOC容器的核心实现
- **config包**: 配置管理实现

### 2.2 nano-boot-web
- **server包**: 内嵌HTTP服务器实现
- **handler包**: 请求处理逻辑

### 2.3 nano-boot-starter
- **boot包**: 提供便捷的启动入口

## 3. 核心类设计

### 3.1 注解定义 (Annotation.java)
- `@Component`: 组件注解
- `@Service`: 服务注解
- `@Controller`: 控制器注解
- `@Autowired`: 依赖注入注解
- `@Value`: 属性值注入注解
- `@RequestMapping`: 请求映射注解

### 3.2 容器实现 (DefaultApplicationContext.java)
- 实现`ApplicationContext`接口
- 管理Bean生命周期
- 支持依赖注入和自动装配
- 提供包扫描功能

### 3.3 包扫描器 (PackageScanner.java)
- 扫描指定包下的所有类
- 识别被注解标记的类
- 注册为Bean定义

## 4. 设计思路

### 4.1 IOC容器设计
采用三级缓存解决循环依赖问题：
1. `singletonObjects`: 存储完整的单例对象
2. `earlySingletonObjects`: 存储提前暴露的对象
3. `singletonFactories`: 存储ObjectFactory

### 4.2 注解驱动
通过反射机制识别和处理各种注解，实现零配置开发体验。

### 4.3 自动装配
- 字段注入：`@Autowired`注解的字段自动注入
- 类型匹配：按类型查找合适的Bean进行注入
- 名称匹配：当多个候选时按名称匹配

### 4.4 内嵌服务器
利用JDK内置的`HttpServer`实现轻量级HTTP服务。

## 5. 使用示例

### 5.1 服务类
```java
@Service
public class UserService {
    @Value("${app.name:DefaultApp}")
    private String appName;

    public String getUserById(Long id) {
        return "User-" + id + " from " + appName;
    }
}
```

### 5.2 控制器类
```java
@Controller("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public String getUser(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }
}
```

### 5.3 启动类
```java
@NanoBootApplication
public class Application {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(Application.class, args);
    }
}
```

## 6. 扩展方向

### 6.1 功能扩展
- **AOP支持**: 通过动态代理实现面向切面编程
- **事务管理**: 集成数据库事务处理
- **安全模块**: 实现认证授权功能
- **缓存支持**: 集成Redis、内存缓存等
- **消息队列**: 支持异步消息处理

### 6.2 性能优化
- **懒加载**: 非单例Bean按需创建
- **缓存优化**: 优化Bean定义和实例缓存
- **并发优化**: 提高多线程环境下性能

### 6.3 生态集成
- **第三方库集成**: 与MyBatis、JPA等ORM框架集成
- **监控功能**: 集成Micrometer等监控工具
- **配置中心**: 支持外部配置管理

## 7. 启动流程

1. **解析注解**: 扫描启动类上的`@NanoBootApplication`注解
2. **包扫描**: 扫描基础包路径下的所有类
3. **注册Bean**: 识别注解类并注册为Bean定义
4. **依赖注入**: 解析和注入Bean之间的依赖关系
5. **初始化Bean**: 调用初始化方法（如`@PostConstruct`）
6. **启动Web服务器**: 注册控制器并启动HTTP服务
7. **应用运行**: 保持应用运行状态处理请求

## 8. 特性对比

| 特性 | Spring Boot | NanoBoot |
|------|-------------|----------|
| IOC容器 | ✓ | ✓ |
| 注解驱动 | ✓ | ✓ |
| 自动配置 | ✓ | 部分支持 |
| 内嵌服务器 | ✓ | ✓ (简单版) |
| AOP支持 | ✓ | 待扩展 |
| 数据访问 | ✓ | 待扩展 |
| 安全框架 | ✓ | 待扩展 |

该框架设计遵循单一职责原则，各模块功能明确，易于扩展和维护。