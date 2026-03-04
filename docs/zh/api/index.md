# API 参考

本节提供 NanoBoot 框架中可用的核心 API 和注解的详细参考文档。

## 注解

### 核心注解

#### @NanoBootApplication
标记主应用程序类并启用框架功能。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan
@EnableConfigurationProperties
public @interface NanoBootApplication {
    // 配置属性
}
```

**用法：**
```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

#### @Component
表示带注解的类是一个"组件"。这些类将被自动检测并注册为 Bean。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";  // Bean 的可选名称
}
```

**用法：**
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

#### @Service
用于表示带注解的类是服务组件的原型注解。这是 @Component 的特化。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Service {
    String value() default "";
}
```

#### @Controller
用于表示带注解的类是 Web 控制器的原型注解。这是 @Component 的特化。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Controller {
    String value() default "";
}
```

### 依赖注入注解

#### @Autowired
将构造函数、字段、setter 方法或配置方法标记为由容器自动装配。

```java
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}
```

#### @Value
在字段或方法/构造函数参数级别注解，表示受影响参数的默认值表达式。

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}
```

#### @Qualifier
此注解可在字段或参数上用作自动装配时候选 Bean 的限定符。

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    String value() default "";
}
```

### 生命周期注解

#### @PostConstruct
PostConstruct 注解用于在依赖注入完成后需要执行的方法上，以执行任何初始化。

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostConstruct {
    // 无属性
}
```

#### @PreDestroy
PreDestroy 注解用于方法上，作为实例正被容器移除的回调通知。

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
    // 无属性
}
```

### Web 注解

#### @RequestMapping
提供 HTTP 请求路径的映射。

```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String[] value() default {};
    RequestMethod[] method() default {};
    String[] produces() default {};
    String[] consumes() default {};
}
```

#### @GetMapping
方便注解，本身带有 @RequestMapping(method = RequestMethod.GET)。

#### @PostMapping
方便注解，本身带有 @RequestMapping(method = RequestMethod.POST)。

#### @PutMapping
方便注解，用于 PUT 请求。

#### @DeleteMapping
方便注解，用于 DELETE 请求。

### 参数绑定注解

#### @PathVariable
注解表示方法参数应绑定到 URI 模板变量。

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value() default "";
    boolean required() default true;
}
```

#### @RequestParam
注解表示方法参数应绑定到 Web 请求参数。

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value() default "";
    boolean required() default true;
    String defaultValue() default "";
}
```

#### @RequestBody
注解表示方法参数应绑定到 Web 请求的 body。

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
    boolean required() default true;
}
```

#### @RequestHeader参数应绑定到
注解表示方法 Web 请求头。

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {
    String value() default "";
    boolean required() default true;
    String defaultValue() default "";
}
```

### WebSocket 注解

#### @ServerEndpoint
用于装饰作为 WebSocket 端点实现的 Java 类的注解。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerEndpoint {
    String value();
    String[] subprotocols() default {};
}
```

## 核心类

### NanoBootApplicationRunner
负责运行 NanoBoot 应用程序的主类。

```java
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args);

    public static void runWithCustomContext(Class<?> mainClass, String[] args,
                                          ApplicationContext applicationContext);
}
```

### ApplicationContext
提供应用程序配置的中心接口。

```java
public class ApplicationContext {

    public <T> T getBean(Class<T> type);

    public Object getBean(String name);

    public <T> T getBean(String name, Class<T> type);

    public boolean containsBean(String name);

    public boolean containsBean(Class<?> type);
}
```

### Session (WebSocket)
表示 WebSocket 会话的接口。

```java
public interface Session {

    String getId();

    void sendMessage(String text) throws IOException;

    void sendMessage(ByteBuffer data) throws IOException;

    boolean isOpen();

    void close() throws IOException;

    String getQueryString();

    Map<String, Object> getUserProperties();
}
```

## 配置属性

### 服务器配置
用于配置 HTTP 服务器的属性：

- `server.port`：监听端口号（默认：8080）
- `server.host`：绑定主机地址（默认：localhost）
- `server.context-path`：所有端点的基准路径（默认：/）
- `server.max-connections`：最大并发连接数（默认：100）
- `server.thread-pool-size`：线程池大小（默认：10）

### 数据库配置
用于数据库连接的属性：

- `database.mysql.jdbcUrl`：MySQL 的 JDBC URL
- `database.mysql.username`：数据库用户名
- `database.mysql.password`：数据库密码
- `database.mysql.driverClassName`：驱动类名
- `database.mysql.maximumPoolSize`：最大连接池大小
- `database.mysql.minimumIdle`：最小空闲连接数

### Redis 配置
用于 Redis 连接的属性：

- `redis.host`：Redis 服务器主机（默认：localhost）
- `redis.port`：Redis 服务器端口（默认：6379）
- `redis.timeout`：连接超时（毫秒）
- `redis.pool.maxTotal`：最大总连接数
- `redis.pool.maxIdle`：最大空闲连接数
- `redis.pool.minIdle`：最小空闲连接数

### WebSocket 配置
用于 WebSocket 设置的属性：

- `websocket.enabled`：是否启用 WebSocket（默认：true）
- `websocket.path.prefix`：WebSocket 端点基准路径（默认：/websocket）
- `websocket.max.text.message.buffer.size`：最大文本消息缓冲区大小
- `websocket.max.session.idle.timeout`：会话空闲超时（毫秒）
- `websocket.connection.limits.per.ip`：每个 IP 地址的最大连接数

此 API 参考提供了 NanoBoot 框架中所有核心组件和注解的完整文档。
