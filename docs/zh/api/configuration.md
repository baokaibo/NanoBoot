# 配置

NanoBoot 框架中所有可用配置属性的参考文档。

## 服务器配置

用于配置 HTTP 服务器和 Web 功能的属性。

### 核心服务器属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `server.port` | Integer | `8080` | HTTP 服务器监听的端口号 |
| `server.host` | String | `localhost` | 服务器绑定的主机地址 |
| `server.context-path` | String | `/` | 所有 Web 端点的基准路径 |
| `server.max-connections` | Integer | `100` | 最大并发连接数 |
| `server.thread-pool-size` | Integer | `10` | 处理请求的线程池大小 |
| `server.request-timeout` | Integer | `30000` | 请求超时时间（毫秒） |
| `server.response-timeout` | Integer | `30000` | 响应超时时间（毫秒） |
| `server.max-request-size` | Integer | `10485760` | HTTP 请求最大大小（字节，10MB） |

### 示例用法

在 application.properties 中：
```properties
# 服务器配置
server.port=9090
server.host=0.0.0.0
server.context-path=/api
server.max-connections=200
server.thread-pool-size=20
```

## 数据库配置

用于配置数据库连接和数据源的属性。

### MySQL 配置

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `database.mysql.jdbcUrl` | String | 无 | 连接 MySQL 数据库的 JDBC URL |
| `database.mysql.username` | String | 无 | 数据库用户名 |
| `database.mysql.password` | String | 无 | 数据库密码 |
| `database.mysql.driverClassName` | String | `com.mysql.cj.jdbc.Driver` | JDBC 驱动类名 |
| `database.mysql.maximumPoolSize` | Integer | `20` | 连接池最大大小 |
| `database.mysql.minimumIdle` | Integer | `5` | 池中最小空闲连接数 |
| `database.mysql.connectionTimeout` | Integer | `30000` | 连接超时时间（毫秒） |
| `database.mysql.idleTimeout` | Integer | `600000` | 空闲超时时间（毫秒） |
| `database.mysql.maxLifetime` | Integer | `1800000` | 连接最大生命周期（毫秒） |
| `database.mysql.leakDetectionThreshold` | Integer | `60000` | 检测连接泄漏的阈值（毫秒） |

### 示例用法

在 application.properties 中：
```properties
# MySQL 配置
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/myapp
database.mysql.username=myuser
database.mysql.password=mypassword
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver

# 连接池设置
database.mysql.maximumPoolSize=25
database.mysql.minimumIdle=5
database.mysql.connectionTimeout=30000
database.mysql.idleTimeout=600000
database.mysql.maxLifetime=1800000
```

## Redis 配置

用于配置 Redis 连接的属性。

### Redis 连接属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `redis.host` | String | `localhost` | Redis 服务器主机地址 |
| `redis.port` | Integer | `6379` | Redis 服务器端口 |
| `redis.timeout` | Integer | `2000` | 连接超时时间（毫秒） |
| `redis.database` | Integer | `0` | Redis 数据库索引 |
| `redis.password` | String | 空 | Redis 服务器密码（如需要） |

### Redis 池属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `redis.pool.maxTotal` | Integer | `20` | 池中最大总连接数 |
| `redis.pool.maxIdle` | Integer | `10` | 池中最大空闲连接数 |
| `redis.pool.minIdle` | Integer | `2` | 池中最小空闲连接数 |
| `redis.pool.maxWaitMillis` | Integer | `3000` | 请求连接时的最大等待时间 |
| `redis.pool.testOnBorrow` | Boolean | `true` | 借用时测试连接有效性 |
| `redis.pool.testOnReturn` | Boolean | `false` | 归还时测试连接有效性 |
| `redis.pool.testWhileIdle` | Boolean | `true` | 空闲时测试连接有效性 |

### 示例用法

在 application.properties 中：
```properties
# Redis 配置
redis.host=redis-server.local
redis.port=6379
redis.timeout=2000
redis.database=1
redis.password=secretPassword

# 连接池
redis.pool.maxTotal=30
redis.pool.maxIdle=15
redis.pool.minIdle=5
redis.pool.maxWaitMillis=5000
redis.pool.testOnBorrow=true
redis.pool.testWhileIdle=true
```

## WebSocket 配置

用于配置 WebSocket 功能的属性。

### WebSocket 属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `websocket.enabled` | Boolean | `true` | 是否启用 WebSocket 功能 |
| `websocket.path.prefix` | String | `/websocket` | 所有 WebSocket 端点的基准路径 |
| `websocket.max.text.message.buffer.size` | Integer | `8192` | 文本消息缓冲区最大大小 |
| `websocket.max.binary.message.buffer.size` | Integer | `8192` | 二进制消息缓冲区最大大小 |
| `websocket.max.session.idle.timeout` | Integer | `300000` | 会话空闲超时（毫秒，5分钟） |
| `websocket.connection.limits.per.ip` | Integer | `10` | 每个 IP 地址的最大连接数 |
| `websocket.ping.interval` | Integer | `30000` | Ping 间隔（毫秒，30秒） |
| `websocket.pong.timeout` | Integer | `10000` | Pong 超时（毫秒，10秒） |

