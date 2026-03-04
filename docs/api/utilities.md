# Utilities

Reference for utility classes and helper functions available in the NanoBoot framework.

## Core Utilities

### ApplicationContext

Central interface to provide configuration for an application. The ApplicationContext manages the lifecycle of beans and provides facilities for dependency injection.

#### Class Definition
```java
public class ApplicationContext
```

#### Methods

##### getBean(Class<T> type)
Retrieves a bean of the specified type.

**Signature:**
```java
public <T> T getBean(Class<T> type)
```

**Parameters:**
- `type`: the type of the bean to retrieve

**Returns:**
- the bean instance of the specified type

**Throws:**
- `NoSuchBeanDefinitionException` if no bean of the given type is found

**Example:**
```java
UserService userService = context.getBean(UserService.class);
```

##### getBean(String name)
Retrieves a bean with the specified name.

**Signature:**
```java
public Object getBean(String name)
```

**Parameters:**
- `name`: the name of the bean to retrieve

**Returns:**
- the bean instance with the specified name

**Throws:**
- `NoSuchBeanDefinitionException` if no bean with the given name is found

**Example:**
```java
Object userService = context.getBean("userService");
```

##### getBean(String name, Class<T> type)
Retrieves a bean of the specified type with the specified name.

**Signature:**
```java
public <T> T getBean(String name, Class<T> type)
```

**Parameters:**
- `name`: the name of the bean to retrieve
- `type`: the type of the bean to retrieve

**Returns:**
- the bean instance with the specified name and type

**Example:**
```java
UserService userService = context.getBean("customUserService", UserService.class);
```

##### containsBean(String name)
Checks if a bean with the specified name exists.

**Signature:**
```java
public boolean containsBean(String name)
```

**Parameters:**
- `name`: the name of the bean to check

**Returns:**
- `true` if the bean exists, `false` otherwise

**Example:**
```java
boolean exists = context.containsBean("userService");
```

##### containsBean(Class<?> type)
Checks if a bean of the specified type exists.

**Signature:**
```java
public boolean containsBean(Class<?> type)
```

**Parameters:**
- `type`: the type of the bean to check

**Returns:**
- `true` if the bean exists, `false` otherwise

**Example:**
```java
boolean exists = context.containsBean(UserService.class);
```

---

### NanoBootApplicationRunner

Main class responsible for running NanoBoot applications. This class handles the initialization and startup of the application context.

#### Class Definition
```java
public class NanoBootApplicationRunner
```

#### Methods

##### run(Class<?> mainClass, String[] args)
Runs the application with the given main class and arguments.

**Signature:**
```java
public static void run(Class<?> mainClass, String[] args)
```

**Parameters:**
- `mainClass`: the main application class annotated with `@NanoBootApplication`
- `args`: application arguments passed from the command line

**Example:**
```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

##### runWithCustomContext(Class<?> mainClass, String[] args, ApplicationContext applicationContext)
Runs the application with a custom application context.

**Signature:**
```java
public static void runWithCustomContext(Class<?> mainClass, String[] args,
                                      ApplicationContext applicationContext)
