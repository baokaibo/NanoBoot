# 工具类

NanoBoot 框架中可用的工具类和辅助函数的参考文档。

## 核心工具类

### ApplicationContext

提供应用程序配置的中心接口。ApplicationContext 管理 Bean 的生命周期并提供依赖注入功能。

#### 类定义
```java
public class ApplicationContext
```

#### 方法

##### getBean(Class<T> type)
获取指定类型的 Bean。

**签名：**
```java
public <T> T getBean(Class<T> type)
```

**参数：**
- `type`：要获取的 Bean 类型

**返回：**
- 指定类型的 Bean 实例

**抛出：**
- 如果找不到给定类型的 Bean，抛出 NoSuchBeanDefinitionException

**示例：**
```java
UserService userService = context.getBean(UserService.class);
```

##### getBean(String name)
获取具有指定名称的 Bean。

**签名：**
```java
public Object getBean(String name)
```

**参数：**
- `name`：要获取的 Bean 名称

**返回：**
- 具有指定名称的 Bean 实例

**抛出：**
- 如果找不到具有给定名称的 Bean，抛出 NoSuchBeanDefinitionException

**示例：**
```java
Object userService = context.getBean("userService");
```

##### getBean(String name, Class<T> type)
获取具有指定名称和类型的 Bean。

**签名：**
```java
public <T> T getBean(String name, Class<T> type)
```

**参数：**
- `name`：要获取的 Bean 名称
- `type`：要获取的 Bean 类型

**返回：**
- 具有指定名称和类型的 Bean 实例

**示例：**
```java
UserService userService = context.getBean("customUserService", UserService.class);
```

##### containsBean(String name)
检查是否存在具有指定名称的 Bean。

**签名：**
```java
public boolean containsBean(String name)
```

**参数：**
- `name`：要检查的 Bean 名称

**返回：**
- 如果 Bean 存在则返回 true，否则返回 false

**示例：**
```java
boolean exists = context.containsBean("userService");
```

##### containsBean(Class<?> type)
检查是否存在指定类型的 Bean。

**签名：**
```java
public boolean containsBean(Class<?> type)
```

**参数：**
- `type`：要检查的 Bean 类型

**返回：**
- 如果 Bean 存在则返回 true，否则返回 false

**示例：**
```java
boolean exists = context.containsBean(UserService.class);
```

---

### NanoBootApplicationRunner

负责运行 NanoBoot 应用程序的主类。此类处理应用程序上下文的初始化和启动。

#### 类定义
```java
public class NanoBootApplicationRunner
```

#### 方法

##### run(Class<?> mainClass, String[] args)
使用给定的主类和参数运行应用程序。

**签名：**
```java
public static void run(Class<?> mainClass, String[] args)
```

**参数：**
- `mainClass`：带有 @NanoBootApplication 注解的主应用程序类
- `args`：从命令行传递的应用程序参数

**示例：**
```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

##### runWithCustomContext(Class<?> mainClass, String[] args, ApplicationContext applicationContext)
使用自定义应用程序上下文运行应用程序。

**签名：**
```java
public static void runWithCustomContext(Class<?> mainClass, String[] args,
                                      ApplicationContext applicationContext)