### 重连属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `websocket.reconnect.max.attempts` | Integer | `5` | 最大重连尝试次数 |
| `websocket.reconnect.initial.delay.ms` | Integer | `1000` | 首次重连尝试前的延迟（毫秒） |
| `websocket.reconnect.multiplier` | Double | `2.0` | 尝试间指数退避的乘数 |

### 示例用法

在 application.properties 中：
```properties
# WebSocket 配置
websocket.enabled=true
websocket.path.prefix=/ws
websocket.max.text.message.buffer.size=16384
websocket.max.session.idle.timeout=600000  # 10分钟
websocket.connection.limits.per.ip=5

# 重连设置
websocket.reconnect.max.attempts=3
websocket.reconnect.initial.delay.ms=2000
websocket.reconnect.multiplier=1.5
```

## 应用程序属性

用于常规应用程序配置的属性。

### 常见应用程序属性

| 属性 | 类型 | 默认值 | 描述 |
|----------|------|---------|-------------|
| `app.name` | String | `nano-boot-app` | 应用程序名称 |
| `app.version` | String | `1.0.0` | 应用程序版本 |
| `app.description` | String | 空 | 应用程序描述 |
| `app.environment` | String | `development` | 环境（development、staging、production） |

### 示例用法

在 application.properties 中：
```properties
# 应用程序属性
app.name=My Awesome App
app.version=2.1.0
app.description=A sample NanoBoot application
app.environment=production
```

## 属性占位符和表达式

### 占位符语法

使用 `${placeholder}` 语法引用其他属性或系统属性：

```properties
# 使用其他属性
server.port=${PORT:8080}  # 使用 PORT 环境变量或默认为 8080

# 使用系统属性
app.tmp.dir=${java.io.tmpdir}/nanoapp

# 使用默认值
database.url=${DB_URL:jdbc:h2:mem:testdb}
```

### 表达式支持

框架支持属性解析的简单表达式：

```properties
# 基于环境的条件值
cache.ttl.seconds=${app.environment == 'production' ? 3600 : 300}
```

## 特定于环境的配置

您可以创建特定于环境的配置文件：

- `application.properties` - 默认配置
- `application-development.properties` - 开发环境
- `application-production.properties` - 生产环境
- `application-test.properties` - 测试环境

### 激活配置文件

使用系统属性设置活动配置文件：
```bash
java -Dspring.profiles.active=production -jar myapp.jar
```

或在属性文件中：
```properties
spring.profiles.active=development
```

## 配置最佳实践

### 1. 保护敏感信息

永远不要将密码等敏感信息以明文形式存储在配置文件中。使用环境变量：

```properties
# 不要这样做：
database.password=secretpassword

# 使用环境变量：
database.password=${DB_PASSWORD}
```

### 2. 使用描述性属性名

选择清晰、描述性的属性名，遵循点号表示法：

```properties
# 好的做法
database.mysql.jdbc-url=jdbc:mysql://localhost:3306/app
cache.redis.expiration-seconds=3600

# 避免使用缩写
db.mysql.url=jdbc:mysql://localhost:3306/app  # 仍然可以接受
```

### 3. 设置适当的默认值

提供合理的默认值，使应用程序开箱即用：

```properties
# 开发环境的合理默认值
server.port=${PORT:8080}
database.mysql.jdbcUrl=${DATABASE_URL:jdbc:h2:mem:devdb}
logging.level.com.mycompany=${LOG_LEVEL:INFO}
```

### 4. 特定于环境的值

不同环境通常需要不同的配置值：

```properties
# 开发环境
logging.level.com.mycompany=DEBUG
database.mysql.maximumPoolSize=5

# 生产环境（在 application-production.properties 中）
logging.level.com.mycompany=WARN
database.mysql.maximumPoolSize=20
```

### 5. 记录自定义属性

记录您在应用程序中引入的任何自定义属性：

```properties
# 我的自定义功能配置
# 控制数据同步频率（秒）
feature.sync.frequency=${FEATURE_SYNC_FREQ:60}

# 启用或禁用实验性功能
feature.experimental.enabled=${FEATURE_EXPERIMENTAL_ENABLED:false}
```

## 加载配置

配置按以下顺序加载（后面的来源覆盖前面的来源）：

1. 默认属性（以编程方式定义）
2. application.properties（或 YAML）
3. 特定于环境的属性（例如 application-production.properties）
4. 打包在 JAR 内部的属性（jar:config/application.properties）
5. 外部属性来自 --spring.config.location
6. 系统属性（-D 参数）
7. 环境变量
8. 命令行参数

此综合配置系统允许您自定义 NanoBoot 应用程序的各个方面，同时保持灵活性和安全最佳实践。
