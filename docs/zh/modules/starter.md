# Starter 模块

Starter 模块为 NanoBoot 应用程序提供应用程序引导功能。它处理初始化序列并将所有框架组件连接在一起。

## 概述

Starter 模块作为 NanoBoot 应用程序的主入口点，提供：

- 应用程序启动和初始化
- 注解处理协调
- 组件生命周期管理
- 与核心模块服务的集成

## 关键组件

### NanoBootApplicationRunner

编排启动过程的主要应用程序运行器：

```java
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        // 初始化应用程序上下文
        ApplicationContext context = new ApplicationContext();

        // 处理 @NanoBootApplication 注解
        processNanoBootApplication(mainClass, context);

        // 扫描组件
        ComponentScanner scanner = new ComponentScanner();
        scanner.scanAndRegister(findBasePackages(mainClass), context);

        // 在上下文中注册 Bean
        registerBeans(context);

        // 处理依赖关系
        DependencyInjector injector = new DependencyInjector();
        injectDependencies(context);

        // 执行后初始化
        executePostConstructMethods(context);

        // 存储全局上下文
        ApplicationContextHolder.setContext(context);

        System.out.println("应用程序启动成功！");
    }

    private static void processNanoBootApplication(Class<?> mainClass, ApplicationContext context) {
        // 处理 @NanoBootApplication 中的任何配置
    }

    private static void registerBeans(ApplicationContext context) {
        // 将发现的组件注册为 Bean
    }

    private static void injectDependencies(Context context) {
        // 为所有 Bean 注入依赖
    }

    private static void executePostConstructMethods(Context context) {
        // 执行 @PostConstruct 方法
    }
}
```

### @NanoBootApplication 注解

标记主应用程序类并触发启动过程：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan  // 启用组件扫描
@EnableConfigurationProperties  // 启用属性处理
public @interface NanoBootApplication {
    // 配置属性
}
```

## 使用方法

### 主应用程序类

使用 `@NanoBootApplication` 注解创建您的主应用程序类：

```java
@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        System.out.println("正在启动 NanoBoot 应用程序...");
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### 配置选项

可以使用附加配置自定义主应用程序类：

```java
@NanoBootApplication
@ComponentScan(basePackages = {
    "com.example.controller",
    "com.example.service",
    "com.example.repository"
})
public class CustomAppApplication {

    public static void main(String[] args) {
        // 自定义启动逻辑
        System.out.println("正在启动自定义应用程序...");

        // 将控制权传递给运行器
        NanoBootApplicationRunner.run(CustomAppApplication.class, args);
    }
}
```

## 集成点

### 与核心模块集成

Starter 模块与核心模块紧密集成：

```java
// 启动期间
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        // 初始化核心组件
        ApplicationContext context = initializeCoreContext();

        // 使用核心扫描发现并注册组件
        scanAndRegisterComponents(context, mainClass);

        // 使用核心注入处理依赖关系
        processDependencies(context);

        // 存储在核心上下文持有者中
        storeGlobalContext(context);
    }
}
```

### 与其他模块集成

Starter 模块为其他模块提供集成点：

```java
public class NanoBootApplicationRunner {

    private static void integrateModules(ApplicationContext context) {
        // Web 模块集成
        if (isModulePresent("nano-boot-web")) {
            initializeWebSupport(context);
        }

        // Data 模块集成
        if (isModulePresent("nano-boot-data")) {
            initializeDataSupport(context);
        }

        // WebSocket 模块集成
        if (isModulePresent("nano-boot-websocket")) {
            initializeWebSocketSupport(context);
        }
    }

    private static boolean isModulePresent(String moduleName) {
        try {
            Class.forName(moduleName + ".SomeClass");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
```

## 高级配置

### 自定义 ApplicationContext

您可以提供自定义的应用程序上下文：

```java
public class CustomContext extends ApplicationContext {
    // 自定义上下文实现
}

@NanoBootApplication
public class CustomContextApplication {

    public static void main(String[] args) {
        // 自定义上下文初始化
        CustomContext customContext = new CustomContext();

        // 使用自定义初始化
        NanoBootApplicationRunner.runWithCustomContext(
            CustomContextApplication.class,
            args,
            customContext
        );
    }
}
```

### 环境特定配置

根据环境加载不同的配置：

```java
@NanoBootApplication
public class EnvironmentAwareApplication {

    public static void main(String[] args) {
        String env = System.getProperty("app.env", "development");

        if ("production".equals(env)) {
            System.setProperty("spring.profiles.active", "prod");
        } else if ("staging".equals(env)) {
            System.setProperty("spring.profiles.active", "staging");
        }

        NanoBootApplicationRunner.run(EnvironmentAwareApplication.class, args);
    }
}
```

## 错误处理

Starter 模块包含全面的错误处理：

```java
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        try {
            // 带有错误处理的启动过程
            performStartup(mainClass, args);
        } catch (ConfigurationException e) {
            System.err.println("配置错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ComponentRegistrationException e) {
            System.err.println("组件注册错误: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("应用程序启动失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
```

## 最佳实践

### 1. 单一职责

保持主应用程序类最小化，只包含启动逻辑。

### 2. 正确的打包

从主类开始，以适当的包层次结构组织您的应用程序。

### 3. 配置管理

使用主类设置特定于环境的配置。

### 4. 错误处理

Starter 模块提供了内置的错误处理，但您可以为其扩展以满足自定义需求。

## 模块发现

Starter 模块自动发现并集成其他 NanoBoot 模块：

```java
public class ModuleDiscovery {

    public static List<NanoBootModule> discoverModules() {
        List<NanoBootModule> modules = new ArrayList<>();

        // 发现 Web 模块
        if (hasClasses("org.nanoboot.web")) {
            modules.add(new WebModule());
        }

        // 发现 Data 模块
        if (hasClasses("org.nanoboot.data")) {
            modules.add(new DataModule());
        }

        // 发现 WebSocket 模块
        if (hasClasses("org.nanoboot.websocket")) {
            modules.add(new WebSocketModule());
        }

        return modules;
    }

    private static boolean hasClasses(String packageName) {
        // 实现检查包是否存在
        return true;
    }
}
```

Starter 模块是将所有 NanoBoot 组件绑定在一起的粘合剂，为使用该框架的应用程序提供统一且一致的启动体验。
