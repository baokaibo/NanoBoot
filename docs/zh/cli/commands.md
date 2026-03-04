# CLI 命令

NanoBoot CLI 工具中所有可用命令的详细文档。

## 命令概述

NanoBoot CLI 提供了一个简单的界面，用于生成新项目和管理 NanoBoot 应用程序。

### 语法

```bash
java -jar nano-boot-cli-[version].jar [command] [arguments]
```

## 可用命令

### create

创建具有指定名称和结构的新 NanoBoot 项目。

#### 语法

```bash
java -jar nano-boot-cli-[version].jar create <project-name>
```

#### 参数

- `<project-name>`（必需）：要创建的项目名称。这将成为：
  - 项目的目录名称
  - 包结构的一部分
  - 主应用程序类名称的基础

#### 描述

create 命令生成一个完整的 NanoBoot 项目，包括：

- Maven 项目结构
- 带有 @NanoBootApplication 注解的主应用程序类
- 示例控制器、服务和 DTO 类
- Maven 配置文件（pom.xml），包含所有必要的依赖项
- 带有默认配置的应用程序属性文件
- 带有常见模式的 Git 忽略文件

#### 示例

```bash
# 创建名为 "my-app" 的项目
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app

# 创建名称中带连字符的项目
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create blog-api

# 创建名称中带下划线的项目
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create user_management_service
```

#### 生成的结构

当您运行 create 命令时，会生成以下结构：

```
<project-name>/
├── pom.xml                 # Maven 配置
├── .gitignore              # Git 忽略模式
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
└── target/                 # 构建输出（在编译期间创建）
```

### --version, -v

显示 NanoBoot CLI 工具的版本。

#### 语法

```bash
java -jar nano-boot-cli-[version].jar --version
# 或
java -jar nano-boot-cli-[version].jar -v
```

#### 描述

显示 CLI 工具的当前版本，用于验证安装和检查更新。

#### 示例

```bash
# 显示版本信息
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar --version
# 输出: NanoBoot CLI v1.0.0

# 短格式
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -v
# 输出: NanoBoot CLI v1.0.0
```

### help, -h

显示有关可用命令及其用法的帮助信息。

#### 语法

```bash
java -jar nano-boot-cli-[version].jar help
# 或
java -jar nano-boot-cli-[version].jar -h
```

#### 描述

提供有关所有可用命令、其语法和示例的信息。当您需要快速参考或忘记命令语法时，这很有用。

#### 示例

```bash
# 显示帮助信息
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar help

# 短格式
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar -h
```

## 命令详情

### create 命令

create 命令是 NanoBoot CLI 的主要功能。它设置了一个包含内置最佳实践的完整项目结构。

#### 项目名称约定

CLI 在生成项目名称时遵循特定约定：

| 输入 | 包 | 主类 |
|-------|---------|------------|
| `my-app` | `com.example.my.app` | `MyAppApplication.java` |
| `blog_api` | `com.example.blog.api` | `BlogApiApplication.java` |
| `simple` | `com.example.simple` | `SimpleApplication.java` |
| `web-application` | `com.example.web.application` | `WebApplicationApplication.java` |

#### 生成的文件

执行 create 命令时，会生成以下文件：

**pom.xml**：带有 NanoBoot 依赖的 Maven 配置
```xml
<dependencies>
    <dependency>
        <groupId>org.nanoboot</groupId>
        <artifactId>nano-boot-starter</artifactId>
        <version>${nanoboot.version}</version>
    </dependency>
    <!-- 其他依赖项 -->
</dependencies>
```

**应用程序属性**：
```properties
# 应用程序属性
app.name=<project-name>
server.port=8080
# 其他配置选项
```

**主应用程序类**：
```java
@NanoBootApplication
public class [GeneratedClassName] {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run([GeneratedClassName].class, args);
    }
}
```

### --version 命令

version 命令提供快速访问 CLI 工具版本信息的途径。

#### 输出格式

version 命令返回以下格式的输出：
```
NanoBoot CLI v[major].[minor].[patch]
```

例如：`NanoBoot CLI v1.0.0`

### help 命令

help 命令提供有关所有可用命令的详细信息。

#### 输出格式

help 命令以以下格式返回信息：
```
用法: nanoboot [command] [options]

命令:
  create <project-name>    创建新的 NanoBoot 项目
  --version, -v           显示版本信息
  help, -h                显示此帮助消息

示例:
  nanoboot create my-app
```

## 高级用法

### 使用别名

您可以创建别名来简化用法：

**Unix/Linux/macOS**：
```bash
alias nanoboot='java -jar /path/to/nano-boot-cli-[version].jar'
nanoboot create my-app
```

**Windows 命令提示符**：
```cmd
doskey nanoboot=java -jar C:\path\to\nano-boot-cli-[version].jar $*
nanoboot create my-app
```

### 组合命令

大多数命令是独立的，不能组合，但您可以链接 shell 命令：

```bash
# 创建项目并导航到该目录
java -jar nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app && cd my-app
```

## 错误处理

### 常见错误

**项目目录已存在**：
```
错误: 目录 'my-app' 已存在
```
解决方案：删除现有目录或选择其他项目名称。

**权限不足**：
```
错误: 无法在 [path] 创建目录
```
解决方案：检查目录权限并确保您有写访问权限。

**项目名称无效**：
```
错误: 项目名称格式无效
```
解决方案：只使用字母数字字符、连字符或下划线。

### 退出代码

- `0`：成功
- `1`：一般错误
- `2`：命令或参数无效

## 故障排除

### 命令未找到

如果您收到 "java: command not found"，请确保 Java 已安装并在您的 PATH 中：
```bash
java -version
```

### 权限问题

在 Unix 系统上，确保 JAR 文件可执行：
```bash
chmod +x nano-boot-cli-[version].jar
```

### Java 版本问题

确保您使用的是 Java 8 或更高版本：
```bash
java -version
```

CLI 命令提供了一个简单但功能强大的界面，用于启动新的 NanoBoot 项目，只需最少的设置即可。
