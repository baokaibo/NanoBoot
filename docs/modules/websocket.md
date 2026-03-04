# WebSocket Module

The WebSocket module provides real-time, bidirectional communication capabilities for NanoBoot applications. It enables the creation of interactive web applications with live updates and real-time features.

## Overview

The WebSocket module implements the WebSocket protocol, allowing for persistent connections between clients and servers. It includes features for:

- WebSocket endpoint creation
- Session management
- Message broadcasting
- Connection lifecycle handling
- Error management

## Key Features

### WebSocket Endpoint Creation

Create WebSocket endpoints using the `@ServerEndpoint` annotation:

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
        WebSocketManager.addSession(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Process received message
        System.out.println("Received: " + message);
        WebSocketManager.broadcast(message, session); // Broadcast to others
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + session.getId());
        WebSocketManager.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
        WebSocketManager.removeSession(session);
    }
}

## Advanced WebSocket Features

### Path Parameters

WebSocket endpoints can capture path parameters:

```java
@ServerEndpoint("/websocket/room/{roomId}/{userId}")
public class RoomEndpoint {

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId, @PathParam("userId") String userId) {
        // Extract parameters from the endpoint URL
        session.getUserProperties().put("roomId", roomId);
        session.getUserProperties().put("userId", userId);

        RoomManager.joinRoom(roomId, session, userId);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        String userId = (String) session.getUserProperties().get("userId");

        // Process message in the context of the room
        RoomManager.processMessage(roomId, userId, message, session);
    }

    @OnClose
    public void onClose(Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        String userId = (String) session.getUserProperties().get("userId");

        RoomManager.leaveRoom(roomId, userId);
    }
}
```

### Query Parameters

Access query parameters from WebSocket connections:

```java
@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        // Access query parameters
        String queryString = session.getQueryString();
        Map<String, String> params = parseQueryString(queryString);

        String token = params.get("token");
        String userId = params.get("userId");

        // Authenticate the user
        if (!authenticate(token, userId)) {
            session.close();
            return;
        }

        // Store user info
        session.getUserProperties().put("authenticated", true);
        session.getUserProperties().put("userId", userId);

        WebSocketManager.addSession(session);
    }

    private boolean authenticate(String token, String userId) {
        // Implement authentication logic
        return token != null && isValidToken(token, userId);
    }
}
```

### Binary Messages

Handle binary WebSocket messages:

```java
@ServerEndpoint("/websocket/binary")
public class BinaryEndpoint {

    @OnMessage
    public void onBinaryMessage(ByteBuffer message, Session session) {
        // Process binary data
        byte[] bytes = new byte[message.remaining()];
        message.get(bytes);

        // Handle binary message (e.g., image upload, file transfer)
        processBinaryData(bytes, session);
    }

    @OnMessage
    public void onTextMessage(String message, Session session) {
        // Process text messages
        System.out.println("Text message: " + message);
    }

    private void processBinaryData(byte[] data, Session session) {
        // Process binary data based on your needs
        // This could be file uploads, image processing, etc.
    }
}
```

## Session Management

### Central WebSocket Manager

Manage all WebSocket sessions in a central location:

```java
@Component
public class WebSocketManager {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    public static void addSession(Session session) {
        sessions.add(session);

        // Extract and store user info
        String userId = (String) session.getUserProperties().get("userId");
        if (userId != null) {
            userSessions.put(userId, session);
        }

        logger.info("Session added: {}, total sessions: {}", session.getId(), sessions.size());
    }

    public static void removeSession(Session session) {
        sessions.remove(session);

        // Remove from user sessions map
        String userId = (String) session.getUserProperties().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
        }

        logger.info("Session removed: {}, remaining sessions: {}", session.getId(), sessions.size());
    }

    public static void broadcast(String message) {
        Iterator<Session> iterator = sessions.iterator();
        while (iterator.hasNext()) {
            Session session = iterator.next();
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    logger.error("Error broadcasting to session {}: {}", session.getId(), e.getMessage());
                    // Remove broken session
                    iterator.remove();
                }
            }
        }
    }

    public static void broadcast(String message, Session excludeSession) {
        for (Session session : sessions) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    logger.error("Error broadcasting to session {}: {}", session.getId(), e.getMessage());
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
                logger.error("Error sending message to user {}: {}", userId, e.getMessage());
                userSessions.remove(userId);
            }
        }
    }

    public static int getOnlineCount() {
        return sessions.size();
    }
}
```

### Room Management

For applications with multiple rooms or channels:

```java
@Component
public class RoomManager {

    private static final Map<String, Set<Session>> roomSessions = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, String>> roomUsers = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);

    public static void joinRoom(String roomId, Session session, String userId, String userName) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);

        Map<String, String> users = roomUsers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>());
        users.put(userId, userName);

        logger.info("User {} joined room {}", userId, roomId);
    }

    public static void leaveRoom(String roomId, String userId) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            sessions.removeIf(session -> {
                String sessionUserId = (String) session.getUserProperties().get("userId");
                return userId.equals(sessionUserId);
            });

            // Clean up empty rooms
            if (sessions.isEmpty()) {
                roomSessions.remove(roomId);
                roomUsers.remove(roomId);
                logger.info("Room {} is now empty and cleaned up", roomId);
            }
        }

        // Remove from room users map
        Map<String, String> users = roomUsers.get(roomId);
        if (users != null) {
            users.remove(userId);
        }

        logger.info("User {} left room {}", userId, roomId);
    }

    public static void broadcastToRoom(String roomId, String message) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(message);
                    } catch (IOException e) {
                        logger.error("Error broadcasting to room {}: {}", roomId, e.getMessage());
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
                    } catch (IOException e) {
                        logger.error("Error broadcasting to room {}: {}", roomId, e.getMessage());
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

    // Getters and setters
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
}
```

