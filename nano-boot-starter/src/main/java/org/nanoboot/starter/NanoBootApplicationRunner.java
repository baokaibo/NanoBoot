package org.nanoboot.starter;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.core.ApplicationContext;
import org.nanoboot.core.config.DefaultEnvironment;
import org.nanoboot.core.container.DefaultApplicationContext;
import org.nanoboot.web.server.NanoHttpServer;

import java.util.HashSet;
import java.util.Set;

/**
 * NanoBoot启动器
 */
public class NanoBootApplicationRunner {

    private final Class<?> mainClass;
    private final String[] args;
    private ApplicationContext applicationContext;
    private NanoHttpServer httpServer;

    public NanoBootApplicationRunner(Class<?> mainClass, String[] args) {
        this.mainClass = mainClass;
        this.args = args;
    }

    /**
     * 运行应用程序
     */
    public void run() {
        // 检查主类是否标注了@NanoBootApplication注解
        if (!mainClass.isAnnotationPresent(NanoBootApplication.class)) {
            throw new IllegalStateException("Main class must be annotated with @NanoBootApplication");
        }

        System.out.println("Starting NanoBoot application...");

        try {
            // 初始化应用上下文
            initializeApplicationContext();

            // 启动HTTP服务器（如果需要的话）
            startHttpServer();

            System.out.println("NanoBoot application started successfully!");

            // 保持主线程运行
            keepAlive();

        } catch (Exception e) {
            System.err.println("Failed to start NanoBoot application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 初始化应用上下文
     */
    private void initializeApplicationContext() {
        // 获取包扫描路径
        String basePackage = mainClass.getPackage().getName();

        Set<String> basePackages = new HashSet<>();
        basePackages.add(basePackage);

        // 初始化环境
        DefaultEnvironment environment = new DefaultEnvironment();

        // 创建应用上下文
        applicationContext = new DefaultApplicationContext(basePackages, environment);

        // 刷新上下文，触发Bean创建
        applicationContext.refresh();

        System.out.println("Application context initialized with " +
                          applicationContext.getBeanDefinitionCount() + " beans.");
    }

    /**
     * 启动HTTP服务器
     */
    private void startHttpServer() {
        try {
            // 尝试获取端口配置，默认8080
            int port = 8080;
            if (applicationContext.getEnvironment().containsProperty("server.port")) {
                String portStr = applicationContext.getEnvironment().getProperty("server.port");
                if (portStr != null) {
                    port = Integer.parseInt(portStr);
                }
            }

            httpServer = new NanoHttpServer(port);

            // 注册所有控制器Bean
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);

                // 检查是否是控制器
                if (isControllerBean(bean)) {
                    httpServer.registerController(bean);
                }
            }

            httpServer.start();
        } catch (Exception e) {
            System.err.println("Failed to start HTTP server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查是否是控制器Bean
     */
    private boolean isControllerBean(Object bean) {
        Class<?> clazz = bean.getClass();
        return clazz.isAnnotationPresent(org.nanoboot.annotation.Annotation.Controller.class);
    }

    /**
     * 保持主线程运行
     */
    private void keepAlive() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // 等待中断信号
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 关闭应用程序
     */
    private void shutdown() {
        System.out.println("Shutting down NanoBoot application...");

        if (httpServer != null) {
            httpServer.stop();
        }

        if (applicationContext != null) {
            applicationContext.close();
        }

        System.out.println("NanoBoot application shut down.");
    }

    /**
     * 静态启动方法
     */
    public static void run(Class<?> mainClass, String[] args) {
        new NanoBootApplicationRunner(mainClass, args).run();
    }
}