# WebSocket

NanoBoot 为客户端和服务器之间的实时双向通信提供 WebSocket 支持。WebSocket 模块使您能够构建具有实时更新和实时功能的交互式应用程序。

## 设置 WebSocket

### 添加依赖项

要使用 WebSocket 功能，请将 WebSocket 模块添加到您的 `pom.xml`：

```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-websocket</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 创建 WebSocket 端点

### 基础 WebSocket 端点

使用 `@ServerEndpoint` 注解创建 WebSocket 端点：

```java
import org.nanoboot.websocket.annotation.ServerEndpoint;
import org.nanoboot.websocket.Session;
import org.nanoboot.websocket.CloseReason;

@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("新连接: " + session.getId());
        // 将会话添加到活动连接
        WebSocketManager.addSession(session);

        // 发送欢迎消息
        session.sendMessage("{\"type\":\"welcome\",\"message\":\"已连接到聊天！\"}");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息: " + message);

        // 向所有连接的客户端广播消息
        WebSocketManager.broadcast(message, session); // 排除发送者
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("连接关闭: " + session.getId() +
                          ", 原因: " + closeReason.getReasonPhrase());
        // 从活动连接中移除会话
        WebSocketManager.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生错误: " + throwable.getMessage());
        // 适当处理错误
        WebSocketManager.removeSession(session);
    }
}
```

### 高级 WebSocket 端点

更复杂的 WebSocket，包含用户认证和房间管理：

```java
@ServerEndpoint("/websocket/room/{roomId}")
public class RoomChatEndpoint {

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        // 从会话或查询参数中提取用户信息
        String userId = extractUserId(session);
        String userName = extractUserName(session);

        // 加入房间
        RoomManager.joinRoom(roomId, session, userId, userName);

        // 通知房间有新用户加入
        String joinMessage = String.format(
            "{\"type\":\"join\",\"userId\":\"%s\",\"userName\":\"%s\",\"timestamp\":%d}",
            userId, userName, System.currentTimeMillis()
        );
        RoomManager.broadcastToRoom(roomId, joinMessage, session);

        // 发送当前房间用户列表
        List<RoomUser> roomUsers = RoomManager.getRoomUsers(roomId);
        String userList = "{\"type\":\"userList\",\"users\":" + toJson(roomUsers) + "}";
        session.sendMessage(userList);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId) {
        try {
            // 解析传入消息
            JsonNode jsonMsg = parseJson(message);
            String messageType = jsonMsg.get("type").asText();

            switch (messageType) {
                case "chat":
                    handleChatMessage(jsonMsg, session, roomId);
                    break;
                case "typing":
                    handleTypingMessage(jsonMsg, session, roomId);
                    break;
                case "private":
                    handlePrivateMessage(jsonMsg, session, roomId);
                    break;
                default:
                    // 未知消息类型
                    break;
            }
        } catch (Exception e) {
            session.sendMessage("{\"type\":\"error\",\"message\":\"无效的消息格式\"}");
        }
    }

    private void handleChatMessage(JsonNode jsonMsg, Session session, String roomId) {
        String userId = extractUserId(session);
        String content = jsonMsg.get("content").asText();

        String chatMessage = String.format(
            "{\"type\":\"chat\",\"userId\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
            userId, content, System.currentTimeMillis()
        );

        RoomManager.broadcastToRoom(roomId, chatMessage, session);
    }

    private void handleTypingMessage(JsonNode jsonMsg, Session session, String roomId) {
        String userId = extractUserId(session);
        boolean isTyping = jsonMsg.get("isTyping").asBoolean();

        String typingMessage = String.format(
            "{\"type\":\"typing\",\"userId\":\"%s\",\"isTyping\":%s}",
            userId, isTyping
        );

        RoomManager.broadcastToRoom(roomId, typingMessage, session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason, @PathParam("roomId") String roomId) {
        String userId = extractUserId(session);
        String userName = extractUserName(session);

        // 离开房间
        RoomManager.leaveRoom(roomId, session);

        // 通知房间用户离开
        String leaveMessage = String.format(
            "{\"type\":\"leave\",\"userId\":\"%s\",\"userName\":\"%s\",\"timestamp\":%d}",
            userId, userName, System.currentTimeMillis()
        );
        RoomManager.broadcastToRoom(roomId, leaveMessage, session);
    }

    private String extractUserId(Session session) {
        // 从会话中提取用户 ID
        // 实现取决于您的认证方法
        return session.getQueryParameter("userId");
    }
}
```

## WebSocket 管理器

集中管理所有 WebSocket 会话：

```java
@Component
public class WebSocketManager {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessions.add(session);

        // 如果可用，按用户 ID 存储
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    public static void removeSession(Session session) {
        sessions.remove(session);

        // 从用户会话映射中移除
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    public static void broadcast(String message) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("广播到会话 " + session.getId() + " 时出错: " + e.getMessage());
                    // 移除损坏的会话
                    removeSession(session);
                }
            }
        }
    }

    public static void broadcast(String message, Session excludeSession) {
        for (Session session : sessions) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("广播到会话 " + session.getId() + " 时出错: " + e.getMessage());
                    removeSession(session);
                }
            }
        }
    }

    public static void sendMessageToUser(String userId, String message) {
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(message);
            } catch (Exception e) {
                System.err.println("发送消息给用户 " + userId + " 时出错: " + e.getMessage());
                userSessions.remove(userId);
            }
        }
    }

    public static int getOnlineCount() {
        return sessions.size();
    }

    private static String extractUserId(Session session) {
        // 从会话中提取用户 ID
        return session.getQueryParameter("userId");
    }
}
```

## 房间管理

对于具有聊天室或频道的更高级场景：

```java
@Component
public class RoomManager {

