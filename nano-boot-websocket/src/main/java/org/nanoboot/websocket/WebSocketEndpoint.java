package org.nanoboot.websocket;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.core.ApplicationContext;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket端点基类，提供基础的WebSocket功能
 */
@Component
public abstract class WebSocketEndpoint {

    // 存储所有活跃的WebSocket会话
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    // 存储会话到用户标识的映射
    private static final ConcurrentHashMap<Session, String> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * 连接建立时调用
     */
    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        onConnectionEstablished(session);
    }

    /**
     * 接收到消息时调用
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        handleMessage(message, session);
    }

    /**
     * 连接关闭时调用
     */
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        sessionUserMap.remove(session);
        onConnectionClosed(session);
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        handleError(session, throwable);
    }

    /**
     * 向所有客户端广播消息
     */
    public void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    sendMessage(session, message);
                }
            }
        }
    }

    /**
     * 向特定会话发送消息
     */
    public void sendToSession(Session session, String message) {
        if (session != null && session.isOpen()) {
            sendMessage(session, message);
        }
    }

    /**
     * 向特定用户发送消息
     */
    public void sendToUser(String userId, String message) {
        for (Session session : sessions) {
            String user = sessionUserMap.get(session);
            if (user != null && user.equals(userId) && session.isOpen()) {
                sendMessage(session, message);
            }
        }
    }

    /**
     * 发送消息的辅助方法
     */
    private void sendMessage(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.err.println("Error sending message to session: " + e.getMessage());
        }
    }

    /**
     * 子类需要实现的方法 - 处理连接建立
     */
    protected abstract void onConnectionEstablished(Session session);

    /**
     * 子类需要实现的方法 - 处理接收到的消息
     */
    protected abstract void handleMessage(String message, Session session);

    /**
     * 子类需要实现的方法 - 处理连接关闭
     */
    protected abstract void onConnectionClosed(Session session);

    /**
     * 子类需要实现的方法 - 处理错误
     */
    protected abstract void handleError(Session session, Throwable throwable);

    /**
     * 设置用户标识与会话的关联
     */
    protected void setUserForSession(Session session, String userId) {
        sessionUserMap.put(session, userId);
    }

    /**
     * 获取会话的用户标识
     */
    protected String getUserForSession(Session session) {
        return sessionUserMap.get(session);
    }

    /**
     * 获取活跃会话数
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }
}