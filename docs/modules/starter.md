# Starter Module

The starter module provides the application bootstrap functionality for NanoBoot applications. It handles the initialization sequence and connects all framework components together.

## Overview

The starter module serves as the main entry point for NanoBoot applications, providing:

- Application startup and initialization
- Annotation processing coordination
- Component lifecycle management
- Integration with core module services

## Key Components

### NanoBootApplicationRunner

The main application runner that orchestrates the startup process:

```java
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        // Initialize the application context
        ApplicationContext context = new ApplicationContext();

        // Process @NanoBootApplication annotation
        processNanoBootApplication(mainClass, context);

        // Scan for components
        ComponentScanner scanner = new ComponentScanner();
        scanner.scanAndRegister(findBasePackages(mainClass), context);

        // Register beans in context
        registerBeans(context);

        // Process dependencies
        DependencyInjector injector = new DependencyInjector();
        injectDependencies(context);

        // Execute post-initialization
        executePostConstructMethods(context);

        // Store context for global access
        ApplicationContextHolder.setContext(context);

        System.out.println("Application started successfully!");
    }

    private static void processNanoBootApplication(Class<?> mainClass, ApplicationContext context) {
        // Process any configuration in @NanoBootApplication
    }

    private static void registerBeans(ApplicationContext context) {
        // Register discovered components as beans
    }

    private static void injectDependencies(ApplicationContext context) {
        // Inject dependencies for all beans
    }

    private static void executePostConstructMethods(ApplicationContext context) {
        // Execute @PostConstruct methods
    }
}
```

### @NanoBootApplication Annotation

Marks the main application class and triggers the startup process:

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan  // Enables component scanning
@EnableConfigurationProperties  // Enables property processing
public @interface NanoBootApplication {
    // Configuration attributes
}
```

## Usage

### Main Application Class

Create your main application class with the `@NanoBootApplication` annotation:

```java
@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        System.out.println("Starting NanoBoot application...");
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### Configuration Options

The main application class can be customized with additional configuration:

```java
@NanoBootApplication
@ComponentScan(basePackages = {
    "com.example.controller",
    "com.example.service",
    "com.example.repository"
})
public class CustomAppApplication {

    public static void main(String[] args) {
        // Custom startup logic
        System.out.println("Starting custom application...");

        // Pass control to the runner
        NanoBootApplicationRunner.run(CustomAppApplication.class, args);
    }
}
```

## Integration Points

### With Core Module

The starter module integrates tightly with the core module:

```java
// During startup
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        // Initialize core components
        ApplicationContext context = initializeCoreContext();

        // Discover and register components using core scanning
        scanAndRegisterComponents(context, mainClass);

        // Process dependencies using core injection
        processDependencies(context);

        // Store in core context holder
        storeGlobalContext(context);
    }
}
```

### With Other Modules

The starter module provides integration points for other modules:

```java
public class NanoBootApplicationRunner {

    private static void integrateModules(ApplicationContext context) {
        // Web module integration
        if (isModulePresent("nano-boot-web")) {
            initializeWebSupport(context);
        }

        // Data module integration
        if (isModulePresent("nano-boot-data")) {
            initializeDataSupport(context);
        }

        // WebSocket module integration
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

## Advanced Configuration

### Custom ApplicationContext

You can provide a custom application context:

```java
public class CustomContext extends ApplicationContext {
    // Custom context implementation
}

@NanoBootApplication
public class CustomContextApplication {

    public static void main(String[] args) {
        // Custom context initialization
        CustomContext customContext = new CustomContext();

        // Use custom initialization
        NanoBootApplicationRunner.runWithCustomContext(
            CustomContextApplication.class,
            args,
            customContext
        );
    }
}
```

### Environment-Specific Configuration

Load different configurations based on environment:

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

## Error Handling

The starter module includes comprehensive error handling:

```java
public class NanoBootApplicationRunner {

    public static void run(Class<?> mainClass, String[] args) {
        try {
            // Startup process with error handling
            performStartup(mainClass, args);
        } catch (ConfigurationException e) {
            System.err.println("Configuration error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (ComponentRegistrationException e) {
            System.err.println("Component registration error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Application startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
```

## Best Practices

### 1. Single Responsibility

Keep the main application class minimal, containing only startup logic.

### 2. Proper Packaging

Organize your application in a proper package hierarchy starting from your main class.

### 3. Configuration Management

Use the main class to set up environment-specific configurations.

### 4. Error Handling

The starter module provides built-in error handling, but you can extend it for custom needs.

## Module Discovery

The starter module automatically discovers and integrates other NanoBoot modules:

```java
public class ModuleDiscovery {

    public static List<NanoBootModule> discoverModules() {
        List<NanoBootModule> modules = new ArrayList<>();

        // Discover web module
        if (hasClasses("org.nanoboot.web")) {
            modules.add(new WebModule());
        }

        // Discover data module
        if (hasClasses("org.nanoboot.data")) {
            modules.add(new DataModule());
        }

        // Discover websocket module
        if (hasClasses("org.nanoboot.websocket")) {
            modules.add(new WebSocketModule());
        }

        return modules;
    }

    private static boolean hasClasses(String packageName) {
        // Implementation to check if package exists
        return true;
    }
}
```

The starter module serves as the glue that binds all NanoBoot components together, providing a unified and consistent startup experience for applications using the framework.