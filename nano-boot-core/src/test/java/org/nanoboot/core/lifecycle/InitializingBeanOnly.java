package org.nanoboot.core.lifecycle;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.core.InitializingBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Bean，只实现 InitializingBean 接口
 */
@Component
public class InitializingBeanOnly implements InitializingBean {

    private static final List<String> lifecycleLog = new ArrayList<>();

    public static List<String> getLifecycleLog() {
        return new ArrayList<>(lifecycleLog);
    }

    public static void clearLifecycleLog() {
        lifecycleLog.clear();
    }

    @Override
    public void afterPropertiesSet() {
        lifecycleLog.add("afterPropertiesSet() called");
    }
}
