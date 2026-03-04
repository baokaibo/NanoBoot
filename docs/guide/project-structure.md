# Project Structure

Understanding the standard NanoBoot project structure helps you navigate and organize your application effectively.

## Standard Directory Layout

A typical NanoBoot application follows this directory structure:

```
my-app/
├── pom.xml                 # Maven configuration and dependencies
├── .gitignore              # Git ignore patterns
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # Main application entry point
│   │   │           ├── controller/              # Web controllers
│   │   │           │   └── ApplicationController.java
│   │   │           ├── service/                 # Business logic services
│   │   │           │   └── ApplicationService.java
│   │   │           ├── repository/              # Data access layer (optional)
│   │   │           │   └── UserRepository.java
│   │   │           └── dto/                     # Data Transfer Objects
│   │   │               └── UserDto.java
│   │   └── resources/                           # Configuration files
│   │       ├── application.properties           # Main configuration
│   │       └── templates/                       # Template files (if needed)
│   └── test/                                    # Unit and integration tests
│       └── java/
│           └── com/
│               └── example/
│                   └── MyAppApplicationTests.java
└── target/                 # Build output directory (generated during build)
```

## Key Files and Directories

### pom.xml

This is your Maven configuration file that manages:

- Project coordinates (groupId, artifactId, version)
- Dependencies on NanoBoot modules
- Build plugins and configurations
- Java compiler settings

### Main Application Class

Located at `src/main/java/com/example/MyAppApplication.java`, this is the entry point:

```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### Application Properties

The `src/main/resources/application.properties` file contains configuration:

```properties
# Application Properties
app.name=my-app
server.port=8080
# Database Configuration (examples)
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=
```

### Package Organization

The recommended package structure separates concerns:

- **controllers**: Handle HTTP requests and responses
- **services**: Contain business logic
- **repositories**: Handle data persistence (when using data module)
- **dto**: Data Transfer Objects for API contracts
- **config**: Configuration classes (when needed)

## Module-Specific Structures

Depending on which NanoBoot modules you use, you might have additional directories:

### Data Module

If using `nano-boot-data`:

```
src/main/java/com/example/
├── config/
│   └── DatabaseConfig.java
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java
└── entity/
    ├── User.java
    └── Product.java
```

### WebSocket Module

If using `nano-boot-websocket`:

```
src/main/java/com/example/
└── websocket/
    ├── ChatEndpoint.java
    └── MessageHandler.java
```

## Configuration Files

### application.properties

Primary configuration file for:

- Server settings (port, context path)
- Database connections
- Custom application properties
- Third-party service configurations

### .gitignore

Standard ignores for:

- Build artifacts (`target/`)
- IDE files
- Log files
- Temporary files
- Secrets and configuration files

## Testing Structure

Unit and integration tests follow the same package structure as the main code:

```java
@Test
public void testUserService() {
    // Test code here
}
```

## Best Practices

### Package Naming

Use reverse domain name notation:
- `com.company.project` - For company projects
- `org.projectname` - For open source projects

### Layer Separation

Maintain clear separation between layers:
- Controllers should only handle HTTP concerns
- Services should contain business logic
- Repositories should handle data persistence

### Configuration Management

Store configuration in `application.properties`:
- Use meaningful property names
- Provide default values with `${property:defaultValue}`
- Separate environment-specific configs when needed

### Resource Organization

Keep resources organized:
- Static assets in appropriate directories
- Templates in `src/main/resources/templates`
- Properties files in `src/main/resources`

This structure provides a solid foundation for organizing your NanoBoot applications and scales well as your project grows.