## Message Formats

### JSON Message Format

Standardize on JSON for message communication:

```java
public class WebSocketMessage {
    private String type;
    private String from;
    private String to;
    private Object data;
    private long timestamp;

    // Constructors, getters, and setters
    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // JSON serialization methods
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

### Message Processing

Process different message types in your endpoint:

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
                case "ping":
                    handlePingMessage(session);
                    break;
                case "join":
                    handleJoinMessage(wsMessage, session);
                    break;
                case "leave":
                    handleLeaveMessage(wsMessage, session);
                    break;
                default:
                    // Unknown message type
                    sendErrorMessage(session, "Unknown message type: " + wsMessage.getType());
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    private void handleChatMessage(WebSocketMessage message, Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        String userId = (String) session.getUserProperties().get("userId");

        // Create response message
        WebSocketMessage response = new WebSocketMessage("chat",
            Map.of(
                "from", userId,
                "content", message.getData(),
                "timestamp", System.currentTimeMillis()
            )
        );

        // Broadcast to room (excluding sender)
        RoomManager.broadcastToRoom(roomId, response.toJson(), session);
    }

    private void handleTypingMessage(WebSocketMessage message, Session session) {
        String roomId = (String) session.getUserProperties().get("roomId");
        String userId = (String) session.getUserProperties().get("userId");
        boolean isTyping = (Boolean) message.getData();

        WebSocketMessage response = new WebSocketMessage("typing",
            Map.of(
                "from", userId,
                "isTyping", isTyping
            )
        );

        RoomManager.broadcastToRoom(roomId, response.toJson(), session);
    }

    private void handlePingMessage(Session session) {
        WebSocketMessage pong = new WebSocketMessage("pong",
            Map.of("timestamp", System.currentTimeMillis()));
        try {
            session.sendMessage(pong.toJson());
        } catch (IOException e) {
            WebSocketManager.removeSession(session);
        }
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        WebSocketMessage error = new WebSocketMessage("error",
            Map.of("message", errorMessage));
        try {
            session.sendMessage(error.toJson());
        } catch (IOException e) {
            WebSocketManager.removeSession(session);
        }
    }
}
```

## Configuration

### WebSocket Settings

Configure WebSocket settings in `application.properties`:

```properties
# WebSocket Configuration
websocket.enabled=true
websocket.path.prefix=/websocket
websocket.max.text.message.buffer.size=8192
websocket.max.binary.message.buffer.size=8192
websocket.max.session.idle.timeout=300000  # 5 minutes
websocket.connection.limits.per.ip=10      # Max connections per IP
websocket.ping.interval=30000              # Ping interval in ms
websocket.pong.timeout=10000               # Pong timeout in ms

# Reconnection settings
websocket.reconnect.max.attempts=5
websocket.reconnect.initial.delay.ms=1000
websocket.reconnect.multiplier=2
```

## Use Cases

### Real-time Notifications

Send real-time notifications to users:

```java
@Component
public class NotificationService {

    public void sendNotification(String userId, String message) {
        WebSocketMessage notification = new WebSocketMessage("notification",
            Map.of(
                "message", message,
                "type", "info",
                "timestamp", System.currentTimeMillis()
            )
        );

        WebSocketManager.sendMessageToUser(userId, notification.toJson());
    }

    public void sendBroadcastNotification(String message) {
        WebSocketMessage notification = new WebSocketMessage("broadcast",
            Map.of(
                "message", message,
                "timestamp", System.currentTimeMillis()
            )
        );

        WebSocketManager.broadcast(notification.toJson());
    }
}
```

### Live Dashboard Updates

Provide real-time dashboard updates:

```java
@Component
public class DashboardService {

    public void updateStatistics() {
        // Calculate statistics
        Map<String, Object> stats = calculateStats();

        WebSocketMessage update = new WebSocketMessage("dashboard-update", stats);

        // Broadcast to all connected dashboards
        WebSocketManager.broadcast(update.toJson());
    }

    private Map<String, Object> calculateStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("onlineUsers", WebSocketManager.getOnlineCount());
        stats.put("serverTime", System.currentTimeMillis());
        stats.put("messagesPerSecond", getMessageRate());
        return stats;
    }
}
```

## Best Practices

### 1. Connection Management
- Always clean up closed connections promptly
- Monitor connection counts
- Implement connection timeouts
- Use heartbeat/ping-pong for liveness checks

### 2. Message Validation
- Validate all incoming messages
- Implement message size limits
- Check message format and structure
- Prevent message injection attacks

### 3. Error Handling
- Handle WebSocket errors gracefully
- Implement reconnection logic on the client side
- Log connection and message errors
- Close broken connections

### 4. Security
- Validate WebSocket origins (CORS)
- Authenticate connections before establishing sessions
- Implement rate limiting
- Sanitize message content

### 5. Scalability
- Use external message queues for clustering
- Consider sticky sessions for WebSocket load balancing
- Monitor memory usage with many connections
- Implement connection limits per user/IP

## Performance Considerations

### Connection Limits
Set appropriate limits based on your server capacity and expected load.

### Message Broadcasting
- For selective broadcasting, use targeted sessions instead of broadcasting to all
- Implement message batching for high-frequency updates
- Consider message queuing for slow consumers

### Memory Management
- Monitor memory usage with increasing connections
- Implement proper session cleanup
- Consider using off-heap storage for large payloads

The WebSocket module enables real-time communication in NanoBoot applications, allowing for features like live chat, real-time notifications, collaborative editing, and live dashboard updates.