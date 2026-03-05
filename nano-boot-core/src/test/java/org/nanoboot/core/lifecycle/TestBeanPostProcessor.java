package org.nanoboot.core.lifecycle;

import org.nanoboot.core.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试用的 BeanPostProcessor
 * 记录所有 Bean 的初始化前后处理
 */
public class TestBeanPostProcessor implements BeanPostProcessor {

    private static final List<String> beforeLog = new ArrayList<>();
    private static final List<String> afterLog = new ArrayList<>();

    public static List<String> getBeforeLog() {
        return new ArrayList<>(beforeLog);
    }

    public static List<String> getAfterLog() {
        return new ArrayList<>(afterLog);
    }

    public static void clearLogs() {
        beforeLog.clear();
        afterLog.clear();
    }

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