    private static final Map<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, String>> roomUsers = new ConcurrentHashMap<>();

    public static void joinRoom(String roomId, Session session, String userId, String userName) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        Map<String, String> users = roomUsers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
        users.put(userId, userName);
    }

    public static void leaveRoom(String roomId, Session session) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
                roomUsers.remove(roomId);
            }
        }
    }

    public static void broadcastToRoom(String roomId, String message) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(message);
                    } catch (Exception e) {
                        System.err.println("广播到房间 " + roomId + " 时出错: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static void broadcastToRoom(String roomId, String message, Session excludeSession) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen() && !session.equals(excludeSession)) {
                    try {
                        session.sendMessage(message);
                    } catch (Exception e) {
                        System.err.println("广播到房间 " + roomId + " 时出错: " + e.getMessage());
                    }
                }
            }
        }
    }

    public static List<RoomUser> getRoomUsers(String roomId) {
        Map<String, String> users = roomUsers.get(roomId);
        if (users != null) {
            return users.entrySet().stream()
                .map(entry -> new RoomUser(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static int getRoomUserCount(String roomId) {
        Set<Session> sessions = roomSessions.get(roomId);
        return sessions != null ? sessions.size() : 0;
    }
}

class RoomUser {
    private String userId;
    private String userName;

    public RoomUser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // Getter 和 Setter
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
}
```

## 客户端 JavaScript

连接到您的 WebSocket 的客户端代码示例：

```javascript
class WebSocketClient {
    constructor(url, onMessageCallback) {
        this.url = url;
        this.onMessageCallback = onMessageCallback;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.connect();
    }

    connect() {
        this.ws = new WebSocket(this.url);

        this.ws.onopen = (event) => {
            console.log('已连接到 WebSocket');
            this.reconnectAttempts = 0;
        };

        this.ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.onMessageCallback(data);
        };

        this.ws.onclose = (event) => {
            console.log('已断开 WebSocket 连接:', event.reason);
            this.attemptReconnect();
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket 错误:', error);
        };
    }

    send(message) {
        if (this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        } else {
            console.warn('WebSocket 未连接，消息未发送:', message);
        }
    }

    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`尝试重新连接 (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

            setTimeout(() => {
                this.connect();
            }, 1000 * this.reconnectAttempts); // 渐进退避
        } else {
            console.error('已达到最大重连次数');
        }
    }

    close() {
        if (this.ws) {
            this.ws.close();
        }
    }
}

// 使用示例
const wsClient = new WebSocketClient('ws://localhost:8080/websocket/chat', (data) => {
    switch(data.type) {
        case 'chat':
            displayChatMessage(data.userId, data.content, data.timestamp);
            break;
        case 'join':
            displayUserJoined(data.userName);
            break;
        case 'leave':
            displayUserLeft(data.userName);
            break;
        case 'welcome':
            console.log(data.message);
            break;
    }
});

// 发送消息
function sendChatMessage(content) {
    wsClient.send({
        type: 'chat',
        content: content,
        timestamp: Date.now()
    });
}
```

## 使用场景

### 实时通知
```java
@Component
public class NotificationService {

    public void sendNotification(String userId, String message) {
        String notification = String.format(
            "{\"type\":\"notification\",\"message\":\"%s\",\"timestamp\":%d}",
            message, System.currentTimeMillis()
        );
        WebSocketManager.sendMessageToUser(userId, notification);
    }

    public void broadcastSystemAlert(String message) {
        String alert = String.format(
            "{\"type\":\"alert\",\"message\":\"%s\",\"severity\":\"high\",\"timestamp\":%d}",
            message, System.currentTimeMillis()
        );
        WebSocketManager.broadcast(alert);
    }
}
```

### 实时分析
```java
@ServerEndpoint("/websocket/analytics")
public class AnalyticsEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        // 解析分析事件
        JsonObject event = Json.parse(message).asObject();
        String eventType = event.getString("type", "");

        // 处理并存储分析数据
        AnalyticsService.recordEvent(eventType, session, event);

        // 可选发送确认
        session.sendMessage("{\"type\":\"ack\",\"timestamp\":" + System.currentTimeMillis() + "}");
    }
}
```

## 最佳实践

### 1. 错误处理
- 始终优雅地处理 WebSocket 错误
- 实施连接恢复机制
- 记录错误以便调试

### 2. 安全
- 验证所有传入消息
- 为 WebSocket 连接实施认证
- 防止消息泛滥攻击
- 广播前清理消息

### 3. 资源管理
- 及时清理关闭的连接
- 监控连接数
- 实施连接超时
- 关闭未使用的连接

### 4. 消息格式
- 使用一致的 JSON 消息格式
- 包含用于路由的消息类型
- 添加用于排序的时间戳
- 验证消息结构

### 5. 可扩展性
- 在集群环境中使用外部存储进行会话管理
- 为重处理实施消息队列
- 考虑 WebSocket 连接的负载均衡

## 配置

在 `application.properties` 中配置 WebSocket 设置：

```properties
# WebSocket 配置
websocket.enabled=true
websocket.path.prefix=/websocket
websocket.max.text.message.buffer.size=8192
websocket.max.binary.message.buffer.size=8192
websocket.max.session.idle.timeout=300000  # 5 分钟
websocket.connection.limits.per.ip=10      # 每个 IP 的最大连接数
```

NanoBoot 中的 WebSocket 模块为实时通信提供了健壮的基础，使您能够构建具有实时更新、聊天系统、通知和协作功能的交互式应用程序。
