# 介绍

NanoBoot 是一个轻量级的 Spring Boot 类似的微框架，专为 Java 开发而设计。它提供了诸如依赖注入、Web 开发、数据访问和 WebSocket 支持等核心功能，形成一个易于使用的模块化包。

## 什么是 NanoBoot？

NanoBoot 是一个 Java 框架，借鉴了 Spring Boot 的设计理念，但更注重轻量化和模块化。它为开发人员提供了构建现代 Java 应用所需的核心功能，而无需庞大的 Spring 生态系统的开销。

### 核心功能

- **依赖注入容器**: 使用 `@Autowired` 进行自动装配
- **注解处理**: 支持 `@Component`、`@Service`、`@Controller` 等注解
- **Web 框架**: 内置 HTTP 服务器，支持路由和 REST API
- **数据访问**: 数据库连接和 Redis 集成
- **WebSocket 支持**: 实时通信能力
- **CLI 工具**: 快速生成项目的命令行界面
- **外部化配置**: 基于属性的配置管理

### 为什么选择 NanoBoot？

- **轻量级**: 比 Spring Boot 更小的体积
- **熟悉易用**: 使用与 Spring 类似的注解和模式
- **模块化**: 只需选择需要的组件
- **启动迅速**: 更快的应用启动时间
- **学习曲线平缓**: 如果你了解 Spring，那么你已经了解 NanoBoot

### 架构概览

NanoBoot 采用模块化架构构建：

- **nano-boot-core**: 核心依赖注入容器
- **nano-boot-starter**: 应用程序引导和注解处理
- **nano-boot-web**: Web 服务器和 HTTP 路由
- **nano-boot-data**: 数据访问层，支持数据库和 Redis
- **nano-boot-websocket**: WebSocket 通信
- **nano-boot-cli**: 用于项目生成的命令行界面

每个模块都可以独立使用或组合起来创建全功能的应用程序。