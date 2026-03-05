# Bean 生命周期回调测试文档

本文档描述了 NanoBoot 框架中 Bean 生命周期回调功能的测试用例，包括初始化回调和销毁回调。

## 测试环境

- **框架版本**: NanoBoot 1.0.0-SNAPSHOT
- **测试框架**: JUnit 4.13.2
- **Java 版本**: 1.8+

## 测试文件结构

```
nano-boot-core/src/test/java/org/nanoboot/core/lifecycle/
├── LifecycleCallbackTest.java      # 主测试类
├── LifecycleTestBean.java          # 完整生命周期测试 Bean
├── SimpleAnnotationBean.java       # 注解方式测试 Bean
├── InitializingBeanOnly.java       # InitializingBean 接口测试
├── DisposableBeanOnly.java         # DisposableBean 接口测试
└── TestBeanPostProcessor.java      # 自定义 BeanPostProcessor
```

---

## 测试用例

### 1. testPostConstructAndPreDestroyAnnotations

**测试目标**: 验证 `@PostConstruct` 和 `@PreDestroy` 注解是否正常工作

**测试代码**:

```java
@Test
public void testPostConstructAndPreDestroyAnnotations() {
    SimpleAnnotationBean.clearLifecycleLog();
    
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultEnvironment environment = new DefaultEnvironment();
    DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);
    
    context.addBeanPostProcessor(new TestBeanPostProcessor());
    context.refresh();
    
    // 验证 @PostConstruct
    List<String> log = SimpleAnnotationBean.getLifecycleLog();
    assertTrue(log.contains("init() called"));
    
    context.close();
    
    // 验证 @PreDestroy
    log = SimpleAnnotationBean.getLifecycleLog();
    assertTrue(log.contains("cleanup() called"));
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testPostConstructAndPreDestroyAnnotations
```

**预期结果**: ✅ 通过

---

### 2. testInitializingBeanInterface

**测试目标**: 验证 `InitializingBean` 接口的 `afterPropertiesSet()` 方法是否被调用

**测试代码**:

```java
@Test
public void testInitializingBeanInterface() {
    InitializingBeanOnly.clearLifecycleLog();
    
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultApplicationContext context = new DefaultApplicationContext(packages, new DefaultEnvironment());
    
    context.refresh();
    
    List<String> log = InitializingBeanOnly.getLifecycleLog();
    assertTrue(log.contains("afterPropertiesSet() called"));
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testInitializingBeanInterface
```

**预期结果**: ✅ 通过

---

### 3. testDisposableBeanInterface

**测试目标**: 验证 `DisposableBean` 接口的 `destroy()` 方法是否在上下文关闭时被调用

**测试代码**:

```java
@Test
public void testDisposableBeanInterface() {
    DisposableBeanOnly.clearLifecycleLog();
    
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultApplicationContext context = new DefaultApplicationContext(packages, new DefaultEnvironment());
    
    context.refresh();
    context.close();
    
    List<String> log = DisposableBeanOnly.getLifecycleLog();
    assertTrue(log.contains("destroy() called"));
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testDisposableBeanInterface
```

**预期结果**: ✅ 通过

---

### 4. testBeanPostProcessor

**测试目标**: 验证 `BeanPostProcessor` 的 `postProcessBeforeInitialization` 和 `postProcessAfterInitialization` 方法是否被调用

**测试代码**:

```java
@Test
public void testBeanPostProcessor() {
    TestBeanPostProcessor.clearLogs();
    
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultApplicationContext context = new DefaultApplicationContext(packages, new DefaultEnvironment());
    
    TestBeanPostProcessor processor = new TestBeanPostProcessor();
    context.addBeanPostProcessor(processor);
    context.refresh();
    
    // 验证 before 回调
    List<String> beforeLog = TestBeanPostProcessor.getBeforeLog();
    assertFalse(beforeLog.isEmpty());
    assertTrue(beforeLog.stream().anyMatch(s -> s.contains("LifecycleTestBean")));
    
    // 验证 after 回调
    List<String> afterLog = TestBeanPostProcessor.getAfterLog();
    assertFalse(afterLog.isEmpty());
    assertTrue(afterLog.stream().anyMatch(s -> s.contains("LifecycleTestBean")));
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testBeanPostProcessor
```

**预期结果**: ✅ 通过

---

### 5. testFullLifecycleOrder

**测试目标**: 验证完整的 Bean 生命周期回调顺序

**测试代码**:

