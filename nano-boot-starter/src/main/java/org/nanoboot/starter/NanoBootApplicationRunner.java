/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nanoboot.starter;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.core.ApplicationContext;
import org.nanoboot.core.config.DefaultEnvironment;
import org.nanoboot.core.container.DefaultApplicationContext;
import org.nanoboot.web.server.NanoHttpServer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that can be used to bootstrap and launch a NanoBoot application from a Java main
 * method. By default class will perform the following steps to bootstrap your application:
 *
 * <ul>
 * <li>Create an appropriate {@link ApplicationContext} instance</li>
 * <li>Register a property source to expose command line arguments as NanoBoot properties</li>
 * <li>Refresh the application context, loading all singleton beans</li>
 * <li>Start embedded HTTP server</li>
 * </ul>
 *
 * In most circumstances the static {@link #run(Class, String[])} method can be called
 * directly from your {@literal main} method to bootstrap your application:
 *
 * <pre class="code">
 * &#064;NanoBootApplication
 * public class MyApplication  {
 *
 *   // ... Bean definitions
 *
 *   public static void main(String[] args) {
 *     NanoBootApplication.run(MyApplication.class, args);
 *   }
 * }
 * </pre>
 *
 * <p>
 * For more advanced configuration a {@link NanoBootApplicationRunner} instance can be created and
 * customized before being run:
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *   NanoBootApplicationRunner application = new NanoBootApplicationRunner(MyApplication.class);
 *   // ... customize application settings here
 *   application.run(args)
 * }
 * </pre>
 *
 * @author NanoBoot
 * @since 1.0.0
 * @see #run(Class, String[])
 */
public class NanoBootApplicationRunner {

    // 日志时间格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String THREAD_NAME = "main";

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

    private Class<?> mainApplicationClass;

    private Set<String> sources = new LinkedHashSet<>();

    private boolean logStartupInfo = true;

    private Set<String> additionalProfiles = Collections.emptySet();

    private Class<?> mainClass;

    private String[] args;

    private ApplicationContext applicationContext;

    private NanoHttpServer httpServer;

    private long startTime;

    private long pid;

    private int port = 8080;

    private String contextPath = "/";

    public NanoBootApplicationRunner(Class<?> primarySource, String... args) {
        this.mainClass = primarySource;
        this.args = args;
        this.mainApplicationClass = primarySource;
        this.sources.add(primarySource.getName());
        this.pid = getPid();
    }

    /**
     * Run the NanoBoot application, creating and refreshing a new
     * {@link ApplicationContext}.
     * @param args the application arguments (usually passed from a Java main method)
     * @return a running {@link ApplicationContext}
     */
    public ApplicationContext run(String... args) {
        long startTime = System.nanoTime();
        
        // 检查主类是否标注了@NanoBootApplication注解
        if (!mainClass.isAnnotationPresent(NanoBootApplication.class)) {
            throw new IllegalStateException("Main class must be annotated with @NanoBootApplication");
        }

        this.startTime = System.currentTimeMillis();
        
        // 打印启动 banner
        printBanner();

        try {
            // 准备环境
            DefaultEnvironment environment = prepareEnvironment();

            // 创建应用上下文
            ApplicationContext context = createApplicationContext(environment);

            // 准备上下文
            prepareContext(context);

            // 刷新上下文
            refreshContext(context);

            // 启动HTTP服务器
            startHttpServer(context);

            // 打印启动完成信息
            printStartedInfo(context);

            // 注册关闭钩子
            registerShutdownHook();

            return context;
        } catch (Throwable ex) {
            handleRunFailure(ex);
            throw new IllegalStateException(ex);
        }
    }

    /**
     * 准备环境
     */
    private DefaultEnvironment prepareEnvironment() {
        DefaultEnvironment environment = new DefaultEnvironment();
        
        // 处理命令行参数
        if (args.length > 0) {
            // 可以在这里处理命令行属性
        }
        
        return environment;
    }

