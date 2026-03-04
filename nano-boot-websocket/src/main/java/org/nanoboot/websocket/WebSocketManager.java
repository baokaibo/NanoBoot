package org.nanoboot.websocket;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Value;

import javax.websocket.server.ServerEndpointConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket管理器，负责管理和配置WebSocket端点
 */
@Component
public class WebSocketManager {

    private final Map<String, Class<? extends WebSocketEndpoint>> endpointClasses = new HashMap<>();

    @Value("${websocket.path:/ws}")
    private String defaultPath;

    /**
     * 注册WebSocket端点
     */
    public void registerEndpoint(String path, Class<? extends WebSocketEndpoint> endpointClass) {
        endpointClasses.put(path, endpointClass);
    }

    /**
     * 获取端点配置
     */
    public ServerEndpointConfig getEndpointConfig(String path) {
        Class<? extends WebSocketEndpoint> endpointClass = endpointClasses.get(path);
        if (endpointClass != null) {
            return ServerEndpointConfig.Builder.create(endpointClass, path).build();
        }
        return null;
    }

    /**
     * 获取所有注册的端点路径
     */
    public String[] getRegisteredEndpoints() {
        return endpointClasses.keySet().toArray(new String[0]);
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String getDefaultPath() {
        return defaultPath;
    }
}