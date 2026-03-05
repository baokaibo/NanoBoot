package org.nanoboot.core.lifecycle;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.PostConstruct;
import org.nanoboot.annotation.Annotation.PreDestroy;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的测试Bean，只使用注解方式
 */
@Component
public class SimpleAnnotationBean {

    private static final List<String> lifecycleLog = new ArrayList<>();

    public static List<String> getLifecycleLog() {
        return new ArrayList<>(lifecycleLog);
    }

    public static void clearLifecycleLog() {
        lifecycleLog.clear();
    }

    @PostConstruct
    public void init() {
        lifecycleLog.add("init() called");
    }

    @PreDestroy
    public void cleanup() {
        lifecycleLog.add("cleanup() called");
    }
}
