# WebSocket 模块

WebSocket 模块为 NanoBoot 应用程序提供实时双向通信能力。它支持创建具有实时更新和实时功能的交互式 Web 应用程序。

## 概述

WebSocket 模块实现了 WebSocket 协议，允许客户端和服务器之间建立持久连接。它包括以下功能：

- WebSocket 端点创建
- 会话管理
- 消息广播
- 连接生命周期处理
- 错误管理

## 关键功能

### WebSocket 端点创建

使用 @ServerEndpoint 注解创建 WebSocket 端点：

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("新连接: " + session.getId());
        WebSocketManager.addSession(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("收到消息: " + message);
        WebSocketManager.broadcast(message, session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("连接关闭: " + session.getId());
        WebSocketManager.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("错误: " + throwable.getMessage());
        WebSocketManager.removeSession(session);
    }
}
```

### 路径参数

WebSocket 端点可以捕获路径参数：

```java
@ServerEndpoint("/websocket/room/{roomId}/{userId}")
public class RoomEndpoint {

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("userId") String userId) {
        session.getUserProperties().put("roomId", roomId);
        session.getUserProperties().put("userId", userId);
        RoomManager.joinRoom(roomId, session, userId);
    }
}
```

### 查询参数

从 WebSocket 连接访问查询参数：

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        String queryString = session.getQueryString();
        String token = extractToken(queryString);
        if (!authenticate(token)) {
            session.close();
            return;
        }
        WebSocketManager.addSession(session);
    }
}
```

### 二进制消息

处理二进制 WebSocket 消息：

```java
@ServerEndpoint("/websocket/binary")
public class BinaryEndpoint {

    @OnMessage
    public void onBinaryMessage(ByteBuffer message, Session session) {
        byte[] bytes = new byte[message.remaining()];
        message.get(bytes);
        processBinaryData(bytes, session);
    }
}
```

## 会话管理

### 中央 WebSocket 管理器

在中央位置管理所有 WebSocket 会话：

```java
@Component
public class WebSocketManager {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessions.add(session);
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    public static void removeSession(Session session) {
        sessions.remove(session);
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
                } catch (IOException e) {
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
            } catch (IOException e) {
                userSessions.remove(userId);
            }
        }
    }

    public static int getOnlineCount() {
        return sessions.size();
    }
}
```

### 房间管理

对于具有多个房间或频道的应用程序：

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
                    } catch (IOException e) {
                        // 处理错误
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
}
```

## 消息格式

### JSON 消息格式

标准化使用 JSON 进行消息通信：

```java
public class WebSocketMessage {
    private String type;
    private String from;
    private String to;
    private Object data;
    private long timestamp;

    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing message", e);
        }
    }

    public static WebSocketMessage fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, WebSocketMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing message", e);
        }
    }
}
```

### 消息处理

在端点中处理不同的消息类型：

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            WebSocketMessage wsMessage = WebSocketMessage.fromJson(message);
            switch (wsMessage.getType()) {
                case "chat":
                    handleChatMessage(wsMessage, session);
                    break;
                case "typing":
                    handleTypingMessage(wsMessage, session);
                    break;
                default:
                    sendErrorMessage(session, "Unknown message type");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }
}
```

## 使用场景

### 实时通知

向用户发送实时通知：

```java
@Component
public class NotificationService {

    public void sendNotification(String userId, String message) {
        WebSocketMessage notification = new WebSocketMessage("notification",
            Map.of("message", message, "timestamp", System.currentTimeMillis()));
        WebSocketManager.sendMessageToUser(userId, notification.toJson());
    }

    public void sendBroadcastNotification(String message) {
        WebSocketMessage notification = new WebSocketMessage("broadcast",
            Map.of("message", message, "timestamp", System.currentTimeMillis()));
        WebSocketManager.broadcast(notification.toJson());
    }
}
```

### 实时仪表板更新

提供实时仪表板更新：

```java
@Component
public class DashboardService {

    public void updateStatistics() {
        Map<String, Object> stats = calculateStats();
        WebSocketMessage update = new WebSocketMessage("dashboard-update", stats);
        WebSocketManager.broadcast(update.toJson());
    }
}
```

## 最佳实践

### 1. 连接管理
- 及时清理关闭的连接
- 监控连接数
- 实施连接超时
- 使用心跳/ping-pong 进行活力检查

### 2. 消息验证
- 验证所有传入消息
- 实施消息大小限制
- 检查消息格式和结构
- 防止消息注入攻击

### 3. 错误处理
- 优雅地处理 WebSocket 错误
- 在客户端实施重连逻辑
- 记录连接和消息错误
- 关闭损坏的连接

### 4. 安全
- 验证 WebSocket 来源（CORS）
- 在建立会话前认证连接
- 实施限流
- 清理消息内容

### 5. 可扩展性
- 在集群环境中使用外部消息队列
- 考虑 WebSocket 负载均衡的粘性会话
- 监控大量连接的内存使用
- 实施每个用户/IP 的连接限制

## 配置

在 application.properties 中配置 WebSocket 设置：

```properties
# WebSocket 配置
websocket.enabled=true
websocket.path.prefix=/websocket
websocket.max.text.message.buffer.size=8192
websocket.max.binary.message.buffer.size=8192
websocket.max.session.idle.timeout=300000
websocket.connection.limits.per.ip=10
```

WebSocket 模块支持在 NanoBoot 应用程序中进行实时通信，实现实时聊天、实时通知、协作编辑和实时仪表板更新等功能。