```

**参数：**
- `mainClass`：带有 @NanoBootApplication 注解的主应用程序类
- `args`：从命令行传递的应用程序参数
- `applicationContext`：要使用的自定义应用程序上下文

**示例：**
```java
ApplicationContext customContext = new CustomApplicationContext();
NanoBootApplicationRunner.runWithCustomContext(MyAppApplication.class, args, customContext);
```

---

## Web 工具类

### Session (WebSocket)

表示 WebSocket 会话的接口，提供与连接的客户端通信的方法。

#### 接口定义
```java
public interface Session
```

#### 方法

##### getId()
获取此会话的唯一标识符。

**签名：**
```java
String getId()
```

**返回：**
- 会话 ID

**示例：**
```java
@OnOpen
public void onOpen(Session session) {
    String sessionId = session.getId();
    System.out.println("新会话: " + sessionId);
}
```

##### sendMessage(String text)
向连接的客户端发送文本消息。

**签名：**
```java
void sendMessage(String text) throws IOException
```

**参数：**
- `text`：要发送的文本消息

**抛出：**
- 如果发生 I/O 错误则抛出 IOException

**示例：**
```java
@OnMessage
public void onMessage(String message, Session session) {
    try {
        session.sendMessage("回显: " + message);
    } catch (IOException e) {
        System.err.println("发送消息时出错: " + e.getMessage());
    }
}
```

##### sendMessage(ByteBuffer data)
向连接的客户端发送二进制消息。

**签名：**
```java
void sendMessage(ByteBuffer data) throws IOException
```

**参数：**
- `data`：要发送的二进制数据

**抛出：**
- 如果发生 I/O 错误则抛出 IOException

**示例：**
```java
public void sendBinaryData(Session session, byte[] data) {
    ByteBuffer buffer = ByteBuffer.wrap(data);
    try {
        session.sendMessage(buffer);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

##### isOpen()
检查会话是否仍然打开。

**签名：**
```java
boolean isOpen()
```

**返回：**
- 如果会话打开则返回 true，否则返回 false

**示例：**
```java
public void broadcastMessage(String message, Collection<Session> sessions) {
    for (Session session : sessions) {
        if (session.isOpen()) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                // 处理错误或移除会话
            }
        }
    }
}
```

##### close()
关闭会话。

**签名：**
```java
void close() throws IOException
```

**抛出：**
- 如果发生 I/O 错误则抛出 IOException

**示例：**
```java
public void closeSessionIfInactive(Session session) {
    if (session.isOpen() && isSessionInactive(session)) {
        try {
            session.close();
        } catch (IOException e) {
            System.err.println("关闭会话时出错: " + e.getMessage());
        }
    }
}
```

##### getQueryString()
获取打开握手时的查询字符串。

**签名：**
```java
String getQueryString()
```

**返回：**
- 查询字符串，如果没有提供则返回 null

**示例：**
```java
@OnOpen
public void onOpen(Session session) {
    String queryString = session.getQueryString();
    if (queryString != null) {
        Map<String, String> params = parseQueryString(queryString);
        String token = params.get("token");
        // 使用令牌进行认证
    }
}
```

##### getUserProperties()
获取与此会话关联的用户定义属性。

**签名：**
```java
Map<String, Object> getUserProperties()
```

**返回：**
- 用户属性映射

**示例：**
```java
@OnOpen
public void onOpen(Session session) {
    session.getUserProperties().put("connectedAt", System.currentTimeMillis());
    session.getUserProperties().put("userId", extractUserId(session));
}
```

---

## 数据访问工具类

### DataSource

数据库连接工厂的接口。提供到底层数据库的连接。

#### 接口定义
```java
public interface DataSource
```

#### 方法

##### getConnection()
获取数据库连接。

**签名：**
```java
Connection getConnection() throws SQLException
```

**返回：**
- 到数据库的连接

**抛出：**
- 如果发生数据库访问错误则抛出 SQLException

**示例：**
```java
@Autowired
private DataSource dataSource;

public User findById(Long id) throws SQLException {
    String sql = "SELECT * FROM users WHERE id = ?";
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return mapRowToUser(rs);
        }
        return null;
    }
}
```

##### getConnection(String username, String password)
使用特定凭证获取连接。

**签名：**
```java
Connection getConnection(String username, String password) throws SQLException
```

**参数：**
- `username`：数据库用户名
- `password`：用户密码

**返回：**
- 具有指定凭证的数据库连接

**抛出：**
- 如果发生数据库访问错误则抛出 SQLException

---

## JSON 工具类

### ObjectMapper

用于 JSON 序列化和反序列化的工具类。

#### 类定义
```java
public class ObjectMapper
```

#### 方法

##### writeValueAsString(Object value)
将对象序列化为其 JSON 表示形式。

**签名：**
```java
public String writeValueAsString(Object value) throws JsonProcessingException
```

**参数：**
- `value`：要序列化的对象

**返回：**
- 对象的 JSON 字符串表示

**抛出：**
- 如果序列化过程中发生错误则抛出 JsonProcessingException

**示例：**
```java
ObjectMapper mapper = new ObjectMapper();
User user = new User("John", "john@example.com");
String jsonString = mapper.writeValueAsString(user);
// 结果: {"name":"John","email":"john@example.com"}
```

##### readValue(String content, Class<T> valueType)
将 JSON 字符串反序列化为指定类型的对象。

**签名：**
```java
public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException
```

**参数：**
- `content`：要反序列化的 JSON 字符串
- `valueType`：反序列化的目标对象类型

**返回：**
- 指定类型的反序列化对象

**抛出：**
- 如果反序列化过程中发生错误则抛出 JsonProcessingException

**示例：**
```java
ObjectMapper mapper = new ObjectMapper();
String jsonString = "{\"name\":\"John\",\"email\":\"john@example.com\"}";
User user = mapper.readValue(jsonString, User.class);
```

---

## 常用工具方法

### StringUtils

常用字符串操作工具类。

#### 类定义
```java
public class StringUtils
```

#### 方法

##### isEmpty(String str)
检查字符串是否为空或 null。

**签名：**
```java
public static boolean isEmpty(String str)
```

**参数：**
- `str`：要检查的字符串

**返回：**
- 如果字符串为 null 或为空则返回 true，否则返回 false

**示例：**
```java
if (StringUtils.isEmpty(userName)) {
    throw new IllegalArgumentException("用户名不能为空");
}
```

##### hasText(String str)
检查字符串是否包含文本（而不仅仅是空白）。

**签名：**
```java
public static boolean hasText(String str)
```

**参数：**
- `str`：要检查的字符串

**返回：**
- 如果字符串包含文本则返回 true，否则返回 false

**示例：**
```java
if (!StringUtils.hasText(userInput)) {
    // 处理空输入
}
```

### CollectionUtils

用于处理集合的工具类。

#### 类定义
```java
public class CollectionUtils
```

#### 方法

##### isEmpty(Collection<?> collection)
检查集合是否为空或 null。

**签名：**
```java
public static boolean isEmpty(Collection<?> collection)
```

**参数：**
- `collection`：要检查的集合

**返回：**
- 如果集合为 null 或为空则返回 true，否则返回 false

**示例：**
```java
if (CollectionUtils.isEmpty(userList)) {
    // 处理空列表
}
```

##### isNotEmpty(Collection<?> collection)
检查集合是否不为空且不为 null。

**签名：**
```java
public static boolean isNotEmpty(Collection<?> collection)
```

**参数：**
- `collection`：要检查的集合

**返回：**
- 如果集合不为 null 且不为空则返回 true，否则返回 false

**示例：**
```java
if (CollectionUtils.isNotEmpty(activeUsers)) {
    processActiveUsers(activeUsers);
}
```

这些工具类提供了使用 NanoBoot 框架开发应用程序所需的核心功能，涵盖依赖注入、Web 开发、数据访问和常见编程任务。
