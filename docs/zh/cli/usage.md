# CLI 使用方法

了解如何使用 NanoBoot CLI 工具创建和管理项目。

## 基本命令

### 创建项目

主要命令是 create，它会生成一个新的 NanoBoot 项目：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create <project-name>
```

**示例：**
```bash
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-first-app
```

这将创建一个名为 my-first-app 的新目录，其中包含完整的项目结构。

### 检查版本

获取 CLI 工具的当前版本：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# 或
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar -v
```

### 获取帮助

显示有关可用命令的帮助信息：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar help
# 或
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

## 项目创建过程

当您运行 create 命令时，CLI 会执行以下步骤：

1. **创建目录结构**，具有正确的 Maven 布局
2. **生成主应用程序类**，带有 @NanoBootApplication 注解
3. **创建示例组件**，包括控制器、服务和 DTO
4. **设置配置文件**，包括 application.properties
5. **生成 Maven 配置**，包含必要的依赖项
6. **创建 .gitignore**，带有适当的模式

## 项目名称

### 命名约定

CLI 在将项目名称转换为类名时遵循特定约定：

```bash
# 创建 MyWebAppApplication.java
java -jar cli.jar create my-web-app

# 创建 BlogApiApplication.java
java -jar cli.jar create blog_api

# 创建 SimpleApplication.java（添加 Application 后缀）
java -jar cli.jar create simple

# 创建 ApiGatewayApplication.java（已经以 app 结尾）
java -jar cli.jar create api-gateway
```

### 有效的项目名称

项目名称应该：
- 使用小写（推荐）
- 使用连字符（-）或下划线（_）分隔单词
- 以字母开头
- 只包含字母、数字、连字符和下划线
- 不超过 100 个字符

### 包命名

CLI 会根据项目名称自动生成包名称：

```bash
# 创建包 com.example.my.web.app
java -jar cli.jar create my-web-app

# 创建包 com.example.blog.api
java -jar cli.jar create blog-api
```

## 使用创建的项目

### 项目创建后

项目创建后，您可以：

1. **导航到项目目录**：
```bash
cd my-app
```

2. **查看生成的文件**：
```bash
ls -la
```

3. **构建项目**：
```bash
mvn clean compile
```

4. **运行应用程序**：
```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

### 运行应用程序

创建后，您可以通过多种方式运行应用程序：

#### 使用 Maven（开发）
```bash
mvn exec:java -Dexec.mainClass="com.example.MyAppApplication"
```

#### 打包并运行 JAR
```bash
# 打包应用程序
mvn package

# 运行 JAR 文件
java -jar target/my-app-0.0.1-SNAPSHOT.jar
```

## 生成的项目结构

CLI 创建以下目录结构：

```
my-app/
├── pom.xml                    # Maven 配置
├── .gitignore                # Git 忽略模式
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

### 主应用程序类

CLI 生成一个完整的主类：

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

创建一个示例 REST 控制器：

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

## 自定义选项

虽然 CLI 生成标准项目结构，但您可以自定义：

### 依赖项

修改生成的 pom.xml 以添加或删除依赖项：

```xml
<dependencies>
    <!-- 核心 NanoBoot 依赖（必需） -->
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-starter</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>

    <!-- 根据需要添加其他依赖项 -->
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-data</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>
</dependencies>
```

### 配置

编辑 application.properties 来配置您的应用程序：

```properties
# 应用程序属性
app.name=my-app
server.port=8080

# 数据库配置（如果使用 data 模块）
# database.mysql.jdbcUrl=jdbc:mysql://localhost:3306/mydb
# database.mysql.username=user
# database.mysql.password=password

# 自定义属性
custom.feature.enabled=true
logging.level.com.example=DEBUG
```

## 最佳实践

### 1. 项目组织

- 将相关功能保存在适当的包中
- 以生成的结构为基础
- 遵循分层架构（controller -> service -> repository）

### 2. 命名约定

- 为包、类和方法使用描述性名称
- 遵循 Java 命名约定
- 使用一致的命名模式

### 3. 文档

- 使用项目特定信息更新生成的注释
- 记录自定义配置
- 为团队成员添加 README 文件

### 4. 配置管理

- 敏感信息不要放在配置文件中
- 使用特定于环境的配置文件
- 记录配置选项

## 故障排除

### 常见问题

#### 1. 目录已存在
如果项目目录已存在，CLI 将失败。请选择其他名称或删除现有目录。

#### 2. 权限问题
确保您对目标目录有写权限。

#### 3. Java 版本兼容性
确保运行生成的应用程序时使用的是 Java 8 或更高版本。

#### 4. Maven 构建问题
如果项目无法构建，请验证：
- Java 版本正确
- Maven 已安装
- 网络连接正常以便下载依赖项

## 示例

### 创建不同类型的项目

```bash
# API 项目
java -jar cli.jar create user-management-api

# Web 应用程序
java -jar cli.jar create company-dashboard

# 微服务
java -jar cli.jar create order-processing-service

# 示例项目
java -jar cli.jar create hello-world-demo
```

### 完整工作流程

```bash
# 1. 创建项目
java -jar cli.jar create my-project

# 2. 导航到项目
cd my-project

# 3. 构建项目
mvn clean compile

# 4. 运行应用程序
mvn exec:java -Dexec.mainClass="com.example.MyProjectApplication"

# 5. 访问 http://localhost:8080/api/info
```

CLI 工具简化了启动新的 NanoBoot 应用程序的过程，为您的项目提供了一致的基础。
