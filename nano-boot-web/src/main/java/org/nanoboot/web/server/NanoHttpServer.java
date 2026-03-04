package org.nanoboot.web.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.nanoboot.annotation.Annotation.RequestMapping;
import org.nanoboot.annotation.Annotation.RequestMethod;
import org.nanoboot.annotation.Annotation.GetMapping;
import org.nanoboot.annotation.Annotation.PostMapping;
import org.nanoboot.core.ApplicationContext;
import org.nanoboot.web.handler.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * 简单的HTTP服务器实现
 */
public class NanoHttpServer {

    private HttpServer server;
    private final int port;
    private final Map<String, RequestMappingInfo> mappings = new HashMap<>();
    private ApplicationContext applicationContext;

    public NanoHttpServer(int port) {
        this.port = port;
    }

    /**
     * 设置应用上下文
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 注册控制器类
     */
    public void registerController(Object controller) {
        Class<?> controllerClass = controller.getClass();
        String basePath = "";

        // 获取控制器级别路径
        if (controllerClass.isAnnotationPresent(org.nanoboot.annotation.Annotation.Controller.class)) {
            org.nanoboot.annotation.Annotation.Controller ctrl =
                controllerClass.getAnnotation(org.nanoboot.annotation.Annotation.Controller.class);
            basePath = ctrl.value();
        }

        // 扫描控制器中的方法
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                String fullPath = basePath + mapping.value();

                RequestMethod[] requestMethods = mapping.method();
                if (requestMethods.length == 0) {
                    // 如果没有指定方法，默认支持GET和POST
                    registerMapping(fullPath, RequestMethod.GET, controller, method);
                    registerMapping(fullPath, RequestMethod.POST, controller, method);
                } else {
                    for (RequestMethod rm : requestMethods) {
                        registerMapping(fullPath, rm, controller, method);
                    }
                }
            } else if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String fullPath = basePath + mapping.value();
                registerMapping(fullPath, RequestMethod.GET, controller, method);
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                String fullPath = basePath + mapping.value();
                registerMapping(fullPath, RequestMethod.POST, controller, method);
            }
        }
    }

    /**
     * 注册映射
     */
    private void registerMapping(String path, RequestMethod method, Object controller, Method handler) {
        RequestMappingInfo info = new RequestMappingInfo(controller, handler, method);
        String key = method.name() + ":" + path;
        mappings.put(key, info);
        System.out.println("Mapped \"" + key + "\" onto " + controller.getClass().getSimpleName() + "." + handler.getName() + "()");
    }

    /**
     * 启动服务器
     */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // 注册根路径处理器
        server.createContext("/", new RootHandler());

        // 启动服务器
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("NanoBoot HTTP Server started on port " + port);
    }

    /**
     * 停止服务器
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("NanoBoot HTTP Server stopped");
        }
    }

    /**
     * 根处理器
     */
    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();
            String requestQuery = exchange.getRequestURI().getQuery();
            String requestBody = getRequestBody(exchange);

            String key = requestMethod + ":" + requestPath;
            RequestMappingInfo mapping = mappings.get(key);

            if (mapping != null) {
                try {
                    // 创建请求处理器
                    RequestHandler requestHandler = new RequestHandler(applicationContext);

                    // 解析方法参数
                    Object[] args = requestHandler.resolveMethodParameters(
                        mapping.getMethod(),
                        requestPath,
                        requestQuery,
                        requestBody
                    );

                    // 调用对应的方法
                    Object result = mapping.getMethod().invoke(mapping.getController(), args);

                    // 返回结果
                    String response = result != null ? result.toString() : "OK";
                    sendResponse(exchange, response, 200);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
                }
            } else {
                sendResponse(exchange, "Not Found", 404);
            }
        }

        private String getRequestBody(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod()) ||
                "PUT".equalsIgnoreCase(exchange.getRequestMethod())) {

                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(exchange.getRequestBody()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                }
            }
            return null;
        }

        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    /**
     * 请求映射信息
     */
    private static class RequestMappingInfo {
        private final Object controller;
        private final Method method;
        private final RequestMethod requestMethod;

        public RequestMappingInfo(Object controller, Method method, RequestMethod requestMethod) {
            this.controller = controller;
            this.method = method;
            this.requestMethod = requestMethod;
        }

        public Object getController() { return controller; }
        public Method getMethod() { return method; }
        public RequestMethod getRequestMethod() { return requestMethod; }
    }
}