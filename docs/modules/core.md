# Core Module

The core module is the foundation of the NanoBoot framework, providing essential functionality such as dependency injection, component scanning, and property management.

## Overview

The core module implements the basic features needed for dependency injection and application context management. It provides:

- Component scanning and registration
- Dependency injection engine
- Property management
- Annotation processing
- Lifecycle management

## Key Features

### Component Scanning

The core module automatically scans for and registers components with the following annotations:

- `@Component` - Generic component
- `@Service` - Service layer components
- `@Controller` - Web controllers
- `@Repository` - Data access components

### Dependency Injection

The dependency injection engine resolves dependencies annotated with `@Autowired`:

```java
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
}
```

### Property Management

Properties can be injected using the `@Value` annotation:

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${server.port:8080}")
    private int port;
}
```

### Lifecycle Management

Components can hook into their lifecycle using annotations:

```java
@Component
public class DatabaseInitializer {

    @PostConstruct
    public void init() {
        // Runs after dependencies are injected
    }

    @PreDestroy
    public void cleanup() {
        // Runs before bean destruction
    }
}
```

## Core Components

### ApplicationContext

The central registry for all beans in the application:

```java
public class ApplicationContext {
    private Map<Class<?>, Object> singletonBeans = new ConcurrentHashMap<>();
    private Map<String, Object> namedBeans = new ConcurrentHashMap<>();

    // Methods for retrieving beans
    public <T> T getBean(Class<T> type) { ... }
    public Object getBean(String name) { ... }
}
```

### ComponentScanner

Responsible for finding and registering annotated classes:

```java
public class ComponentScanner {
    public void scanAndRegister(String packagePath, ApplicationContext context) { ... }
}
```

### DependencyInjector

Handles the injection of dependencies:

```java
public class DependencyInjector {
    public void injectDependencies(Object bean, ApplicationContext context) { ... }
}
```

## Configuration

### Component Scanning

Configure component scanning in your main application:

```java
@NanoBootApplication
@ComponentScan(basePackages = {"com.example.service", "com.example.controller"})
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### Property Files

Place configuration in `application.properties`:

```properties
# Application properties
app.name=My Application
app.version=1.0.0

# Custom properties
custom.feature.enabled=true
custom.timeout.seconds=30
```

## Best Practices

### 1. Use Appropriate Stereotypes

Use the right stereotype annotation for each component type:
- `@Service` for business logic
- `@Controller` for web endpoints
- `@Repository` for data access (when available)

### 2. Configure Properties Externally

Store configuration values in property files rather than hardcoding them.

### 3. Use Constructor Injection

When possible, use constructor injection for required dependencies.

### 4. Handle Lifecycle Events Properly

Use `@PostConstruct` and `@PreDestroy` appropriately for initialization and cleanup.

## Extending the Core

The core module can be extended with custom annotations and processors:

```java
// Custom annotation
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomInjection {
    String value() default "";
}

// Custom processor
public class CustomAnnotationProcessor {
    public void processCustomAnnotations(Object bean, ApplicationContext context) {
        // Custom processing logic
    }
}
```

The core module provides the essential infrastructure that all other modules build upon, ensuring consistent dependency injection and component management across the framework.