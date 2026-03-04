package org.nanoboot.example.websocket;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.websocket.WebSocketEndpoint;

import javax.websocket.Session;

/**
 * 示例WebSocket端点实现
 */
@Component
public class ChatWebSocketEndpoint extends WebSocketEndpoint {

    @Override
    protected void onConnectionEstablished(Session session) {
        System.out.println("WebSocket connection established: " + session.getId());
        // 发送欢迎消息
        sendToSession(session, "Welcome to the chat!");
    }

    @Override
    protected void handleMessage(String message, Session session) {
        System.out.println("Received message from " + session.getId() + ": " + message);
        // 广播消息给所有连接的客户端
        broadcast("[" + session.getId() + "] " + message);
    }

    @Override
    protected void onConnectionClosed(Session session) {
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    @Override
    protected void handleError(Session session, Throwable throwable) {
        System.err.println("WebSocket error in session " + session.getId() + ": " + throwable.getMessage());
    }
}