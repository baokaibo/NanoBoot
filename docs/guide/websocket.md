# WebSocket

NanoBoot provides WebSocket support for real-time, bidirectional communication between clients and servers. The WebSocket module enables you to build interactive applications with live updates and real-time features.

## Setting Up WebSocket

### Adding Dependencies

To use WebSocket features, add the WebSocket module to your `pom.xml`:

```xml
<dependency>
    <groupId>org.nanoboot</groupId>
    <artifactId>nano-boot-websocket</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Creating WebSocket Endpoints

### Basic WebSocket Endpoint

Create a WebSocket endpoint using the `@ServerEndpoint` annotation:

```java
import org.nanoboot.websocket.annotation.ServerEndpoint;
import org.nanoboot.websocket.Session;
import org.nanoboot.websocket.CloseReason;

@ServerEndpoint("/websocket/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
        // Add session to active connections
        WebSocketManager.addSession(session);

        // Send welcome message
        session.sendMessage("{\"type\":\"welcome\",\"message\":\"Connected to chat!\"}");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received: " + message);

        // Broadcast message to all connected clients
        WebSocketManager.broadcast(message, session); // Exclude sender
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + session.getId() +
                          ", reason: " + closeReason.getReasonPhrase());
        // Remove session from active connections
        WebSocketManager.removeSession(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error occurred: " + throwable.getMessage());
        // Handle error appropriately
        WebSocketManager.removeSession(session);
    }
}
```

### Advanced WebSocket Endpoint

More complex WebSocket with user authentication and room management:

```java
@ServerEndpoint("/websocket/room/{roomId}")
public class RoomChatEndpoint {

    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") String roomId) {
        // Extract user info from session or query parameters
        String userId = extractUserId(session);
        String userName = extractUserName(session);

        // Join room
        RoomManager.joinRoom(roomId, session, userId, userName);

        // Notify room about new user
        String joinMessage = String.format(
            "{\"type\":\"join\",\"userId\":\"%s\",\"userName\":\"%s\",\"timestamp\":%d}",
            userId, userName, System.currentTimeMillis()
        );
        RoomManager.broadcastToRoom(roomId, joinMessage, session);

        // Send current room users
        List<RoomUser> roomUsers = RoomManager.getRoomUsers(roomId);
        String userList = "{\"type\":\"userList\",\"users\":" + toJson(roomUsers) + "}";
        session.sendMessage(userList);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("roomId") String roomId) {
        try {
            // Parse incoming message
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
                    // Unknown message type
                    break;
            }
        } catch (Exception e) {
            session.sendMessage("{\"type\":\"error\",\"message\":\"Invalid message format\"}");
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

        // Leave room
        RoomManager.leaveRoom(roomId, session);

        // Notify room about user leaving
        String leaveMessage = String.format(
            "{\"type\":\"leave\",\"userId\":\"%s\",\"userName\":\"%s\",\"timestamp\":%d}",
            userId, userName, System.currentTimeMillis()
        );
        RoomManager.broadcastToRoom(roomId, leaveMessage, session);
    }

    private String extractUserId(Session session) {
        // Extract user ID from session or query parameters
        // Implementation depends on your authentication method
        return session.getQueryParameter("userId");
    }
}
```

## WebSocket Manager

A central manager to handle all WebSocket sessions:

```java
@Component
public class WebSocketManager {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    public static void addSession(Session session) {
        sessions.add(session);

        // Store by user ID if available
        String userId = extractUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    public static void removeSession(Session session) {
        sessions.remove(session);

        // Remove from user sessions map
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
                    System.err.println("Error broadcasting to session " + session.getId() + ": " + e.getMessage());
                    // Remove broken session
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
                    System.err.println("Error broadcasting to session " + session.getId() + ": " + e.getMessage());
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
                System.err.println("Error sending message to user " + userId + ": " + e.getMessage());
                userSessions.remove(userId);
            }
        }
    }

    public static int getOnlineCount() {
        return sessions.size();
    }

    private static String extractUserId(Session session) {
        // Extract user ID from session
        return session.getQueryParameter("userId");
    }
}
```

## Room Management

For more advanced scenarios with chat rooms:

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
                        System.err.println("Error broadcasting to room " + roomId + ": " + e.getMessage());
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
                        System.err.println("Error broadcasting to room " + roomId + ": " + e.getMessage());
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

## Client-Side JavaScript

Example client-side code to connect to your WebSocket:

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
            console.log('Connected to WebSocket');
            this.reconnectAttempts = 0;
        };

        this.ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.onMessageCallback(data);
        };

        this.ws.onclose = (event) => {
            console.log('Disconnected from WebSocket:', event.reason);
            this.attemptReconnect();
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
        };
    }

    send(message) {
        if (this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        } else {
            console.warn('WebSocket not connected, message not sent:', message);
        }
    }

    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

            setTimeout(() => {
                this.connect();
            }, 1000 * this.reconnectAttempts); // Progressive backoff
        } else {
            console.error('Max reconnection attempts reached');
        }
    }

    close() {
        if (this.ws) {
            this.ws.close();
        }
    }
}

// Usage example
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

// Send a message
function sendChatMessage(content) {
    wsClient.send({
        type: 'chat',
        content: content,
        timestamp: Date.now()
    });
}
```

## Use Cases

### Live Notifications
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

### Real-Time Analytics
```java
@ServerEndpoint("/websocket/analytics")
public class AnalyticsEndpoint {

    @OnMessage
    public void onMessage(String message, Session session) {
        // Parse analytics event
        JsonObject event = Json.parse(message).asObject();
        String eventType = event.getString("type", "");

        // Process and store analytics
        AnalyticsService.recordEvent(eventType, session, event);

        // Optionally send confirmation
        session.sendMessage("{\"type\":\"ack\",\"timestamp\":" + System.currentTimeMillis() + "}");
    }
}
```

## Best Practices

### 1. Error Handling
- Always handle WebSocket errors gracefully
- Implement connection recovery mechanisms
- Log errors for debugging

### 2. Security
- Validate all incoming messages
- Implement authentication for WebSocket connections
- Prevent message flooding attacks
- Sanitize messages before broadcasting

### 3. Resource Management
- Clean up closed connections promptly
- Monitor connection counts
- Implement connection timeouts
- Close unused connections

### 4. Message Format
- Use consistent JSON message formats
- Include message types for routing
- Add timestamps for ordering
- Validate message structure

### 5. Scalability
- Use external stores for session management in clustered environments
- Implement message queues for heavy processing
- Consider load balancing for WebSocket connections

## Configuration

Configure WebSocket settings in `application.properties`:

```properties
# WebSocket Configuration
websocket.enabled=true
websocket.path.prefix=/websocket
websocket.max.text.message.buffer.size=8192
websocket.max.binary.message.buffer.size=8192
websocket.max.session.idle.timeout=300000  # 5 minutes
websocket.connection.limits.per.ip=10      # Max connections per IP
```

The WebSocket module in NanoBoot provides a robust foundation for real-time communication, enabling you to build interactive applications with live updates, chat systems, notifications, and collaborative features.