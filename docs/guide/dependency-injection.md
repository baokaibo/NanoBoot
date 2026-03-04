# Dependency Injection

NanoBoot provides a powerful dependency injection (DI) container that manages object creation and lifecycle. This system automates the wiring of components, making your code more modular and testable.

## Core Concepts

### Components and Beans

In NanoBoot, any class annotated with `@Component` is treated as a managed bean:

```java
import org.nanoboot.annotation.Annotation.Component;

@Component
public class UserService {
    // This class will be managed by the DI container
}
```

### Component Stereotypes

NanoBoot supports several stereotypes that are aliases for `@Component`:

- `@Service` - For service layer components
- `@Repository` - For data access components (coming soon)
- `@Controller` - For web controllers

### Dependency Injection with @Autowired

Use `@Autowired` to inject dependencies into fields, constructors, or methods:

```java
@Component
public class OrderService {

    @Autowired
    private UserService userService;  // Field injection

    @Autowired
    private PaymentService paymentService;

    public String processOrder(Long userId, BigDecimal amount) {
        User user = userService.findById(userId);
        // Process the order using injected services
        return paymentService.processPayment(user, amount);
    }
}
```

### Constructor Injection

Constructor injection is preferred as it ensures required dependencies are always provided:

```java
@Component
public class OrderProcessor {
    private final UserService userService;
    private final PaymentService paymentService;

    @Autowired  // Constructor injection (optional in newer versions)
    public OrderProcessor(UserService userService, PaymentService paymentService) {
        this.userService = userService;
        this.paymentService = paymentService;
    }

    public void processOrder(Order order) {
        // Use injected dependencies
    }
}
```

## Property Injection

Use `@Value` to inject configuration properties:

```java
@Component
public class AppConfig {

    @Value("${app.name:DefaultApp}")
    private String appName;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${database.url:jdbc:h2:mem:testdb}")
    private String databaseUrl;

    // Getters and setters...
}
```

### Property Sources

Properties can come from:
- `application.properties` file
- System properties
- Environment variables
- Command line arguments

## Qualifiers and Named Beans

When you have multiple beans of the same type, use `@Qualifier` to specify which one to inject:

```java
// Define multiple implementations
@Component("emailNotifier")
public class EmailNotificationService implements NotificationService {
    // Implementation
}

@Component("smsNotifier")
public class SmsNotificationService implements NotificationService {
    // Implementation
}

// Inject specific implementation
@Component
public class OrderService {

    @Autowired
    @Qualifier("emailNotifier")
    private NotificationService notificationService;
}
```

## Lifecycle Annotations

### @PostConstruct

Methods annotated with `@PostConstruct` run after dependency injection is complete:

```java
@Component
public class DatabaseInitializer {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void initializeDatabase() {
        // Initialize database schema or seed data
        // Called after all dependencies are injected
        System.out.println("Database initialized");
    }
}
```

### @PreDestroy

Methods annotated with `@PreDestroy` run before the bean is destroyed:

```java
@Component
public class ConnectionPoolManager {

    private ConnectionPool pool;

    @PreDestroy
    public void cleanup() {
        // Close connections and clean up resources
        if (pool != null) {
            pool.close();
        }
        System.out.println("Connection pool closed");
    }
}
```

## Scopes and Singleton Pattern

By default, all beans in NanoBoot are singletons (one instance per application). The container manages the lifecycle and ensures thread-safe access.

## Advanced Configuration

### Component Scanning

The main application class with `@NanoBootApplication` enables component scanning automatically:

```java
@NanoBootApplication
@ComponentScan(basePackages = {"com.example.service", "com.example.controller"})
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### Conditional Bean Creation

Beans can be created conditionally based on property values:

```java
@Configuration
public class ConditionalConfig {

    @Bean
    @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
    public FeatureService featureService() {
        return new FeatureServiceImpl();
    }
}
```

## Best Practices

### 1. Use Constructor Injection
Constructor injection ensures required dependencies are always provided and makes testing easier.

### 2. Avoid Circular Dependencies
Design your components to avoid circular dependencies. If you have circular dependencies, consider refactoring with a third component.

### 3. Group Related Components
Organize your code into logical packages based on functionality.

### 4. Use Appropriate Stereotypes
Use `@Service`, `@Controller`, etc. appropriately to indicate the role of components.

### 5. Configure Properties Externally
Use `@Value` annotations with external configuration rather than hardcoded values.

## Troubleshooting

### Common Issues

1. **Bean Creation Failure**: Ensure all `@Autowired` dependencies exist and are properly annotated.

2. **Circular Dependencies**: Use `@Lazy` annotation or refactor to break cycles.

3. **Missing Properties**: Verify property names in `application.properties` match `@Value` annotations.

4. **Component Not Found**: Check that the component is properly annotated and in a scanned package.

The dependency injection system in NanoBoot provides a robust foundation for managing component relationships and configuration, making your applications more modular, testable, and maintainable.