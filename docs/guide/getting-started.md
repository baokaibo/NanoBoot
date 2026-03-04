# Getting Started

This guide will walk you through creating your first NanoBoot application using the CLI tool.

## Prerequisites

Before you begin, make sure you have:

- Java 8 or higher
- Maven 3.6 or higher
- Git (optional, for cloning the framework)

## Installation

### Installing the CLI Tool

First, you'll need to download and install the NanoBoot CLI tool. You can either build it from source or download the pre-built JAR.

If you built the framework from source:

```bash
# Navigate to the CLI module directory
cd nano-boot-cli

# Package the CLI tool
mvn clean package

# The JAR file will be created in the target directory
ls target/nano-boot-cli-*.jar
```

### Creating Your First Project

Once you have the CLI tool, creating a new project is simple:

```bash
# Create a new project called "my-app"
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

This command will create a new directory called `my-app` with a complete NanoBoot project structure.

### Project Structure

After running the create command, you'll have the following project structure:

```
my-app/
├── pom.xml                 # Maven configuration
├── .gitignore              # Git ignore file
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # Main application class
│   │   │           ├── controller/
│   │   │           │   └── ApplicationController.java
│   │   │           ├── service/
│   │   │           │   └── ApplicationService.java
│   │   │           └── dto/
│   │   │               └── GreetingRequest.java
│   │   └── resources/
│   │       └── application.properties          # Configuration properties
│   └── test/
│       └── java/
└── target/                 # Build output (created during compilation)
```

## Running Your Application

### Building the Project

Navigate to your project directory and build it with Maven:

```bash
cd my-app
mvn clean compile
```

### Running in Development Mode

To run your application in development mode:

```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

Or if your JAR has been packaged with the correct main class in the manifest:

```bash
mvn package
java -jar target/my-app-0.0.1-SNAPSHOT.jar
```

Your application will start on port 8080 by default.

## Exploring the Generated Code

### Main Application Class

The main application class is annotated with `@NanoBootApplication`:

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

### Controller Example

The generated controller demonstrates basic REST API functionality:

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

### Service Example

The service class demonstrates dependency injection and property configuration:

```java
package com.example.service;

import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Value;

@Service
public class ApplicationService {

    @Value("${app.name:DefaultApp}")
    private String appName;

    public String getAppInfo() {
        return "Application: " + appName + " running on NanoBoot";
    }

    public String greet(String name) {
        return "Hello " + name + "! Welcome to " + appName + ".";
    }
}
```

## Testing Your Application

Once your application is running, you can test the endpoints:

- GET `http://localhost:8080/api/info` - Get application information
- GET `http://localhost:8080/api/greet?name=John` - Get personalized greeting
- POST `http://localhost:8080/api/greet` with JSON body `{"name": "Jane"}`

## Next Steps

Now that you have a basic application running, you can:

1. Modify the generated code to add your business logic
2. Add new controllers, services, and components
3. Configure data sources and database connections
4. Add WebSocket endpoints for real-time communication
5. Set up property-based configuration

Continue to the next sections to learn more about specific features of NanoBoot.