# CLI 安装

了解如何安装和设置 NanoBoot CLI 工具以创建新项目。

## 前提条件

在安装 NanoBoot CLI 之前，请确保您的系统满足以下要求：

### 系统要求
- 操作系统：Windows、macOS 或 Linux
- Java：Java 8 或更高版本（推荐 Java 11+）
- Maven：Apache Maven 3.6 或更高版本（可选，用于构建项目）

### 验证前提条件

检查您是否已安装 Java：

```bash
java -version
```

预期输出（示例）：
```
openjdk version "11.0.12" 2021-07-20
OpenJDK Runtime Environment (build 11.0.12+7-Ubuntu-0ubuntu1)
OpenJDK 64-Bit Server VM (build 11.0.12+7-Ubuntu-0ubuntu1, mixed mode, sharing)
```

检查您是否已安装 Maven（如需要）：

```bash
mvn -version
```

## 安装方法

### 方法 1：下载预构建的 JAR（推荐）

使用 CLI 的最简单方法是下载预构建的 JAR 文件：

1. 从 NanoBoot 发布页面下载最新版本
2. 将 JAR 文件保存到方便的位置
3. 直接使用 Java 运行：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar create my-app
```

### 方法 2：从源码构建

如果您更喜欢从源码构建 CLI：

1. 克隆 NanoBoot 仓库：
```bash
git clone https://github.com/nanoboot/framework.git
cd framework
```

2. 导航到 CLI 模块：
```bash
cd nano-boot-cli
```

3. 构建项目：
```bash
mvn clean package
```

4. JAR 文件将创建在 target/ 目录中：
```bash
ls target/nano-boot-cli-*.jar
```

### 方法 3：创建别名或脚本

为方便使用 CLI，请创建别名或脚本：

#### 在 Unix/Linux/macOS 上

添加到您的 shell 配置文件（.bashrc、.zshrc 等）：

```bash
alias nanoboot='java -jar /path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar'
```

或创建可执行脚本：

```bash
#!/bin/bash
java -jar /path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar "$@"
```

保存为 /usr/local/bin/nanoboot 并设置可执行权限：
```bash
chmod +x /usr/local/bin/nanoboot
```

#### 在 Windows 上

创建批处理文件 nanoboot.bat：

```batch
@echo off
java -jar C:\path\to\nano-boot-cli-1.0.0-SNAPSHOT.jar %*
```

将包含批处理文件的目录添加到您的 PATH。

## 验证

验证 CLI 是否正确安装：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar --version
```

预期输出：
```
NanoBoot CLI v1.0.0
```

也测试帮助命令：

```bash
java -jar path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar help
```

## 故障排除

### 常见问题

#### 1. 找不到 Java
如果您收到 "java: command not found"，请安装 Java 并确保它在您的 PATH 中。

#### 2. 权限被拒绝（Unix/Linux/macOS）
如果您收到权限错误，请确保 JAR 文件可读：
```bash
chmod +r path/to/nano-boot-cli-1.0.0-SNAPSHOT.jar
```

#### 3. Java 版本过旧
如果您收到兼容性错误，请确保您使用的是 Java 8 或更高版本：
```bash
java -version
```

#### 4. JAR 文件无效或损坏
如果您收到 "invalid JAR file" 错误，请重新下载或重新构建 JAR 文件。

## 更新

要更新到更新版本的 CLI：

1. 下载新版本
2. 替换旧的 JAR 文件
3. 使用版本命令验证安装

如果您从源码构建，请拉取最新更改并重新构建：

```bash
git pull origin main
cd nano-boot-cli
mvn clean package
```

## 卸载

要卸载 CLI：

1. 删除 JAR 文件
2. 如果您创建了别名或脚本，也将其删除
3. 清理 shell 配置中的任何引用

NanoBoot CLI 是可移植的，不需要复杂的安装程序，使其易于使用和维护。
