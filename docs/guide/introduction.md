# Introduction

NanoBoot is a lightweight Spring Boot-like micro-framework designed for Java development. It provides essential features like dependency injection, web development, data access, and WebSocket support in a modular, easy-to-use package.

## What is NanoBoot?

NanoBoot is a Java framework that takes inspiration from Spring Boot but with a focus on being more lightweight and modular. It provides the core features developers need for building modern Java applications without the overhead of a full Spring ecosystem.

### Key Features

- **Dependency Injection Container**: Automatic wiring of components with `@Autowired`
- **Annotation Processing**: Support for `@Component`, `@Service`, `@Controller`, and more
- **Web Framework**: Built-in HTTP server with routing and REST API support
- **Data Access**: Database connections and Redis integration
- **WebSocket Support**: Real-time communication capabilities
- **CLI Tool**: Command-line interface to generate projects quickly
- **Externalized Configuration**: Property-based configuration management

### Why Choose NanoBoot?

- **Lightweight**: Smaller footprint than Spring Boot
- **Familiar**: Uses similar annotations and patterns as Spring
- **Modular**: Choose only the components you need
- **Fast Startup**: Quicker application boot times
- **Easy Learning Curve**: If you know Spring, you know NanoBoot

### Architecture Overview

NanoBoot is built with a modular architecture:

- **nano-boot-core**: Core dependency injection container
- **nano-boot-starter**: Application bootstrap and annotation processing
- **nano-boot-web**: Web server and HTTP routing
- **nano-boot-data**: Data access layer with database and Redis support
- **nano-boot-websocket**: WebSocket communication
- **nano-boot-cli**: Command-line interface for project generation

Each module can be used independently or combined to create a full-featured application.