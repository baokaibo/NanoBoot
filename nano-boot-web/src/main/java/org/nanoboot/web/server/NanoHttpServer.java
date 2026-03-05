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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
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
     * 处理可视化控制台请求
     */
    private void handleConsole(HttpExchange exchange) throws IOException {
        // 从静态文件加载HTML
        String html = loadStaticHtml("console.html");
        if (html != null) {
            sendResponse(exchange, html, 200, "text/html;charset=UTF-8");
        } else {
            sendResponse(exchange, "Console HTML not found", 404, "text/plain;charset=UTF-8");
        }
    }
    
    /**
     * 加载静态HTML文件
     */
    private String loadStaticHtml(String filename) {
        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            if (is != null) {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                return sb.toString();
            }
        } catch (Exception e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 生成可视化控制台HTML - 优化版
     */
    private String generateConsoleHtml() {
        StringBuilder beans = new StringBuilder();
        StringBuilder routes = new StringBuilder();
        StringBuilder config = new StringBuilder();
        StringBuilder dependencyGraph = new StringBuilder();
        int beanCount = 0;

        // 获取已注册的Bean
        if (applicationContext != null) {
            try {
                Method getBeanNamesMethod = applicationContext.getClass().getMethod("getBeanDefinitionNames");
                String[] beanNames = (String[]) getBeanNamesMethod.invoke(applicationContext);
                beanCount = beanNames.length;
                
                // 生成Bean列表和依赖关系数据
                Map<String, List<String>> beanDependencies = new LinkedHashMap<>();
                
                for (String name : beanNames) {
                    Object bean = applicationContext.getBean(name);
                    Class<?> clazz = bean.getClass();
                    
                    // 获取依赖的Bean
                    List<String> deps = new ArrayList<>();
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.isAnnotationPresent(org.nanoboot.annotation.Annotation.Autowired.class)) {
                            deps.add(field.getType().getSimpleName());
                        }
                    }
                    beanDependencies.put(name, deps);
                    
                    beans.append("<tr><td><span class='bean-name'>").append(name)
                         .append("</span></td><td>").append(clazz.getName())
                         .append("</td><td>").append(deps.isEmpty() ? "-" : String.join(", ", deps))
                         .append("</td></tr>");
                }
                
                // 生成依赖关系图数据
                for (Map.Entry<String, List<String>> entry : beanDependencies.entrySet()) {
                    String beanName = entry.getKey();
                    List<String> deps = entry.getValue();
                    if (!deps.isEmpty()) {
                        for (String dep : deps) {
                            dependencyGraph.append("{ from: '").append(beanName)
                                         .append("', to: '").append(dep).append("' },");
                        }
                    }
                }
                
            } catch (Exception e) {
                beans.append("<tr><td colspan='3'>Error loading beans: ").append(e.getMessage()).append("</td></tr>");
            }
        }

        // 获取路由映射
        for (Map.Entry<String, RequestMappingInfo> entry : mappings.entrySet()) {
            String key = entry.getKey();
            RequestMappingInfo info = entry.getValue();
            String methodColor = getMethodColor(key.split(":")[0]);
            routes.append("<tr><td><span class='method-badge' style='background:").append(methodColor)
                 .append("'>").append(key.split(":")[0])
                 .append("</span></td><td>").append(key.split(":")[1])
                 .append("</td><td>").append(info.getController().getClass().getSimpleName())
                 .append(".").append(info.getMethod().getName()).append("()</td></tr>");
        }

        // 获取配置信息
        if (applicationContext != null) {
            try {
                Method getEnvMethod = applicationContext.getClass().getMethod("getEnvironment");
                Object env = getEnvMethod.invoke(applicationContext);
                if (env != null) {
                    Method getPropertiesMethod = env.getClass().getMethod("getProperties");
                    Map<?, ?> props = (Map<?, ?>) getPropertiesMethod.invoke(env);
                    for (Map.Entry<?, ?> entry : props.entrySet()) {
                        config.append("<tr><td>").append(entry.getKey())
                              .append("</td><td>").append(entry.getValue()).append("</td></tr>");
                    }
                }
            } catch (Exception e) {
                config.append("<tr><td colspan='2'>Error loading config: ").append(e.getMessage()).append("</td></tr>");
            }
        }

        // 生成路由下拉选项
        StringBuilder routeOptions = new StringBuilder();
        for (Map.Entry<String, RequestMappingInfo> entry : mappings.entrySet()) {
            String key = entry.getKey();
            routeOptions.append("<option value='").append(key).append("'>").append(key).append("</option>");
        }

        return "<!DOCTYPE html>" +
            "<html><head><meta charset='UTF-8'>" +
            "<title>NanoBoot Admin Console</title>" +
            "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>" +
            "<style>" +
            "*{margin:0;padding:0;box-sizing:border-box}" +
            "body{font-family:'Segoe UI',system-ui,sans-serif;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);min-height:100vh;padding:20px}" +
            ".container{max-width:1400px;margin:0 auto}" +
            ".header{background:rgba(255,255,255,0.95);border-radius:16px;padding:30px;margin-bottom:20px;box-shadow:0 10px 40px rgba(0,0,0,0.2)}" +
            ".header h1{color:#333;font-size:2.5em;display:flex;align-items:center;gap:15px}" +
            ".header h1 i{color:#667eea}" +
            ".lang-btn{float:right;padding:8px 16px;border:none;background:#667eea;color:white;border-radius:20px;cursor:pointer;font-size:0.9em;transition:all 0.3s}" +
            ".lang-btn:hover{background:#764ba2}" +
            ".stats{display:flex;gap:20px;margin-top:20px;flex-wrap:wrap}" +
            ".stat-card{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:20px 30px;border-radius:12px;flex:1;min-width:150px}" +
            ".stat-card i{font-size:2em;opacity:0.8}" +
            ".stat-card .number{font-size:2.5em;font-weight:bold;margin:10px 0}" +
            ".stat-card .label{opacity:0.9;font-size:0.9em}" +
            ".tabs{display:flex;gap:5px;background:rgba(255,255,255,0.3);padding:5px;border-radius:12px;flex-wrap:wrap}" +
            ".tab-btn{padding:15px 25px;border:none;background:transparent;color:white;font-size:1em;cursor:pointer;border-radius:8px;transition:all 0.3s;display:flex;align-items:center;gap:8px}" +
            ".tab-btn:hover{background:rgba(255,255,255,0.2)}" +
            ".tab-btn.active{background:white;color:#667eea;box-shadow:0 4px 15px rgba(0,0,0,0.1)}" +
            ".tab-content{background:rgba(255,255,255,0.95);border-radius:16px;padding:30px;margin-top:20px;box-shadow:0 10px 40px rgba(0,0,0,0.2);display:none}" +
            ".tab-content.active{display:block}" +
            "table{width:100%;border-collapse:collapse;background:white;border-radius:8px;overflow:hidden}" +
            "th,td{padding:15px;text-align:left;border-bottom:1px solid #eee}" +
            "th{background:linear-gradient(135deg,#667eea,#764ba2);color:white;font-weight:600}" +
            "tr:hover{background:#f8f9ff}" +
            ".bean-name{font-weight:600;color:#667eea}" +
            ".method-badge{padding:4px 10px;border-radius:20px;font-size:0.85em;font-weight:600;color:white}" +
            ".api-tester{background:linear-gradient(135deg,#f5f7fa,#c3cfe2);padding:25px;border-radius:12px;margin-bottom:20px}" +
            ".api-tester h3{margin-bottom:20px;color:#333;display:flex;align-items:center;gap:10px}" +
            ".form-row{display:flex;gap:15px;margin-bottom:15px;flex-wrap:wrap}" +
            "select,input,textarea{flex:1;padding:12px 15px;border:2px solid #ddd;border-radius:8px;font-size:1em;transition:border-color 0.3s}" +
            "select:focus,input:focus,textarea:focus{outline:none;border-color:#667eea}" +
            "textarea{min-height:120px;font-family:'Consolas',monospace}" +
            ".btn{padding:12px 30px;border:none;border-radius:8px;font-size:1em;cursor:pointer;transition:all 0.3s;display:inline-flex;align-items:center;gap:8px}" +
            ".btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white}" +
            ".btn-primary:hover{transform:translateY(-2px);box-shadow:0 5px 20px rgba(102,126,234,0.4)}" +
            ".response-area{background:#1e1e1e;color:#d4d4d4;padding:20px;border-radius:8px;min-height:150px;max-height:400px;overflow:auto;font-family:'Consolas',monospace;white-space:pre-wrap;margin-top:15px}" +
            ".quick-test{font-size:1.1em;font-weight:600;margin:20px 0 10px;color:#667eea}" +
            ".quick-test-container{display:flex;flex-wrap:wrap;gap:8px;margin-bottom:15px}" +
            ".quick-btn{background:linear-gradient(135deg,#667eea,#764ba2);border:none;color:white;padding:8px 12px;border-radius:6px;cursor:pointer;display:flex;align-items:center;gap:8px;font-size:0.9em;transition:all 0.3s}" +
            ".quick-btn:hover{transform:translateY(-2px);box-shadow:0 4px 12px rgba(102,126,234,0.4)}" +
            ".quick-btn .method-badge{padding:2px 6px;border-radius:4px;font-size:0.8em;font-weight:600}" +
            ".quick-btn .method-badge.GET{background:#4CAF50}" +
            ".quick-btn .method-badge.POST{background:#2196F3}" +
            ".quick-btn .method-badge.PUT{background:#FF9800}" +
            ".quick-btn .method-badge.DELETE{background:#f44336}" +
            ".quick-btn .path{color:white;opacity:0.9}" +
            ".graph-container{background:#1e1e1e;border-radius:12px;padding:20px;min-height:400px;position:relative}" +
            ".graph-info{position:absolute;top:10px;left:10px;color:#888;font-size:0.85em}" +
            ".legend{position:absolute;top:10px;right:10px;background:rgba(255,255,255,0.1);padding:10px;border-radius:8px;color:#ccc;font-size:0.85em}" +
            ".loading{text-align:center;padding:40px;color:#888}" +
            ".loading i{font-size:3em;animation:spin 1s linear infinite}" +
            "@keyframes spin{0%{transform:rotate(0deg)}100%{transform:rotate(360deg)}}" +
            ".status-bar{display:flex;justify-content:space-between;align-items:center;padding:10px 20px;background:#2d2d2d;color:#888;border-radius:8px;margin-top:10px;font-size:0.9em}" +
            ".status-dot{width:8px;height:8px;border-radius:50%;background:#4CAF50;display:inline-block;margin-right:8px}" +
            "@media(max-width:768px){.header h1{font-size:1.8em}.stat-card{min-width:100%}.form-row{flex-direction:column}}" +
            "</style></head>" +
            "<body>" +
            "<div class='container'>" +
            "  <div class='header'>" +
            "    <h1><i class='fas fa-bolt'></i> NanoBoot <span style='font-size:0.5em;color:#888'>Admin Console</span> <button id='langToggle' class='lang-btn'>English</button></h1>" +
            "    <div class='stats'>" +
            "      <div class='stat-card'><i class='fas fa-cube'></i><div class='number'>"+beanCount+"</div><div class='label'>Beans</div></div>" +
            "      <div class='stat-card'><i class='fas fa-route'></i><div class='number'>"+mappings.size()+"</div><div class='label'>Routes</div></div>" +
            "      <div class='stat-card'><i class='fas fa-server'></i><div class='number'>"+port+"</div><div class='label'>Port</div></div>" +
            "      <div class='stat-card'><i class='fas fa-clock'></i><div class='number'>"+new java.text.SimpleDateFormat("HH:mm").format(new Date())+"</div><div class='label'>Time</div></div>" +
            "    </div>" +
            "  </div>" +
            "  <div class='tabs' id='tabContainer'>" +
            "    <button class='tab-btn active' data-tab='dashboard'><i class='fas fa-th-large'></i> Dashboard</button>" +
            "    <button class='tab-btn' data-tab='beans'><i class='fas fa-cube'></i> Beans ("+beanCount+")</button>" +
            "    <button class='tab-btn' data-tab='routes'><i class='fas fa-route'></i> Routes ("+mappings.size()+")</button>" +
            "    <button class='tab-btn' data-tab='api'><i class='fas fa-paper-plane'></i> API Tester</button>" +
            "    <button class='tab-btn' data-tab='graph'><i class='fas fa-project-diagram'></i> Dependency Graph</button>" +
            "    <button class='tab-btn' data-tab='config'><i class='fas fa-cog'></i> Config</button>" +
            "  </div>" +
            
            // Dashboard Tab
            "  <div id='dashboard' class='tab-content active'>" +
            "    <h2><i class='fas fa-th-large'></i> Application Overview</h2>" +
            "    <div style='display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:20px;margin-top:20px'>" +
            "      <div style='background:linear-gradient(135deg,#667eea,#764ba2);padding:25px;border-radius:12px;color:white'>" +
            "        <h3><i class='fas fa-cube'></i> Registered Beans</h3>" +
            "        <p style='font-size:2em;font-weight:bold;margin:15px 0'>"+beanCount+"</p>" +
            "        <p style='opacity:0.8'>Total components managed by IOC container</p>" +
            "      </div>" +
            "      <div style='background:linear-gradient(135deg,#f093fb,#f5576c);padding:25px;border-radius:12px;color:white'>" +
            "        <h3><i class='fas fa-route'></i> API Routes</h3>" +
            "        <p style='font-size:2em;font-weight:bold;margin:15px 0'>"+mappings.size()+"</p>" +
            "        <p style='opacity:0.8'>HTTP endpoints available</p>" +
            "      </div>" +
            "      <div style='background:linear-gradient(135deg,#4facfe,#00f2fe);padding:25px;border-radius:12px;color:white'>" +
            "        <h3><i class='fas fa-server'></i> Server Status</h3>" +
            "        <p style='font-size:2em;font-weight:bold;margin:15px 0'>Running</p>" +
            "        <p style='opacity:0.8'>Listening on port "+port+"</p>" +
            "      </div>" +
            "    </div>" +
            "  </div>" +
            
            // Beans Tab
            "  <div id='beans' class='tab-content'>" +
            "    <h2><i class='fas fa-cube'></i> Registered Beans</h2>" +
            "    <table><thead><tr><th>Bean Name</th><th>Type</th><th>Dependencies</th></tr></thead><tbody>" + beans + "</tbody></table>" +
            "  </div>" +
            
            // Routes Tab
            "  <div id='routes' class='tab-content'>" +
            "    <h2><i class='fas fa-route'></i> API Routes</h2>" +
            "    <table><thead><tr><th>Method</th><th>Path</th><th>Handler</th></tr></thead><tbody>" + routes + "</tbody></table>" +
            "  </div>" +
            
            // API Tester Tab
            "  <div id='api' class='tab-content'>" +
            "    <div class='api-tester'>" +
            "      <h3><i class='fas fa-paper-plane'></i> HTTP Request Tester</h3>" +
            "      <div class='form-row'>" +
            "        <select id='requestMethod' style='max-width:150px'><option value='GET'>GET</option><option value='POST'>POST</option><option value='PUT'>PUT</option><option value='DELETE'>DELETE</option></select>" +
            "        <input type='text' id='requestPath' placeholder='/api/path' value='" + (mappings.isEmpty() ? "/api/example" : mappings.keySet().iterator().next().split(":")[1]) + "'>" +
            "        <button class='btn btn-primary' onclick='sendRequest()'><i class='fas fa-paper-plane'></i> Send</button>" +
            "      </div>" +
            "      <textarea id='requestBody' placeholder='Request Body (JSON)'></textarea>" +
            "      <div class='quick-test' id='quickTestTitle'><i class='fas fa-bolt'></i> Quick Test</div>" +
            "      <div class='quick-test-container' id='quickTestContainer'>Loading routes...</div>" +
            "      <div class='response-area' id='responseArea'>Response will appear here...</div>" +
            "      <div class='status-bar'>" +
            "        <span><span class='status-dot'></span>Ready</span>" +
            "        <span id='responseTime'></span>" +
            "      </div>" +
            "    </div>" +
            "  </div>" +
            
            // Dependency Graph Tab
            "  <div id='graph' class='tab-content'>" +
            "    <h2><i class='fas fa-project-diagram'></i> Bean Dependency Graph</h2>" +
            "    <div class='graph-container' id='graphContainer'>" +
            "      <div class='graph-info'>Visualizing "+beanCount+" beans</div>" +
            "      <div class='legend'>● Node = Bean &nbsp; → = Dependency</div>" +
            "      <div class='loading'><i class='fas fa-spinner'></i><br>Loading graph...</div>" +
            "    </div>" +
            "  </div>" +
            
            // Config Tab
            "  <div id='config' class='tab-content'>" +
            "    <h2><i class='fas fa-cog'></i> Configuration</h2>" +
            "    <table><thead><tr><th>Key</th><th>Value</th></tr></thead><tbody>" + config + "</tbody></table>" +
            "  </div>" +
            "</div>" +
            
            "<script src='/console.js'></script>" +
            "</body></html>";
    }
    
    private String getMethodColor(String method) {
        switch(method.toUpperCase()) {
            case "GET": return "#4CAF50";
            case "POST": return "#2196F3";
            case "PUT": return "#FF9800";
            case "DELETE": return "#f44336";
            default: return "#9E9E9E";
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

            // 处理 /nanoboot 可视化控制台
            if (requestPath.equals("/nanoboot") || requestPath.equals("/nanoboot.html")) {
                handleConsole(exchange);
                return;
            }

            // 处理 /nanoboot/routes 路由列表
            if (requestPath.equals("/nanoboot/routes")) {
                handleRoutesList(exchange);
                return;
            }
            
            // 处理 /nanoboot/beans Bean列表
            if (requestPath.equals("/nanoboot/beans")) {
                handleBeansList(exchange);
                return;
            }
            
            // 处理 /nanoboot/config 配置信息
            if (requestPath.equals("/nanoboot/config")) {
                handleConfigList(exchange);
                return;
            }

            // 处理静态资源
            if (requestPath.equals("/console.html") || requestPath.startsWith("/console.js") || requestPath.endsWith(".js") || requestPath.endsWith(".css")) {
                handleStaticResource(exchange, requestPath);
                return;
            }

            String requestQuery = exchange.getRequestURI().getQuery();
            String requestBody = getRequestBody(exchange);

            String key = requestMethod + ":" + requestPath;
            RequestMappingInfo mapping = mappings.get(key);

            if (mapping != null) {
                try {
                    RequestHandler requestHandler = new RequestHandler(applicationContext);
                    Object[] args = requestHandler.resolveMethodParameters(
                        mapping.getMethod(), requestPath, requestQuery, requestBody
                    );
                    Object result = mapping.getMethod().invoke(mapping.getController(), args);
                    
                    // Return JSON response
                    String jsonResponse;
                    if (result instanceof String) {
                        // If result is a string, wrap it in JSON
                        jsonResponse = "{\"message\":\"" + escapeJson((String)result) + "\"}";
                    } else if (result != null) {
                        // Try to convert to JSON
                        jsonResponse = objectToJson(result);
                    } else {
                        jsonResponse = "{\"message\":\"OK\"}";
                    }
                    sendResponse(exchange, jsonResponse, 200, "application/json;charset=UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Internal Server Error: " + e.getMessage(), 500, "text/plain;charset=UTF-8");
                }
            } else {
                sendResponse(exchange, "Not Found", 404, "text/plain;charset=UTF-8");
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

        /**
         * 处理路由列表API
         */
        private void handleRoutesList(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;
            for (Map.Entry<String, RequestMappingInfo> entry : mappings.entrySet()) {
                String key = entry.getKey();
                String method = key.split(":")[0];
                String path = key.split(":")[1];
                String handler = entry.getValue().getController().getClass().getSimpleName() + "." + entry.getValue().getMethod().getName() + "()";
                if (!first) sb.append(",");
                sb.append("{\"method\":\"").append(method).append("\",\"path\":\"").append(path).append("\",\"handler\":\"").append(handler).append("\"}");
                first = false;
            }
            sb.append("]");
            sendResponse(exchange, sb.toString(), 200, "application/json;charset=UTF-8");
        }
        
        /**
         * 转义JSON字符串
         */
        private String escapeJson(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                     .replace("\"", "\\\"")
                     .replace("\n", "\\n")
                     .replace("\r", "\\r")
                     .replace("\t", "\\t");
        }
        
        /**
         * 处理Bean列表API
         */
        private void handleBeansList(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;
            
            if (applicationContext != null) {
                try {
                    Method getBeanNamesMethod = applicationContext.getClass().getMethod("getBeanDefinitionNames");
                    String[] beanNames = (String[]) getBeanNamesMethod.invoke(applicationContext);
                    
                    for (String name : beanNames) {
                        Object bean = applicationContext.getBean(name);
                        Class<?> clazz = bean.getClass();
                        
                        // 获取依赖的Bean
                        List<String> deps = new ArrayList<>();
                        for (Field field : clazz.getDeclaredFields()) {
                            if (field.isAnnotationPresent(org.nanoboot.annotation.Annotation.Autowired.class)) {
                                deps.add(field.getType().getSimpleName());
                            }
                        }
                        
                        if (!first) sb.append(",");
                        sb.append("{\"name\":\"").append(escapeJson(name))
                          .append("\",\"className\":\"").append(escapeJson(clazz.getName()))
                          .append("\",\"dependencies\":\"").append(escapeJson(String.join(", ", deps))).append("\"}");
                        first = false;
                    }
                } catch (Exception e) {
                    System.err.println("Error getting beans: " + e.getMessage());
                }
            }
            
            sb.append("]");
            sendResponse(exchange, sb.toString(), 200, "application/json;charset=UTF-8");
        }
        
        /**
         * 处理配置信息API
         */
        private void handleConfigList(HttpExchange exchange) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;
            
            // 添加服务器信息
            sb.append("{\"key\":\"server.port\",\"value\":\"").append(port).append("\"}");
            first = false;
            
            if (applicationContext != null) {
                try {
                    Method getEnvMethod = applicationContext.getClass().getMethod("getEnvironment");
                    Object env = getEnvMethod.invoke(applicationContext);
                    if (env != null) {
                        Method getPropertiesMethod = env.getClass().getMethod("getProperties");
                        Map<?, ?> props = (Map<?, ?>) getPropertiesMethod.invoke(env);
                        for (Map.Entry<?, ?> entry : props.entrySet()) {
                            sb.append(",{\"key\":\"").append(escapeJson(String.valueOf(entry.getKey())))
                              .append("\",\"value\":\"").append(escapeJson(String.valueOf(entry.getValue()))).append("\"}");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error getting config: " + e.getMessage());
                }
            }
            
            sb.append("]");
            sendResponse(exchange, sb.toString(), 200, "application/json;charset=UTF-8");
        }

        /**
         * 处理静态资源
         */
        private void handleStaticResource(HttpExchange exchange, String requestPath) throws IOException {
            String resourcePath = requestPath.substring(1); // 去掉前导 /
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (is != null) {
                String contentType = "application/javascript";
                if (requestPath.endsWith(".css")) {
                    contentType = "text/css";
                }
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                sendResponse(exchange, sb.toString(), 200, contentType + ";charset=UTF-8");
            } else {
                sendResponse(exchange, "Not Found", 404, "text/plain;charset=UTF-8");
            }
        }
    }

    /**
     * 发送响应
     */
    private void sendResponse(HttpExchange exchange, String response, int statusCode, String contentType) throws IOException {
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
    /**
     * Convert object to JSON string (simple implementation)
     */
    private String objectToJson(Object obj) {
        if (obj == null) return "null";
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
        boolean first = true;
        for (java.lang.reflect.Field field : fields) {
            if (!first) sb.append(",");
            first = false;
            
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                value = null;
            }
            
            sb.append("\"").append(fieldName).append("\":");
            if (value == null) {
                sb.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value.toString());
            } else {
                sb.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Escape special characters for JSON string
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
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
