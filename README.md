# NanoBoot - 轻量级Java框架

![NanoBoot](docs/docs/image/NanoBoot.png)

NanoBoot是一个轻量级的Java框架，模仿Spring Boot的设计理念，提供IOC容器、注解驱动、自动包扫描、自动装配和内嵌HTTP服务器等功能。

## 特性

- **IOC容器**: 提供依赖注入和控制反转功能
- **注解驱动**: 使用注解简化配置
- **自动包扫描**: 自动扫描和注册Bean
- **自动装配**: 自动解析和注入依赖
- **内嵌HTTP服务器**: 内置轻量级HTTP服务器
- **模块化设计**: 清晰的模块结构便于扩展

## 模块结构

```
nano-boot/
├── nano-boot-core/           # 核心容器模块
│   ├── annotation/          # 注解定义
│   ├── container/           # 容器实现
│   └── config/              # 配置管理
├── nano-boot-web/           # Web模块
│   ├── server/              # HTTP服务器
│   └── handler/             # 请求处理器
└── nano-boot-starter/       # 启动器
    └── starter/             # 启动类
```

## 快速开始

### 1. 创建服务类
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

### 2. 创建控制器类
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

### 3. 启动应用
```java
@NanoBootApplication
public class Application {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(Application.class, args);
    }
}
```

## 支持的注解

- `@Component`: 组件注解
- `@Service`: 服务注解
- `@Controller`: 控制器注解
- `@Autowired`: 依赖注入
- `@Value`: 属性注入
- `@RequestMapping`: 请求映射
- `@GetMapping`: GET请求映射
- `@PostMapping`: POST请求映射
- `@RequestParam`: 请求参数
- `@PathVariable`: 路径变量

## 配置文件

支持`application.properties`配置文件：

```properties
app.name=NanoBootApplication
server.port=8080
```

## 扩展性

框架设计具有良好的扩展性，可以轻松添加以下功能：
- AOP支持
- 数据库访问
- 安全框架
- 缓存系统
- 消息队列
- 监控系统

## 架构优势

1. **轻量级**: 无额外依赖，基于JDK标准库
2. **易理解**: 代码简洁，逻辑清晰
3. **模块化**: 结构清晰，便于维护
4. **可扩展**: 设计灵活，易于扩展新功能
5. **高性能**: 简化的实现，高效的性能

## 开发指南

### 环境要求
- Java 8+
- Maven 3.6+

### 构建项目
```bash
mvn clean install
```

### 运行示例
```bash
java -cp target/nanoboot-example.jar org.nanoboot.example.NanoBootExampleApp
```

## 设计原则

- **单一职责**: 每个类只负责一个功能领域
- **开闭原则**: 对扩展开放，对修改封闭
- **依赖倒置**: 依赖抽象而非具体实现
- **接口隔离**: 使用细粒度的接口
- **里氏替换**: 子类可以替换父类

## 许可证

MIT License