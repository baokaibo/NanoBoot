# CLI Installation

Learn how to install and set up the NanoBoot CLI tool for creating new projects.

## Prerequisites

Before installing the NanoBoot CLI, ensure your system meets the following requirements:

### System Requirements
- **Operating System**: Windows, macOS, or Linux
- **Java**: Java 8 or higher (Java 11+ recommended)
- **Maven**: Apache Maven 3.6 or higher (optional, for building projects)

### Verifying Prerequisites

Check that you have Java installed:

```bash
java -version
```

Expected output (example):
```
openjdk version "11.0.12" 2021-07-20
OpenJDK Runtime Environment (build 11.0.12+7-Ubuntu-0ubuntu1)
OpenJDK 64-Bit Server VM (build 11.0.12+7-Ubuntu-0ubuntu1, mixed mode, sharing)
```

Check that you have Maven installed (if needed):

```bash
mvn -version
```

## Installation Methods

### Method 1: Download Pre-built JAR (Recommended)

The easiest way to use the CLI is to download the pre-built JAR file:

1. Download the latest release from the NanoBoot releases page
2. Save the JAR file to a convenient location
3. Use it directly with Java:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

### Method 2: Build from Source

If you prefer to build the CLI from source:

1. Clone the NanoBoot repository:
```bash
git clone https://github.com/nanoboot/framework.git
cd framework
```

2. Navigate to the CLI module:
```bash
cd nano-boot-cli
```

3. Build the project:
```bash
mvn clean package
```

4. The JAR file will be created in the `target/` directory:
```bash
ls target/nano-boot-cli-*.jar
```

### Method 3: Create an Alias or Script

To make the CLI easier to use, create an alias or script:

#### On Unix/Linux/macOS

Add to your shell profile (`.bashrc`, `.zshrc`, etc.):

```bash
alias nanoboot='java -jar /path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar'
```

Or create an executable script:

```bash
#!/bin/bash
java -jar /path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar "$@"
```

Save as `/usr/local/bin/nanoboot` and make executable:
```bash
chmod +x /usr/local/bin/nanoboot
```

#### On Windows

Create a batch file `nanoboot.bat`:

```batch
@echo off
java -jar C:\path\to\nano-boot-cli-1.0.0-SNAPSHOT.jar %*
```

Add the directory containing the batch file to your PATH.

## Verification

Verify that the CLI is installed correctly:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar --version
```

Expected output:
```
NanoBoot CLI v1.0.0
```

Also test the help command:

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar help
```

## Troubleshooting

### Common Issues

#### 1. Java Not Found
If you get "java: command not found", install Java and ensure it's in your PATH.

#### 2. Permission Denied (Unix/Linux/macOS)
If you get a permission error, ensure the JAR file is readable:
```bash
chmod +r path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar
```

#### 3. Outdated Java Version
If you get compatibility errors, ensure you're using Java 8 or higher:
```bash
java -version
```

#### 4. Invalid or Corrupted JAR
If you get "invalid JAR file" errors, redownload or rebuild the JAR file.

## Updating

To update to a newer version of the CLI:

1. Download the new version
2. Replace the old JAR file
3. Verify the installation with the version command

If you built from source, pull the latest changes and rebuild:

```bash
git pull origin main
cd nano-boot-cli
mvn clean package
```

## Uninstallation

To uninstall the CLI:

1. Remove the JAR file
2. If you created an alias or script, remove it as well
3. Clean up any references in your shell configuration

The NanoBoot CLI is portable and doesn't require complex installation procedures, making it easy to use and maintain.