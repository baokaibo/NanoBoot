# CLI Tool

The NanoBoot CLI tool provides a command-line interface for creating new NanoBoot projects quickly and efficiently.

## Overview

The NanoBoot CLI tool simplifies the process of creating new NanoBoot applications. With a single command, you can generate a complete project structure with all necessary dependencies, configuration files, and example code.

## Installation

### Prerequisites

Before using the CLI tool, ensure you have:

- Java 8 or higher
- Maven 3.6 or higher

### Building the CLI Tool

If you built the framework from source:

```bash
# Navigate to the CLI module directory
cd nano-boot-cli

# Package the CLI tool
mvn clean package

# The JAR file will be created in the target directory
ls target/nano-boot-cli-*.jar
```

### Running the CLI Tool

The CLI tool can be run directly using Java:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar [command] [options]
```

## Commands

### create

Creates a new NanoBoot project with the specified name.

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create <project-name>
```

**Arguments:**
- `<project-name>`: Name of the project to create

**Example:**
```bash
# Create a new project called "my-app"
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

This command will:
- Create the project directory structure
- Generate the main application class
- Create controller, service, and DTO example files
- Generate the Maven configuration (pom.xml)
- Create application.properties with default configuration
- Generate a .gitignore file

### --version / -v

Displays the version information of the CLI tool.

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# or
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -v
```

**Example Output:**
```
NanoBoot CLI v1.0.0
```

### help / -h

Displays help information about available commands.

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar help
# or
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

**Example Output:**
```
Usage: nanoboot [command] [options]

Commands:
  create <project-name>    Create a new NanoBoot project
  --version, -v           Show version information
  help, -h                Show this help message

Examples:
  nanoboot create my-app
```

## Generated Project Structure

When you run the `create` command, the CLI tool generates the following project structure:

```
my-app/
├── pom.xml                 # Maven configuration with NanoBoot dependencies
├── .gitignore              # Git ignore patterns
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # Main application entry point
│   │   │           ├── controller/
│   │   │           │   └── ApplicationController.java  # Example REST controller
│   │   │           ├── service/
│   │   │           │   └── ApplicationService.java     # Example service
│   │   │           └── dto/
│   │   │               └── GreetingRequest.java        # Data transfer object
│   │   └── resources/
│   │       └── application.properties          # Configuration properties
│   └── test/
│       └── java/
└── target/                 # Build output directory
```

## Generated Files

### Main Application Class

The CLI tool generates a main application class with the `@NanoBootApplication` annotation:

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

An example REST controller is created with basic endpoints:

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

### Example Service

An example service class with dependency injection:

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

### Maven Configuration

The CLI generates a complete Maven configuration with all necessary dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-app</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>my-app</name>
    <description>Demo project for NanoBoot</description>

    <properties>
        <java.version>1.8</java.version>
        <nanoboot.version>1.0.0-SNAPSHOT</nanoboot.version>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.nanoboot</groupId>
            <artifactId>nano-boot-starter</artifactId>
            <version>${nanoboot.version}</version>
        </dependency>

        <!-- Optional dependencies -->
        <dependency>
            <groupId>org.nanoboot</groupId>
            <artifactId>nano-boot-data</artifactId>
            <version>${nanoboot.version}</version>
        </dependency>
        <dependency>
            <groupId>org.nanoboot</groupId>
            <artifactId>nano-boot-websocket</artifactId>
            <version>${nanoboot.version}</version>
        </dependency>

        <!-- Test dependencies -->
        <dependency>
            <groupId>org.nanoboot</groupId>
            <artifactId>nano-boot-core</artifactId>
            <version>${nanoboot.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>com.example.MyAppApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## Examples

### Creating a New Project

```bash
# Create a new project named "blog-api"
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api

# Navigate to the project directory
cd blog-api

# Build the project
mvn clean compile

# Package the application
mvn package

# Run the application
java -jar target/blog-api-0.0.1-SNAPSHOT.jar
```

### Creating a Project with Different Naming

The CLI tool automatically converts project names to appropriate class names:

```bash
# Creates MyAwesomeProjectApplication.java
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-awesome-project

# Creates UserManagementSystemApplication.java
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create user_management_system
```

## Best Practices

### 1. Project Naming
- Use hyphens for multi-word project names
- Follow standard Java package naming conventions
- Keep names descriptive but concise

### 2. After Generation
- Review and customize the generated application.properties
- Update dependencies in pom.xml if needed
- Modify the main application class if you need custom configuration
- Add your business logic to the generated structure

### 3. Development Workflow
- Use the generated structure as a foundation
- Add new packages for different features
- Follow the layered architecture pattern (controller → service → repository)
- Use dependency injection consistently

The CLI tool provides a quick way to start new NanoBoot projects with best practices built in from the beginning.