    /**
     * 创建应用上下文
     */
    private ApplicationContext createApplicationContext(DefaultEnvironment environment) {
        // 获取包扫描路径
        String basePackage = mainClass.getPackage().getName();

        Set<String> basePackages = new HashSet<>();
        basePackages.add(basePackage);

        // 创建应用上下文
        return new DefaultApplicationContext(basePackages, environment);
    }

    /**
     * 准备上下文
     */
    private void prepareContext(ApplicationContext context) {
        logInfo("org.nanoboot.starter", "Loading source packages: " + sources);
        logInfo("org.nanoboot.starter", "Bean factory initialization...");
    }

    /**
     * 刷新上下文
     */
    private void refreshContext(ApplicationContext context) {
        context.refresh();
    }

    /**
     * 启动HTTP服务器
     */
    private void startHttpServer(ApplicationContext context) {
        try {
            // 尝试获取端口配置
            String portStr = context.getEnvironment().getProperty("server.port");
            if (portStr != null && !portStr.isEmpty()) {
                port = Integer.parseInt(portStr);
            }

            // 获取context path
            String path = context.getEnvironment().getProperty("server.servlet.context-path");
            if (path != null && !path.isEmpty()) {
                contextPath = path;
            }

            httpServer = new NanoHttpServer(port);

            // 注册所有控制器Bean
            String[] beanNames = context.getBeanDefinitionNames();
            int controllerCount = 0;
            for (String beanName : beanNames) {
                Object bean = context.getBean(beanName);

                // 检查是否是控制器
                if (isControllerBean(bean)) {
                    httpServer.registerController(bean);
                    controllerCount++;
                }
            }

            httpServer.start();

            logInfo("org.nanoboot.starter", "Tomcat started on port: " + port + " with context path '" + contextPath + "'");
            logInfo("org.nanoboot.starter", "Registering " + controllerCount + " controller(s)...");

        } catch (Exception e) {
            logError("org.nanoboot.starter", "Failed to start HTTP server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理运行失败
     */
    private void handleRunFailure(Throwable exception) {
        logError("Application run failed", exception);
    }

    /**
     * 注册关闭钩子
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    /**
     * 打印启动 Banner
     */
    private void printBanner() {
        System.out.println();
        // 打印 Logo
        for (String line : LOGO) {
            System.out.println(ConsoleColor.PURPLE + ConsoleColor.BOLD + line + ConsoleColor.RESET);
        }
        System.out.println();

        // 打印版本信息
        String version = getAppVersion();
        System.out.println(ConsoleColor.BLUE + ":: NanoBoot ::" + ConsoleColor.RESET + "        (v" + version + ")");
        System.out.println();
    }

    /**
     * 获取应用版本
     */
    private String getAppVersion() {
        return "1.0.0-SNAPSHOT";
    }

    /**
     * 打印启动完成信息
     */
    private void printStartedInfo(ApplicationContext context) {
        long startupTime = System.currentTimeMillis() - startTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTimeStr = sdf.format(new Date(startTime));

        int beanCount = context.getBeanDefinitionCount();

        System.out.println();
        logInfo("org.nanoboot.starter", " ---------------------------------------------------------- ");
        logInfo("org.nanoboot.starter", " Application " + mainClass.getSimpleName() + " is running!");
        logInfo("org.nanoboot.starter", " ---------------------------------------------------------- ");
        System.out.println();
        logInfo("org.nanoboot.starter", " Application Name  : " + mainClass.getSimpleName());
        logInfo("org.nanoboot.starter", " NanoBoot Version  : " + getAppVersion());
        logInfo("org.nanoboot.starter", " Java Version      : " + System.getProperty("java.version"));
        logInfo("org.nanoboot.starter", " Server Port       : " + port);
        logInfo("org.nanoboot.starter", " Context Path      : " + contextPath);
        logInfo("org.nanoboot.starter", " Beans Loaded      : " + beanCount);
        logInfo("org.nanoboot.starter", " Start Time        : " + startTimeStr);
        logInfo("org.nanoboot.starter", " Startup Time      : " + startupTime + "ms");
        System.out.println();
        logInfo("org.nanoboot.starter", " Started " + mainClass.getSimpleName() + " in " + (startupTime / 1000.0) + " seconds");
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
     * 关闭应用程序
     */
    private void shutdown() {
        System.out.println();
        logInfo("org.nanoboot.starter", "Stopping NanoBoot application...");

        if (httpServer != null) {
            httpServer.stop();
        }

        if (applicationContext != null) {
            applicationContext.close();
        }

        logInfo("org.nanoboot.starter", "Application shutdown completed.");
    }

    /**
     * 获取进程ID (Java 8 兼容)
     */
    private long getPid() {
        try {
            String name = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            return Long.parseLong(name.split("@")[0]);
        } catch (Exception e) {
            return (long) (Math.random() * 100000);
        }
    }

    // ==================== 日志方法 ====================

    /**
     * 格式化日志输出 - Spring Boot 风格
     * 2026-03-05 11:43:55.243  INFO 54580 --- [main] org.nanoboot.starter : message
     */
    private void logInfo(String logger, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String formatted = String.format("%s  INFO %d --- [%-14s] %-35s : %s", timestamp, pid, THREAD_NAME, logger, message);

        // 只有org.nanoboot.starter显示蓝色
        if ("org.nanoboot.starter".equals(logger)) {
            System.out.println(ConsoleColor.BLUE + formatted + ConsoleColor.RESET);
        } else {
            System.out.println(formatted); // 默认黑色
        }
    }

    private void logInfo(String message) {
        logInfo("org.nanoboot.starter", message);
    }

    /**
     * 警告信息 - 黄色
     */
    private void logWarn(String logger, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String formatted = String.format("%s  WARN %d --- [%-14s] %-35s : %s", timestamp, pid, THREAD_NAME, logger, message);
        System.out.println(ConsoleColor.YELLOW + formatted + ConsoleColor.RESET);
    }

    private void logWarn(String message) {
        logWarn("org.nanoboot.starter.NanoBootApplicationRunner", message);
    }

    /**
     * 错误信息 - 红色
     */
    private void logError(String logger, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String formatted = String.format("%s ERROR %d --- [%-14s] %-35s : %s", timestamp, pid, THREAD_NAME, logger, message);
        System.err.println(ConsoleColor.RED + formatted + ConsoleColor.RESET);
    }

    private void logError(String message, Throwable throwable) {
        logError("org.nanoboot.starter.NanoBootApplicationRunner", message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    private void logError(String message) {
        logError("org.nanoboot.starter.NanoBootApplicationRunner", message);
    }

    // ==================== 静态方法 ====================

    /**
     * Static helper that can be used to run a {@link NanoBootApplicationRunner} from the
     * specified source using default settings.
     * @param primarySource the primary source to load
     * @param args the application arguments (usually passed from a Java main method)
     * @return the running {@link ApplicationContext}
     */
    public static ApplicationContext run(Class<?> primarySource, String... args) {
        return new NanoBootApplicationRunner(primarySource, args).run(args);
    }

    /**
     * Static helper that can be used to run a {@link NanoBootApplicationRunner} from the
     * specified sources using default settings and user supplied arguments.
     * @param primarySources the primary sources to load
     * @param args the application arguments (usually passed from a Java main method)
     * @return the running {@link ApplicationContext}
     */
    public static ApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new NanoBootApplicationRunner(primarySources[0], args).run(args);
    }

    /**
     * A basic main that can be used to launch an application.
     * @param args command line arguments
     * @throws Exception if the application cannot be started
     */
    public static void main(String[] args) throws Exception {
        run(new Class<?>[0], args);
    }
}
