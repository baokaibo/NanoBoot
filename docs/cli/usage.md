# CLI Usage

Learn how to use the NanoBoot CLI tool to create and manage projects.

## Basic Commands

### Creating Projects

The primary command is `create`, which generates a new NanoBoot project:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create <project-name>
```

**Example:**
```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-first-app
```

This creates a new directory called `my-first-app` with a complete project structure.

### Checking Version

Get the current version of the CLI tool:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# or
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar -v
```

### Getting Help

Display help information about available commands:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar help
# or
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

## Project Creation Process

When you run the `create` command, the CLI performs the following steps:

1. **Creates the directory structure** with proper Maven layout
2. **Generates the main application class** with `@NanoBootApplication` annotation
3. **Creates example components** including controllers, services, and DTOs
4. **Sets up configuration files** including `application.properties`
5. **Generates Maven configuration** with necessary dependencies
6. **Creates .gitignore** with appropriate patterns

## Project Names

### Naming Conventions

The CLI follows specific conventions when converting project names to class names:

```bash
# Creates MyWebAppApplication.java
java -jar cli.jar create my-web-app

# Creates BlogApiApplication.java
java -jar cli.jar create blog_api

# Creates SimpleApplication.java (adds "Application" suffix)
java -jar cli.jar create simple

# Creates ApiGatewayApplication.java (already ends with "app")
java -jar cli.jar create api-gateway
```

### Valid Project Names

Project names should:
- Be lowercase (recommended)
- Use hyphens (-) or underscores (_) to separate words
- Start with a letter
- Contain only letters, numbers, hyphens, and underscores
- Not exceed 100 characters

### Package Naming

The CLI automatically generates package names based on the project name:

```bash
# Creates package com.example.my.web.app
java -jar cli.jar create my-web-app

# Creates package com.example.blog.api
java -jar cli.jar create blog-api
```

## Working with Created Projects

### After Project Creation

Once the project is created, you can:

1. **Navigate to the project directory**:
```bash
cd my-app
```

2. **Review the generated files**:
```bash
ls -la
```

3. **Build the project**:
```bash
mvn clean compile
```

4. **Run the application**:
```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

### Running the Application

After creation, you can run your application in several ways:

#### Using Maven (Development)
```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

#### Packaging and Running JAR
```bash
# Package the application
mvn package

# Run the JAR file
java -jar target/my-app-0.0.1-SNAPSHOT.jar
```

## Generated Project Structure

The CLI creates the following directory structure:

```
my-app/
├── pom.xml                    # Maven configuration
├── .gitignore                # Git ignore patterns
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java
│   │   │           ├── controller/
│   │   │           │   └── ApplicationController.java
│   │   │           ├── service/
│   │   │           │   └── ApplicationService.java
│   │   │           └── dto/
│   │   │               └── GreetingRequest.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
└── target/
```

### Main Application Class

The CLI generates a complete main class:

```java
package com.example;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.starter.NanoBootApplicationRunner;

@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        System.out.println("Starting NanoBoot application...");
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### Example Controller

An example REST controller is created:

```java
package com.example.controller;

import org.nanoboot.annotation.Annotation.Autowired;
import org.nanoboot.annotation.Annotation.Controller;
import org.nanoboot.annotation.Annotation.GetMapping;
import org.nanoboot.annotation.Annotation.PostMapping;
import org.nanoboot.annotation.Annotation.RequestParam;
import org.nanoboot.annotation.Annotation.RequestBody;
import org.nanoboot.annotation.Annotation.RequestMapping;
import com.example.service.ApplicationService;
import com.example.dto.GreetingRequest;

@Controller
@RequestMapping("/api")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/info")
    public String getInfo() {
        return applicationService.getAppInfo();
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
        return applicationService.greet(name);
    }

    @PostMapping("/greet")
    public String greetUser(@RequestBody GreetingRequest request) {
        return applicationService.greet(request.getName());
    }
}
```

## Customization Options

While the CLI generates standard project structures, you can customize:

### Dependencies

Modify the generated `pom.xml` to add or remove dependencies:

```xml
<dependencies>
    <!-- Core NanoBoot dependency (required) -->
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-starter</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>

    <!-- Add additional dependencies as needed -->
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-data</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>
</dependencies>
```

### Configuration

Edit `application.properties` to configure your application:

```properties
# Application Properties
app.name=my-app
server.port=8080

# Database Configuration (if using data module)
# database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/mydb
# database.mysql.username=user
# database.mysql.password=password

# Custom properties
custom.feature.enabled=true
logging.level.com.example=DEBUG
```

## Best Practices

### 1. Project Organization

- Keep related functionality in appropriate packages
- Use the generated structure as a foundation
- Follow the layered architecture (controller → service → repository)

### 2. Naming Conventions

- Use descriptive names for packages, classes, and methods
- Follow Java naming conventions
- Use consistent naming patterns

### 3. Documentation

- Update the generated comments with project-specific information
- Document custom configurations
- Add README files for team members

### 4. Configuration Management

- Keep sensitive information out of configuration files
- Use environment-specific configuration files
- Document configuration options

## Troubleshooting

### Common Issues

#### 1. Directory Already Exists
If the project directory already exists, the CLI will fail. Choose a different name or remove the existing directory.

#### 2. Permission Issues
Ensure you have write permissions in the target directory.

#### 3. Java Version Compatibility
Ensure you're using Java 8 or higher when running the generated application.

#### 4. Maven Build Issues
If the project doesn't build, verify:
- Correct Java version
- Maven installation
- Network connectivity for downloading dependencies

## Examples

### Creating Different Types of Projects

```bash
# API project
java -jar cli.jar create user-management-api

# Web application
java -jar cli.jar create company-dashboard

# Microservice
java -jar cli.jar create order-processing-service

# Demo project
java -jar cli.jar create hello-world-demo
```

### Complete Workflow

```bash
# 1. Create the project
java -jar cli.jar create my-project

# 2. Navigate to the project
cd my-project

# 3. Build the project
mvn clean compile

# 4. Run the application
mvn exec:java -Dexec.mainClass="com.example.MyProjectApplication"

# 5. Visit http://localhost:8080/api/info
```

The CLI tool streamlines the process of starting new NanoBoot applications, providing a consistent foundation for your projects.