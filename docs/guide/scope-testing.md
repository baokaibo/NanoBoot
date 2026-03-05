# Bean 作用域测试

本文档描述了 NanoBoot 框架中 Bean 作用域的测试结果。

## 测试概述

测试用例验证了以下作用域:
- **Singleton** - 单例作用域（默认）
- **Prototype** - 原型作用域（每次获取创建新实例）

## 测试结果

```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

**全部通过 ✅**

## 测试用例详情

### 1. testSingletonScope - 单例作用域测试

**目标**: 验证单例 Bean 在多次获取时返回相同实例

**代码**:
```java
SingletonBean singleton1 = context.getBean(SingletonBean.class);
SingletonBean singleton2 = context.getBean(SingletonBean.class);

assertSame(singleton1, singleton2);
assertEquals(singleton1.getInstanceId(), singleton2.getInstanceId());
assertEquals(1, SingletonBean.getInstanceCount());
```

**输出**:
```
SingletonBean created, instance #1
```

**结论**: ✅ 单例 Bean 只创建一次，后续获取返回同一实例

---

### 2. testPrototypeScope - 原型作用域测试

**目标**: 验证原型 Bean 每次获取时创建新实例

**代码**:
```java
PrototypeBean prototype1 = context.getBean(PrototypeBean.class);
PrototypeBean prototype2 = context.getBean(PrototypeBean.class);
PrototypeBean prototype3 = context.getBean(PrototypeBean.class);

assertNotSame(prototype1, prototype2);
assertNotSame(prototype2, prototype3);
assertEquals(3, PrototypeBean.getInstanceCount());
```

**输出**:
```
PrototypeBean created, instance #1
PrototypeBean created, instance #2
PrototypeBean created, instance #3
```

**结论**: ✅ 原型 Bean 每次获取都创建新实例

---

### 3. testSingletonAndPrototypeTogether - 混合作用域测试

**目标**: 验证单例和原型 Bean 可以共存

**代码**:
```java
SingletonBean singleton = context.getBean(SingletonBean.class);
SingletonBean singleton2 = context.getBean(SingletonBean.class);
assertSame(singleton, singleton2);

PrototypeBean prototype1 = context.getBean(PrototypeBean.class);
PrototypeBean prototype2 = context.getBean(PrototypeBean.class);
assertNotSame(prototype1, prototype2);
```

**结论**: ✅ 单例和原型 Bean 可以正常工作并共存

---

## 使用方法

### 定义单例 Bean（默认）
```java
@Component
public class MySingletonBean {
    // 默认为 singleton 作用域
}
```

### 定义原型 Bean
```java
@Component
@Scope("prototype")
public class MyPrototypeBean {
    // 每次 getBean() 创建新实例
}
```

---

## 总结

| 作用域 | 说明 | 测试结果 |
|--------|------|----------|
| singleton | 单例（默认） | ✅ 通过 |
| prototype | 原型（每次创建新实例） | ✅ 通过 |
| request | 请求作用域 | 预留（Web模块） |
| session | 会话作用域 | 预留（Web模块） |
