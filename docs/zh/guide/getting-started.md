# 快速开始

本指南将引导您使用 CLI 工具创建第一个 NanoBoot 应用程序。

## 前提条件

在开始之前，请确保您具备以下条件：

- Java 8 或更高版本
- Maven 3.6 或更高版本
- Git（可选，用于克隆框架）

## 安装

### 安装 CLI 工具

首先，您需要下载并安装 NanoBoot CLI 工具。您可以从源码构建或下载预构建的 JAR 文件。

如果您从源码构建框架：

```bash
# 导航到 CLI 模块目录
cd nano-boot-cli

# 打包 CLI 工具
mvn clean package

# JAR 文件将在 target 目录中创建
ls target/nano-boot-cli-*.jar
```

### 创建您的第一个项目

拥有 CLI 工具后，创建新项目很简单：

```bash
# 创建名为 "my-app" 的新项目
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

此命令将创建一个名为 `my-app` 的新目录，其中包含完整的 NanoBoot 项目结构。

### 项目结构

运行 create 命令后，您将得到以下项目结构：

```
my-app/
├── pom.xml                 # Maven 配置
├── .gitignore              # Git 忽略文件
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # 主应用程序类
│   │   │           ├── controller/
│   │   │           │   └── ApplicationController.java
│   │   │           ├── service/
│   │   │           │   └── ApplicationService.java
│   │   │           └── dto/
│   │   │               └── GreetingRequest.java
│   │   └── resources/
│   │       └── application.properties          # 配置属性
│   └── test/
│       └── java/
└── target/                 # 构建输出（编译时创建）
```

## 运行您的应用程序

### 构建项目

导航到项目目录并使用 Maven 构建它：

```bash
cd my-app
mvn clean compile
```

### 开发模式下运行

要在开发模式下运行应用程序：

```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

或者，如果您的 JAR 已使用清单中的正确主类打包：

```bash
mvn package
java -jar target/my-app-0.0.1-SNAPSHOT.jar
```

您的应用程序将在默认端口 8080 上启动。

## 探索生成的代码

### 主应用程序类

主应用程序类使用 `@NanoBootApplication` 注解：

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

### 控制器示例

生成的控制器演示了基本的 REST API 功能：

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

### 服务示例

服务类演示了依赖注入和属性配置：

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

## 测试您的应用程序

应用程序运行后，您可以测试端点：

- GET `http://localhost:8080/api/info` - 获取应用程序信息
- GET `http://localhost:8080/api/greet?name=John` - 获取个性化问候
- POST `http://localhost:8080/api/greet` 使用 JSON 主体 `{"name": "Jane"}`

## 下一步

现在您有了一个基本的应用程序在运行，您可以：

1. 修改生成的代码以添加业务逻辑
2. 添加新的控制器、服务和组件
3. 配置数据源和数据库连接
4. 为实时通信添加 WebSocket 端点
5. 设置基于属性的配置

继续阅读下一节，了解更多关于 NanoBoot 特性的信息。