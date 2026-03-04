# 项目结构

了解标准的 NanoBoot 项目结构有助于您有效地组织和导航应用程序。

## 标准目录布局

典型的 NanoBoot 应用程序遵循以下目录结构：

```
my-app/
├── pom.xml                 # Maven 配置和依赖项
├── .gitignore              # Git 忽略模式
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           ├── MyAppApplication.java    # 主应用程序入口点
│   │   │           ├── controller/              # Web 控制器
│   │   │           │   └── ApplicationController.java
│   │   │           ├── service/                 # 业务逻辑服务
│   │   │           │   └── ApplicationService.java
│   │   │           ├── repository/              # 数据访问层（可选）
│   │   │           │   └── UserRepository.java
│   │   │           └── dto/                    # 数据传输对象
│   │   │               └── UserDto.java
│   │   └── resources/                          # 配置文件
│   │       ├── application.properties          # 主配置文件
│   │       └── templates/                      # 模板文件（如需要）
│   └── test/                                   # 单元测试和集成测试
│       └── java/
│           └── com/
│               └── example/
│                   └── MyAppApplicationTests.java
└── target/                 # 构建输出目录（构建时生成）
```

## 关键文件和目录

### pom.xml

这是您的 Maven 配置文件，管理以下内容：

- 项目坐标（groupId、artifactId、version）
- NanoBoot 模块依赖项
- 构建插件和配置
- Java 编译器设置

### 主应用程序类

位于 `src/main/java/com/example/MyAppApplication.java`，这是入口点：

```java
@NanoBootApplication
public class MyAppApplication {
    public static void main(String[] args) {
        NanoBootApplicationRunner.run(MyAppApplication.class, args);
    }
}
```

### 应用程序属性

`src/main/resources/application.properties` 文件包含配置：

```properties
# 应用程序属性
app.name=my-app
server.port=8080
# 数据库配置（示例）
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=
```

### 包组织

推荐的分包结构分离了不同的关注点：

- **controllers**：处理 HTTP 请求和响应
- **services**：包含业务逻辑
- **repositories**：处理数据持久化（使用 data 模块时）
- **dto**：用于 API 契约的数据传输对象
- **config**：配置类（如需要）

## 特定模块的结构

根据您使用的 NanoBoot 模块，可能会有额外的目录：

### Data 模块

如果使用 `nano-boot-data`：

```
src/main/java/com/example/
├── config/
│   └── DatabaseConfig.java
├── repository/
│   ├── UserRepository.java
│   └── ProductRepository.java
└── entity/
    ├── User.java
    └── Product.java
```

### WebSocket 模块

如果使用 `nano-boot-websocket`：

```
src/main/java/com/example/
└── websocket/
    ├── ChatEndpoint.java
    └── MessageHandler.java
```

## 配置文件

### application.properties

主要配置文件用于：

- 服务器设置（端口、上下文路径）
- 数据库连接
- 自定义应用程序属性
- 第三方服务配置

### .gitignore

标准忽略项包括：

- 构建产物（`target/`）
- IDE 文件
- 日志文件
- 临时文件
- 密钥和配置文件

## 测试结构

单元测试和集成测试遵循与主代码相同的包结构：

```java
@Test
public void testUserService() {
    // 测试代码
}
```

## 最佳实践

### 包命名

使用反向域名命名法：
- `com.company.project` - 用于公司项目
- `org.projectname` - 用于开源项目

### 分层分离

保持层之间的清晰分离：
- 控制器只处理 HTTP 关注点
- 服务应包含业务逻辑
- 仓库应处理数据持久化

### 配置管理

将配置存储在 `application.properties` 中：
- 使用有意义的属性名
- 使用 `${property:defaultValue}` 提供默认值
- 根据需要分离环境特定的配置

### 资源组织

保持资源有序：
- 静态资源放在适当的目录中
- 模板放在 `src/main/resources/templates`
- 属性文件放在 `src/main/resources`

这种结构为组织您的 NanoBoot 应用程序提供了坚实的基础，并随着项目的增长而具有良好的扩展性。
