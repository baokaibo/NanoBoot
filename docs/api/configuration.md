# Configuration

Reference for all configuration properties available in the NanoBoot framework.

## Server Configuration

Properties for configuring the HTTP server and web functionality.

### Core Server Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `server.port` | Integer | `8080` | Port number for the HTTP server to listen on |
| `server.host` | String | `localhost` | Host address to bind the server to |
| `server.context-path` | String | `/` | Base path for all web endpoints |
| `server.max-connections` | Integer | `100` | Maximum number of concurrent connections |
| `server.thread-pool-size` | Integer | `10` | Size of the thread pool for handling requests |
| `server.request-timeout` | Integer | `30000` | Request timeout in milliseconds |
| `server.response-timeout` | Integer | `30000` | Response timeout in milliseconds |
| `server.max-request-size` | Integer | `10485760` | Maximum size of HTTP request in bytes (10MB) |

### Example Usage

In `application.properties`:
```properties
# Server configuration
server.port=9090
server.host=0.0.0.0
server.context-path=/api
server.max-connections=200
server.thread-pool-size=20
```

## Database Configuration

Properties for configuring database connections and data sources.

### MySQL Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `database.mysql.jdbcUrl` | String | None | JDBC URL for connecting to MySQL database |
| `database.mysql.username` | String | None | Database username |
| `database.mysql.password` | String | None | Database password |
| `database.mysql.driverClassName` | String | `com.mysql.cj.jdbc.Driver` | JDBC driver class name |
| `database.mysql.maximumPoolSize` | Integer | `20` | Maximum size of the connection pool |
| `database.mysql.minimumIdle` | Integer | `5` | Minimum number of idle connections in the pool |
| `database.mysql.connectionTimeout` | Integer | `30000` | Connection timeout in milliseconds |
| `database.mysql.idleTimeout` | Integer | `600000` | Idle timeout in milliseconds |
| `database.mysql.maxLifetime` | Integer | `1800000` | Maximum lifetime of connections in milliseconds |
| `database.mysql.leakDetectionThreshold` | Integer | `60000` | Threshold for detecting connection leaks in milliseconds |

### Example Usage

In `application.properties`:
```properties
# MySQL Configuration
database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/myapp
database.mysql.username=myuser
database.mysql.password=mypassword
database.mysql.driverClassName=com.mysql.cj.jdbc.Driver

# Connection Pool Settings
database.mysql.maximumPoolSize=25
database.mysql.minimumIdle=5
database.mysql.connectionTimeout=30000
database.mysql.idleTimeout=600000
database.mysql.maxLifetime=1800000
```

## Redis Configuration

Properties for configuring Redis connections.

### Redis Connection Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `redis.host` | String | `localhost` | Redis server host address |
| `redis.port` | Integer | `6379` | Redis server port |
| `redis.timeout` | Integer | `2000` | Connection timeout in milliseconds |
| `redis.database` | Integer | `0` | Redis database index |
| `redis.password` | String | Empty | Redis server password (if required) |

### Redis Pool Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `redis.pool.maxTotal` | Integer | `20` | Maximum total connections in the pool |
| `redis.pool.maxIdle` | Integer | `10` | Maximum idle connections in the pool |
| `redis.pool.minIdle` | Integer | `2` | Minimum idle connections in the pool |
| `redis.pool.maxWaitMillis` | Integer | `3000` | Maximum wait time when requesting a connection |
| `redis.pool.testOnBorrow` | Boolean | `true` | Test connection validity when borrowing |
| `redis.pool.testOnReturn` | Boolean | `false` | Test connection validity when returning |
| `redis.pool.testWhileIdle` | Boolean | `true` | Test connection validity while idle |

### Example Usage

In `application.properties`:
```properties
# Redis Configuration
redis.host=redis-server.local
redis.port=6379
redis.timeout=2000
redis.database=1
redis.password=secretPassword

# Connection Pool
redis.pool.maxTotal=30
redis.pool.maxIdle=15
redis.pool.minIdle=5
redis.pool.maxWaitMillis=5000
redis.pool.testOnBorrow=true
redis.pool.testWhileIdle=true
```

## WebSocket Configuration

Properties for configuring WebSocket functionality.

### WebSocket Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `websocket.enabled` | Boolean | `true` | Whether WebSocket functionality is enabled |
| `websocket.path.prefix` | String | `/websocket` | Base path for all WebSocket endpoints |
| `websocket.max.text.message.buffer.size` | Integer | `8192` | Maximum size of text message buffer |
| `websocket.max.binary.message.buffer.size` | Integer | `8192` | Maximum size of binary message buffer |
| `websocket.max.session.idle.timeout` | Integer | `300000` | Session idle timeout in milliseconds (5 minutes) |
| `websocket.connection.limits.per.ip` | Integer | `10` | Maximum connections per IP address |
| `websocket.ping.interval` | Integer | `30000` | Ping interval in milliseconds (30 seconds) |
| `websocket.pong.timeout` | Integer | `10000` | Pong timeout in milliseconds (10 seconds) |

