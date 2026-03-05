package org.nanoboot.core.lifecycle;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.PostConstruct;
import org.nanoboot.annotation.Annotation.PreDestroy;
import org.nanoboot.core.DisposableBean;
import org.nanoboot.core.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Bean，用于验证生命周期回调
 * 实现了所有三种初始化和销毁方式：
 * 1. @PostConstruct / @PreDestroy 注解
 * 2. InitializingBean / DisposableBean 接口
 */
@Component
public class LifecycleTestBean implements InitializingBean, DisposableBean {

    private static final List<String> lifecycleLog = new ArrayList<>();

    public static List<String> getLifecycleLog() {
        return new ArrayList<>(lifecycleLog);
    }

    public static void clearLifecycleLog() {
        lifecycleLog.clear();
    }

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
