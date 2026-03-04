# CLI 工具

NanoBoot CLI 工具提供了一个命令行界面，用于快速高效地创建新的 NanoBoot 项目。

## 概述

NanoBoot CLI 工具简化了创建新 NanoBoot 应用程序的过程。只需一个命令，即可生成包含所有必要依赖项、配置文件和示例代码的完整项目结构。

## 安装

### 前提条件

在使用 CLI 工具之前，请确保您具备：

- Java 8 或更高版本
- Maven 3.6 或更高版本（可选，用于构建项目）

### 构建 CLI 工具

如果从源码构建：

```bash
# 导航到 CLI 模块目录
cd nano-boot-cli

# 构建 CLI 工具
mvn clean package

# JAR 文件将在 target 目录中创建
ls target/nano-boot-cli-*.jar
```

### 运行 CLI 工具

CLI 工具可以直接使用 Java 运行：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar [command] [options]
```

## 命令

### create

创建指定名称的新 NanoBoot 项目。

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create <project-name>
```

**参数：**
- `<project-name>`: 要创建的项目名称

**示例：**
```bash
# 创建名为 "my-app" 的新项目
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

此命令将：
- 创建项目目录结构
- 生成主应用程序类
- 创建控制器、服务和 DTO 示例文件
- 生成 Maven 配置（pom.xml）
- 创建带有默认配置的 application.properties
- 生成 .gitignore 文件

### --version / -v

显示 CLI 工具的版本信息。

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# 或
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -v
```

**示例输出：**
```
NanoBoot CLI v1.0.0
```

### help / -h

显示有关可用命令的帮助信息。

```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar help
# 或
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

**示例输出：**
```
用法: nanoboot [command] [options]

命令:
  create <project-name>    创建新的 NanoBoot 项目
  --version, -v           显示版本信息
  help, -h                显示此帮助消息

示例:
  nanoboot create my-app
```

## 生成的项目结构

当您运行 `create` 命令时，CLI 工具会生成以下项目结构：

```
my-app/
├── pom.xml                 # 带有 NanoBoot 依赖的 Maven 配置
├── .gitignore              # Git 忽略模式
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # 主应用程序入口点
│   │   │           ├── controller/
│   │   │           │   └── ApplicationController.java  # 示例 REST 控制器
│   │   │           ├── service/
│   │   │           │   └── ApplicationService.java     # 示例服务
│   │   │           └── dto/
│   │   │               └── GreetingRequest.java        # 数据传输对象
│   │   └── resources/
│   │       └── application.properties          # 配置属性
│   └── test/
│       └── java/
└── target/                 # 构建输出目录
```

## 生成的文件

### 主应用程序类

CLI 工具生成带有 `@NanoBootApplication` 注解的主应用程序类：

```java
package com.example;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.starter.NanoBootApplicationRunner;

@NanoBootApplication
public class MyAppApplication {

    public static void main(String[] args) {
        System.out.println("正在启动 NanoBoot 应用程序...");
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### 示例控制器

创建带有基本端点的示例 REST 控制器：

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

### 示例服务

带依赖注入的示例服务类：

```java
package com.example.service;

import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Value;

@Service
public class ApplicationService {

    @Value("${app.name:DefaultApp}")
    private String appName;

    public String getAppInfo() {
        return "应用程序: " + appName + " 运行在 NanoBoot 上";
    }

    public String greet(String name) {
        return "你好 " + name + "! 欢迎使用 " + appName + "。";
    }
}
```

## 示例

### 创建新项目

```bash
# 创建名为 "blog-api" 的新项目
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api

# 导航到项目目录
cd blog-api

# 构建项目
mvn clean compile

# 运行应用程序
java -jar target/blog-api-0.0.1-SNAPSHOT.jar
```

### 创建具有不同命名的项目

CLI 工具会自动将项目名称转换为适当的类名：

```bash
# 创建 MyAwesomeProjectApplication.java
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-awesome-project

# 创建 UserManagementSystemApplication.java
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create user_management_system
```

CLI 工具提供了一种快速开始新 NanoBoot 项目的方法，其中包含从一开始就内置的最佳实践。