### Reconnection Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `websocket.reconnect.max.attempts` | Integer | `5` | Maximum reconnection attempts |
| `websocket.reconnect.initial.delay.ms` | Integer | `1000` | Initial delay before first reconnection attempt in milliseconds |
| `websocket.reconnect.multiplier` | Double | `2.0` | Multiplier for exponential backoff between attempts |

### Example Usage

In `application.properties`:
```properties
# WebSocket Configuration
websocket.enabled=true
websocket.path.prefix=/ws
websocket.max.text.message.buffer.size=16384
websocket.max.session.idle.timeout=600000  # 10 minutes
websocket.connection.limits.per.ip=5

# Reconnection settings
websocket.reconnect.max.attempts=3
websocket.reconnect.initial.delay.ms=2000
websocket.reconnect.multiplier=1.5
```

## Application Properties

Properties for general application configuration.

### Common Application Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `app.name` | String | `nano-boot-app` | Name of the application |
| `app.version` | String | `1.0.0` | Version of the application |
| `app.description` | String | Empty | Description of the application |
| `app.environment` | String | `development` | Environment (development, staging, production) |

### Example Usage

In `application.properties`:
```properties
# Application Properties
app.name=My Awesome App
app.version=2.1.0
app.description=A sample NanoBoot application
app.environment=production
```

## Property Placeholders and Expressions

### Placeholder Syntax

Use the `${placeholder}` syntax to reference other properties or system properties:

```properties
# Using other properties
server.port=${PORT:8080}  # Use PORT environment variable or default to 8080

# Using system properties
app.tmp.dir=${java.io.tmpdir}/nanoapp

# Using defaults
database.url=${DB_URL:jdbc:h2:mem:testdb}
```

### Expression Support

The framework supports simple expressions for property resolution:

```properties
# Conditional values based on environment
cache.ttl.seconds=${app.environment == 'production' ? 3600 : 300}
```

## Profile-Specific Configuration

You can create profile-specific configuration files:

- `application.properties` - Default configuration
- `application-development.properties` - Development environment
- `application-production.properties` - Production environment
- `application-test.properties` - Test environment

### Activating Profiles

Set the active profile using system property:
```bash
java -Dspring.profiles.active=production -jar myapp.jar
```

Or in properties file:
```properties
spring.profiles.active=development
```

## Configuration Best Practices

### 1. Secure Sensitive Information

Never store sensitive information like passwords in plain text configuration files. Use environment variables:

```properties
# Instead of this:
database.password=secretpassword

# Use environment variables:
database.password=${DB_PASSWORD}
```

### 2. Use Descriptive Property Names

Choose clear, descriptive property names following dot notation:

```properties
# Good
database.mysql.jdbc-url=jdbc:mysql://localhost:3306/app
cache.redis.expiration-seconds=3600

# Avoid abbreviations unless they're standard
db.mysql.url=jdbc:mysql://localhost:3306/app  # Still acceptable
```

### 3. Set Appropriate Defaults

Provide reasonable defaults to make applications work out of the box:

```properties
# Reasonable defaults for development
server.port=${PORT:8080}
database.mysql.jdbcUrl=${DATABASE_URL:jdbc:h2:mem:devdb}
logging.level.com.mycompany=${LOG_LEVEL:INFO}
```

### 4. Environment-Specific Values

Different environments often need different configuration values:

```properties
# Development
logging.level.com.mycompany=DEBUG
database.mysql.maximumPoolSize=5

# Production (in application-production.properties)
logging.level.com.mycompany=WARN
database.mysql.maximumPoolSize=20
```

### 5. Document Custom Properties

Document any custom properties you introduce in your application:

```properties
# My Custom Feature Configuration
# Controls the frequency of data synchronization (in seconds)
feature.sync.frequency=${FEATURE_SYNC_FREQ:60}

# Enable or disable experimental features
feature.experimental.enabled=${FEATURE_EXPERIMENTAL_ENABLED:false}
```

## Loading Configuration

Configuration is loaded in the following order (later sources override earlier ones):

1. Default properties (defined programmatically)
2. `application.properties` (or YAML)
3. Profile-specific properties (e.g., `application-production.properties`)
4. Application properties packaged inside JAR (`jar:config/application.properties`)
5. External properties from `--spring.config.location`
6. System properties (`-D` parameters)
7. Environment variables
8. Command line arguments

This comprehensive configuration system allows you to customize every aspect of your NanoBoot application while maintaining flexibility and security best practices.