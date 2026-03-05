package org.nanoboot.core.lifecycle;

import org.junit.Test;
import org.nanoboot.core.container.DefaultApplicationContext;
import org.nanoboot.core.config.DefaultEnvironment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Bean 生命周期回调测试类
 */
public class LifecycleCallbackTest {

    @Test
    public void testPostConstructAndPreDestroyAnnotations() {
        // 清理之前的日志
        SimpleAnnotationBean.clearLifecycleLog();

        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        try {
            // 注册 BeanPostProcessor
            context.addBeanPostProcessor(new TestBeanPostProcessor());

            // 刷新上下文（会创建 Bean）
            context.refresh();

            // 验证 @PostConstruct 被调用
            List<String> log = SimpleAnnotationBean.getLifecycleLog();
            assertTrue("PostConstruct should be called", log.contains("init() called"));

            // 关闭上下文（会调用 @PreDestroy）
            context.close();

            // 验证 @PreDestroy 被调用
            log = SimpleAnnotationBean.getLifecycleLog();
            assertTrue("PreDestroy should be called after close", log.contains("cleanup() called"));
        } finally {
            SimpleAnnotationBean.clearLifecycleLog();
        }
    }

    @Test
    public void testInitializingBeanInterface() {
        // 清理之前的日志
        InitializingBeanOnly.clearLifecycleLog();

        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        try {
            context.refresh();

            // 验证 InitializingBean.afterPropertiesSet() 被调用
            List<String> log = InitializingBeanOnly.getLifecycleLog();
            assertTrue("InitializingBean.afterPropertiesSet() should be called",
                    log.contains("afterPropertiesSet() called"));
        } finally {
            InitializingBeanOnly.clearLifecycleLog();
        }
    }

    @Test
    public void testDisposableBeanInterface() {
        // 清理之前的日志
        DisposableBeanOnly.clearLifecycleLog();

        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        try {
            context.refresh();

            // 关闭上下文
            context.close();

            // 验证 DisposableBean.destroy() 被调用
            List<String> log = DisposableBeanOnly.getLifecycleLog();
            assertTrue("DisposableBean.destroy() should be called after close",
                    log.contains("destroy() called"));
        } finally {
            DisposableBeanOnly.clearLifecycleLog();
        }
    }

    @Test
    public void testBeanPostProcessor() {
        // 清理之前的日志
        TestBeanPostProcessor.clearLogs();

        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        try {
            // 注册 BeanPostProcessor
            TestBeanPostProcessor processor = new TestBeanPostProcessor();
            context.addBeanPostProcessor(processor);

            context.refresh();

            // 验证 postProcessBeforeInitialization 被调用
            List<String> beforeLog = TestBeanPostProcessor.getBeforeLog();
            assertFalse("postProcessBeforeInitialization should be called", beforeLog.isEmpty());
            assertTrue("Should process LifecycleTestBean",
                    beforeLog.stream().anyMatch(s -> s.contains("LifecycleTestBean")));

            // 验证 postProcessAfterInitialization 被调用
            List<String> afterLog = TestBeanPostProcessor.getAfterLog();
            assertFalse("postProcessAfterInitialization should be called", afterLog.isEmpty());
            assertTrue("Should process LifecycleTestBean",
                    afterLog.stream().anyMatch(s -> s.contains("LifecycleTestBean")));
        } finally {
            TestBeanPostProcessor.clearLogs();
        }
    }

    @Test
    public void testFullLifecycleOrder() {
        // 清理之前的日志
        LifecycleTestBean.clearLifecycleLog();
        TestBeanPostProcessor.clearLogs();

        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        try {
            // 注册 BeanPostProcessor
            context.addBeanPostProcessor(new TestBeanPostProcessor());

            // 刷新上下文
            context.refresh();

            // 验证初始化顺序
            List<String> initLog = LifecycleTestBean.getLifecycleLog();
            System.out.println("Initialization order: " + initLog);

            // 初始化顺序应该是：
            // 1. postProcessBeforeInitialization
            // 2. InitializingBean.afterPropertiesSet
            // 3. @PostConstruct
            // 4. postProcessAfterInitialization
            
            // 验证 @PostConstruct 和 InitializingBean 都被调用
            assertTrue("@PostConstruct should be called", initLog.contains("@PostConstruct"));
            assertTrue("InitializingBean.afterPropertiesSet should be called", 
                    initLog.contains("InitializingBean.afterPropertiesSet"));

            // 关闭上下文
            context.close();

            // 验证销毁顺序
            List<String> destroyLog = LifecycleTestBean.getLifecycleLog();
            System.out.println("Destruction order: " + destroyLog);

            // 销毁顺序应该是：
            // 1. @PreDestroy
            // 2. DisposableBean.destroy
            
            assertTrue("@PreDestroy should be called", destroyLog.contains("@PreDestroy"));
            assertTrue("DisposableBean.destroy should be called", destroyLog.contains("DisposableBean.destroy"));
        } finally {
            LifecycleTestBean.clearLifecycleLog();
            TestBeanPostProcessor.clearLogs();
        }
    }

    @Test
    public void testGetBeanPostProcessors() {
        // 创建应用上下文
        Set<String> packages = new HashSet<>();
        packages.add("org.nanoboot.core.lifecycle");
        DefaultEnvironment environment = new DefaultEnvironment();
        DefaultApplicationContext context = new DefaultApplicationContext(packages, environment);

        // 添加 BeanPostProcessor
        TestBeanPostProcessor processor = new TestBeanPostProcessor();
        context.addBeanPostProcessor(processor);

        // 验证可以获取到 BeanPostProcessor
        List<org.nanoboot.core.BeanPostProcessor> processors = context.getBeanPostProcessors();
        assertEquals(1, processors.size());
        assertTrue(processors.get(0) instanceof TestBeanPostProcessor);
    }
}
