# CLI Commands

Detailed documentation of all available commands in the NanoBoot CLI tool.

## Command Overview

The NanoBoot CLI provides a simple interface for generating new projects and managing NanoBoot applications.

### Syntax

```bash
java -jar nano-boot-cli-[version].jar [command] [arguments]
```

## Available Commands

### create

Creates a new NanoBoot project with the specified name and structure.

#### Syntax

```bash
java -jar nano-boot-cli-[version].jar create <project-name>
```

#### Arguments

- `<project-name>` (required): The name of the project to create. This will become:
  - The directory name for the project
  - Part of the package structure
  - The basis for the main application class name

#### Description

The `create` command generates a complete NanoBoot project with:

- Maven project structure
- Main application class annotated with `@NanoBootApplication`
- Example controller, service, and DTO classes
- Maven configuration file (pom.xml) with all necessary dependencies
- Application properties file with default configuration
- Git ignore file with common patterns

#### Examples

```bash
# Create a project named "my-app"
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app

# Create a project with hyphens in the name
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api

# Create a project with underscores
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create user_management_service
```

#### Generated Structure

When you run the `create` command, the following structure is generated:

```
<project-name>/
├── pom.xml                 # Maven configuration
├── .gitignore              # Git ignore patterns
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── [MainClassName].java
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
└── target/                 # Build output (created during compilation)
```

### --version, -v

Displays the version of the NanoBoot CLI tool.

#### Syntax

```bash
java -jar nano-boot-cli-[version].jar --version
# or
java -jar nano-boot-cli-[version].jar -v
```

#### Description

Shows the current version of the CLI tool, which is useful for verifying the installation and checking for updates.

#### Examples

```bash
# Display version information
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# Output: NanoBoot CLI v1.0.0

# Short form
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -v
# Output: NanoBoot CLI v1.0.0
```

### help, -h

Displays help information about available commands and their usage.

#### Syntax

```bash
java -jar nano-boot-cli-[version].jar help
# or
java -jar nano-boot-cli-[version].jar -h
```

#### Description

Provides information about all available commands, their syntax, and examples. This is useful when you need a quick reference or have forgotten the command syntax.

#### Examples

```bash
# Display help information
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar help

# Short form
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

## Command Details

### create Command

The `create` command is the primary functionality of the NanoBoot CLI. It sets up a complete project structure with best practices built-in.

#### Project Name Conventions

The CLI follows specific conventions when generating project names:

| Input | Package | Main Class |
|-------|---------|------------|
| `my-app` | `com.example.my.app` | `MyAppApplication.java` |
| `blog_api` | `com.example.blog.api` | `BlogApiApplication.java` |
| `simple` | `com.example.simple` | `SimpleApplication.java` |
| `web-application` | `com.example.web.application` | `WebApplicationApplication.java` |

#### Generated Files

When the `create` command is executed, the following files are generated:

**pom.xml**: Maven configuration with NanoBoot dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-starter</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>
    <!-- Additional dependencies -->
</dependencies>
```

**Application Properties**:
```properties
# Application Properties
app.name=<project-name>
server.port=8080
# Additional configuration options
```

**Main Application Class**:
```java
@NanoBootApplication
public class [GeneratedClassName] {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run([GeneratedClassName].class, args);
    }
}
```

### --version Command

The version command provides quick access to the CLI tool's version information.

#### Output Format

The version command returns output in the format:
```
NanoBoot CLI v[major].[minor].[patch]
```

For example: `NanoBoot CLI v1.0.0`

### help Command

The help command provides detailed information about all available commands.

#### Output Format

The help command returns information in the following format:
```
Usage: nanoboot [command] [options]

Commands:
  create <project-name>    Create a new NanoBoot project
  --version, -v           Show version information
  help, -h                Show this help message

Examples:
  nanoboot create my-app
```

## Advanced Usage

### Using Aliases

You can create an alias to simplify usage:

**Unix/Linux/macOS**:
```bash
alias nanoboot='java -jar /path/to/nano-boot-cli-[version].jar'
nanoboot create my-app
```

**Windows Command Prompt**:
```cmd
doskey nanoboot=java -jar C:\path\to\nano-boot-cli-[version].jar $*
nanoboot create my-app
```

### Combining Commands

Most commands are independent and don't combine, but you can chain shell commands:

```bash
# Create a project and navigate to it
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app && cd my-app
```

## Error Handling

### Common Errors

**Project Directory Already Exists**:
```
Error: Directory 'my-app' already exists
```
Solution: Remove the existing directory or choose a different project name.

**Insufficient Permissions**:
```
Error: Cannot create directory at [path]
```
Solution: Check directory permissions and ensure you have write access.

**Invalid Project Name**:
```
Error: Invalid project name format
```
Solution: Use alphanumeric characters, hyphens, or underscores only.

### Exit Codes

- `0`: Success
- `1`: General error
- `2`: Invalid command or arguments

## Troubleshooting

### Command Not Found

If you get "java: command not found", ensure Java is installed and in your PATH:
```bash
java -version
```

### Permission Issues

On Unix systems, ensure the JAR file is executable:
```bash
chmod +x nano-boot-cli-[version].jar
```

### Java Version Issues

Ensure you're using Java 8 or higher:
```bash
java -version
```

The CLI commands provide a simple but powerful interface for starting new NanoBoot projects with minimal setup required.