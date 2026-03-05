package org.nanoboot.starter;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.core.ApplicationContext;
import org.nanoboot.core.config.DefaultEnvironment;
import org.nanoboot.core.container.DefaultApplicationContext;
import org.nanoboot.web.server.NanoHttpServer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * NanoBoot启动器
 */
public class NanoBootApplicationRunner {

    // ANSI 颜色代码
    private static final String RESET = "\033[0m";
    private static final String BOLD = "\033[1m";
    private static final String GREEN = "\033[32m";
    private static final String CYAN = "\033[36m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";
    private static final String MAGENTA = "\033[35m";
    private static final String RED = "\033[31m";

    // NanoBoot Logo
    private static final String[] LOGO = { 
        "" +
        "  _   _                     ____              _   \n" +
        " | \\ | |                   |  _ \\            | |  \n" +
        " |  \\| | __ _ _ __   ___   | |_) | ___   ___ | |_ \n" +
        " | . ` |/ _` | '_ \\ / _ \\  |  _ < / _ \\ / _ \\| __|\n" +
        " | |\\  | (_| | | | | (_) | | |_) | (_) | (_) | |_ \n" +
        " |_| \\_|\\__,_|_| |_|\\___/  |____/ \\___/ \\___/ \\__|\n" +
        "                                                  \n" +
        "                                                  " +
        "" 
     };

    private final Class<?> mainClass;
    private final String[] args;
    private ApplicationContext applicationContext;
    private NanoHttpServer httpServer;
    private long startTime;

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

        startTime = System.currentTimeMillis();

        // 打印启动 banner
        printBanner();

        try {
            // 初始化应用上下文
            initializeApplicationContext();

            // 启动HTTP服务器（如果需要的话）
            startHttpServer();

            // 打印启动完成信息
            printStartedInfo();

            // 保持主线程运行
            keepAlive();

        } catch (Exception e) {
            System.err.println(BOLD + RED + "Application run failed" + RESET);
            System.err.println(RED + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }

    /**
     * 打印启动 Banner
     */
    private void printBanner() {
        System.out.println();
        // 打印 Logo
        for (String line : LOGO) {
            System.out.println(MAGENTA + BOLD + line + RESET);
        }
        System.out.println();

        // 打印版本信息
        String version = getAppVersion();
        System.out.println(BLUE + ":: NanoBoot ::" + RESET + "        (v" + version + ")");
        System.out.println();
    }

    /**
     * 获取应用版本
     */
    private String getAppVersion() {
        // 默认版本
        return "1.0.0-SNAPSHOT";
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

        System.out.println(BLUE + "Loading source packages: " + RESET + CYAN + basePackage + RESET);
        System.out.println(YELLOW + "Bean factory initialization..." + RESET);
    }

    /**
     * 启动HTTP服务器
     */
    private void startHttpServer() {
        try {
            // 尝试获取端口配置，默认8080
            int port = 8080;
            String portStr = applicationContext.getEnvironment().getProperty("server.port");
            if (portStr != null && !portStr.isEmpty()) {
                port = Integer.parseInt(portStr);
            }

            httpServer = new NanoHttpServer(port);

            // 注册所有控制器Bean
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            int controllerCount = 0;
            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);

                // 检查是否是控制器
                if (isControllerBean(bean)) {
                    httpServer.registerController(bean);
                    controllerCount++;
                }
            }

            httpServer.start();

            System.out.println(GREEN + "Tomcat started on port: " + RESET + BOLD + port + RESET);
            System.out.println(YELLOW + "Registering " + controllerCount + " controller(s)..." + RESET);

        } catch (Exception e) {
            System.err.println(BOLD + RED + "Failed to start HTTP server: " + RESET + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 打印启动完成信息
     */
    private void printStartedInfo() {
        long startupTime = System.currentTimeMillis() - startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeStr = sdf.format(new Date(startTime));

        int beanCount = applicationContext.getBeanDefinitionCount();
        int port = 8080;
        String portStr = applicationContext.getEnvironment().getProperty("server.port");
        if (portStr != null && !portStr.isEmpty()) {
            port = Integer.parseInt(portStr);
        }

        System.out.println();
        System.out.println(BOLD + GREEN + "----------------------------------------------------------" + RESET);
        System.out.println(BOLD + "  Application " + mainClass.getSimpleName() + " is running!" + RESET);
        System.out.println(BOLD + GREEN + "----------------------------------------------------------" + RESET);
        System.out.println();
        System.out.println(BLUE + "  Application Name  : " + RESET + mainClass.getSimpleName());
        System.out.println(BLUE + "  NanoBoot Version  : " + RESET + getAppVersion());
        System.out.println(BLUE + "  Java Version      : " + RESET + System.getProperty("java.version"));
        System.out.println(BLUE + "  Server Port       : " + RESET + port);
        System.out.println(BLUE + "  Context Path      : " + RESET + "/");
        System.out.println(BLUE + "  Beans Loaded      : " + RESET + beanCount);
        System.out.println(BLUE + "  Start Time        : " + RESET + startTimeStr);
        System.out.println(BLUE + "  Startup Time      : " + RESET + startupTime + "ms");
        System.out.println();
        System.out.println(GREEN + "  Started " + mainClass.getSimpleName() + " in " + (startupTime / 1000.0) + " seconds" + RESET);
        System.out.println();
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
        System.out.println();
        System.out.println(BLUE + "Stopping NanoBoot application..." + RESET);

        if (httpServer != null) {
            httpServer.stop();
        }

        if (applicationContext != null) {
            applicationContext.close();
        }

        System.out.println(GREEN + "Application shutdown completed." + RESET);
    }

    /**
     * 静态启动方法
     */
    public static void run(Class<?> mainClass, String[] args) {
        new NanoBootApplicationRunner(mainClass, args).run();
    }
}