```

**Parameters:**
- `mainClass`: the main application class annotated with `@NanoBootApplication`
- `args`: application arguments passed from the command line
- `applicationContext`: custom application context to use

**Example:**
```java
ApplicationContext customContext = new CustomApplicationContext();
NanoBootApplicationRunner.runWithCustomContext(MyAppApplication.class, args, customContext);
```

---

## Web Utilities

### Session (WebSocket)

Interface representing a WebSocket session, providing methods for communication with the connected client.

#### Interface Definition
```java
public interface Session
```

#### Methods

##### getId()
Gets the unique identifier for this session.

**Signature:**
```java
String getId()
```

**Returns:**
- the session ID

**Example:**
```java
@OnOpen
public void onOpen(Session session) {
    String sessionId = session.getId();
    System.out.println("New session: " + sessionId);
}
```

##### sendMessage(String text)
Sends a text message to the connected client.

**Signature:**
```java
void sendMessage(String text) throws IOException
```

**Parameters:**
- `text`: the text message to send

**Throws:**
- `IOException` if an I/O error occurs during message transmission

**Example:**
```java
@OnMessage
public void onMessage(String message, Session session) {
    try {
        session.sendMessage("Echo: " + message);
    } catch (IOException e) {
        System.err.println("Error sending message: " + e.getMessage());
    }
}
```

##### sendMessage(ByteBuffer data)
Sends a binary message to the connected client.

**Signature:**
```java
void sendMessage(ByteBuffer data) throws IOException
```

**Parameters:**
- `data`: the binary data to send

**Throws:**
- `IOException` if an I/O error occurs during message transmission

**Example:**
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
Checks if the session is still open.

**Signature:**
```java
boolean isOpen()
```

**Returns:**
- `true` if the session is open, `false` otherwise

**Example:**
```java
public void broadcastMessage(String message, Collection<Session> sessions) {
    for (Session session : sessions) {
        if (session.isOpen()) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                // Handle error or remove session
            }
        }
    }
}
```

##### close()
Closes the session.

**Signature:**
```java
void close() throws IOException
```

**Throws:**
- `IOException` if an I/O error occurs during session closure

**Example:**
```java
public void closeSessionIfInactive(Session session) {
    if (session.isOpen() && isSessionInactive(session)) {
        try {
            session.close();
        } catch (IOException e) {
            System.err.println("Error closing session: " + e.getMessage());
        }
    }
}
```

##### getQueryString()
Gets the query string from the opening handshake.

**Signature:**
```java
String getQueryString()
```

**Returns:**
- the query string, or `null` if none was provided

**Example:**
```java
@OnOpen
public void onOpen(Session session) {
    String queryString = session.getQueryString();
    if (queryString != null) {
        Map<String, String> params = parseQueryString(queryString);
        String token = params.get("token");
        // Use token for authentication
    }
}
```

##### getUserProperties()
Gets user-defined properties associated with this session.

**Signature:**
```java
Map<String, Object> getUserProperties()
```

**Returns:**
- a map of user properties

**Example:**
```java
@OnOpen
public void onOpen(Session session) {
    session.getUserProperties().put("connectedAt", System.currentTimeMillis());
    session.getUserProperties().put("userId", extractUserId(session));
}
```

---

## Data Access Utilities

### DataSource

Interface for database connection factories. Provides connections to the underlying database.

#### Interface Definition
```java
public interface DataSource
```

#### Methods

##### getConnection()
Obtains a connection to the database.

**Signature:**
```java
Connection getConnection() throws SQLException
```

**Returns:**
- a connection to the database

**Throws:**
- `SQLException` if a database access error occurs

**Example:**
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
Obtains a connection with specific credentials.

**Signature:**
```java
Connection getConnection(String username, String password) throws SQLException
```

**Parameters:**
- `username`: the database user name
- `password`: the user's password

**Returns:**
- a connection to the database with the specified credentials

**Throws:**
- `SQLException` if a database access error occurs

---

## JSON Utilities

### ObjectMapper

Utility class for JSON serialization and deserialization.

#### Class Definition
```java
public class ObjectMapper
```

#### Methods

##### writeValueAsString(Object value)
Serializes an object to its JSON representation.

**Signature:**
```java
public String writeValueAsString(Object value) throws JsonProcessingException
```

**Parameters:**
- `value`: the object to serialize

**Returns:**
- JSON string representation of the object

**Throws:**
- `JsonProcessingException` if an error occurs during serialization

**Example:**
```java
ObjectMapper mapper = new ObjectMapper();
User user = new User("John", "john@example.com");
String jsonString = mapper.writeValueAsString(user);
// Result: {"name":"John","email":"john@example.com"}
```

##### readValue(String content, Class<T> valueType)
Deserializes a JSON string to an object of the specified type.

**Signature:**
```java
public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException
```

**Parameters:**
- `content`: JSON string to deserialize
- `valueType`: the type of object to deserialize to

**Returns:**
- deserialized object of the specified type

**Throws:**
- `JsonProcessingException` if an error occurs during deserialization

**Example:**
```java
ObjectMapper mapper = new ObjectMapper();
String jsonString = "{\"name\":\"John\",\"email\":\"john@example.com\"}";
User user = mapper.readValue(jsonString, User.class);
```

---

## Common Utility Methods

### StringUtils

Common string manipulation utilities.

#### Class Definition
```java
public class StringUtils
```

#### Methods

##### isEmpty(String str)
Checks if a string is null or empty.

**Signature:**
```java
public static boolean isEmpty(String str)
```

**Parameters:**
- `str`: the string to check

**Returns:**
- `true` if the string is null or empty, `false` otherwise

**Example:**
```java
if (StringUtils.isEmpty(userName)) {
    throw new IllegalArgumentException("Username cannot be empty");
}
```

##### hasText(String str)
Checks if a string contains text (not just whitespace).

**Signature:**
```java
public static boolean hasText(String str)
```

**Parameters:**
- `str`: the string to check

**Returns:**
- `true` if the string contains text, `false` otherwise

**Example:**
```java
if (!StringUtils.hasText(userInput)) {
    // Handle empty input
}
```

### CollectionUtils

Utilities for working with collections.

#### Class Definition
```java
public class CollectionUtils
```

#### Methods

##### isEmpty(Collection<?> collection)
Checks if a collection is null or empty.

**Signature:**
```java
public static boolean isEmpty(Collection<?> collection)
```

**Parameters:**
- `collection`: the collection to check

**Returns:**
- `true` if the collection is null or empty, `false` otherwise

**Example:**
```java
if (CollectionUtils.isEmpty(userList)) {
    // Handle empty list
}
```

##### isNotEmpty(Collection<?> collection)
Checks if a collection is not null and not empty.

**Signature:**
```java
public static boolean isNotEmpty(Collection<?> collection)
```

**Parameters:**
- `collection`: the collection to check

**Returns:**
- `true` if the collection is not null and not empty, `false` otherwise

**Example:**
```java
if (CollectionUtils.isNotEmpty(activeUsers)) {
    processActiveUsers(activeUsers);
}
```

These utilities provide the core functionality needed for developing applications with the NanoBoot framework, covering dependency injection, web development, data access, and common programming tasks.