```java
@Test
public void testFullLifecycleOrder() {
    LifecycleTestBean.clearLifecycleLog();
    TestBeanPostProcessor.clearLogs();
    
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultApplicationContext context = new DefaultApplicationContext(packages, new DefaultEnvironment());
    
    context.addBeanPostProcessor(new TestBeanPostProcessor());
    context.refresh();
    
    // 初始化阶段
    List<String> initLog = LifecycleTestBean.getLifecycleLog();
    assertTrue(initLog.contains("@PostConstruct"));
    assertTrue(initLog.contains("InitializingBean.afterPropertiesSet"));
    
    context.close();
    
    // 销毁阶段
    List<String> destroyLog = LifecycleTestBean.getLifecycleLog();
    assertTrue(destroyLog.contains("@PreDestroy"));
    assertTrue(destroyLog.contains("DisposableBean.destroy"));
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testFullLifecycleOrder
```

**预期结果**: ✅ 通过

---

### 6. testGetBeanPostProcessors

**测试目标**: 验证可以获取已注册的 `BeanPostProcessor` 列表

**测试代码**:

```java
@Test
public void testGetBeanPostProcessors() {
    Set<String> packages = new HashSet<>();
    packages.add("org.nanoboot.core.lifecycle");
    DefaultApplicationContext context = new DefaultApplicationContext(packages, new DefaultEnvironment());
    
    TestBeanPostProcessor processor = new TestBeanPostProcessor();
    context.addBeanPostProcessor(processor);
    
    List<BeanPostProcessor> processors = context.getBeanPostProcessors();
    assertEquals(1, processors.size());
    assertTrue(processors.get(0) instanceof TestBeanPostProcessor);
}
```

**调用方式**:

```bash
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest#testGetBeanPostProcessors
```

**预期结果**: ✅ 通过

---

## 运行所有测试

### 完整命令

```bash
cd D:\dai\NanoBoot
mvn test -pl nano-boot-core -Dtest=LifecycleCallbackTest
```

### 测试结果

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running org.nanoboot.core.lifecycle.LifecycleCallbackTest
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

Results :
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

---

## 生命周期回调顺序总结

### 初始化阶段

| 顺序 | 回调方式 | 说明 |
|------|---------|------|
| 1 | `BeanPostProcessor.postProcessBeforeInitialization()` | Bean 初始化前处理 |
| 2 | `InitializingBean.afterPropertiesSet()` | Bean 属性设置完成后调用 |
| 3 | `@PostConstruct` 注解方法 | 自定义初始化方法 |
| 4 | `BeanPostProcessor.postProcessAfterInitialization()` | Bean 初始化后处理 |

### 销毁阶段

| 顺序 | 回调方式 | 说明 |
|------|---------|------|
| 1 | `@PreDestroy` 注解方法 | 自定义销毁方法 |
| 2 | `DisposableBean.destroy()` | Bean 销毁时调用 |

---

## 测试 Bean 示例

### LifecycleTestBean (完整生命周期)

```java
@Component
public class LifecycleTestBean implements InitializingBean, DisposableBean {

    @PostConstruct
    public void postConstruct() {
        lifecycleLog.add("@PostConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        lifecycleLog.add("@PreDestroy");
    }

    @Override
    public void afterPropertiesSet() {
        lifecycleLog.add("InitializingBean.afterPropertiesSet");
    }

    @Override
    public void destroy() {
        lifecycleLog.add("DisposableBean.destroy");
    }
}
```

### SimpleAnnotationBean (仅注解方式)

```java
@Component
public class SimpleAnnotationBean {

    @PostConstruct
    public void init() {
        lifecycleLog.add("init() called");
    }

    @PreDestroy
    public void cleanup() {
        lifecycleLog.add("cleanup() called");
    }
}
```

### InitializingBeanOnly (仅接口方式)

```java
@Component
public class InitializingBeanOnly implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        lifecycleLog.add("afterPropertiesSet() called");
    }
}
```

### DisposableBeanOnly (仅接口方式)

```java
@Component
public class DisposableBeanOnly implements DisposableBean {

    @Override
    public void destroy() {
        lifecycleLog.add("destroy() called");
    }
}
```

### TestBeanPostProcessor (自定义处理器)

```java
public class TestBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        beforeLog.add(beanName + ":" + bean.getClass().getSimpleName());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        afterLog.add(beanName + ":" + bean.getClass().getSimpleName());
        return bean;
    }
}
```
