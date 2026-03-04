package org.nanoboot.core.container;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean定义类，存储Bean的元数据信息
 */
public class BeanDefinition {

    private final Class<?> beanClass;
    private final boolean singleton;
    private final String scope;
    private final Map<String, Object> properties = new HashMap<>();

    public BeanDefinition(Class<?> beanClass, boolean singleton) {
        this.beanClass = beanClass;
        this.singleton = singleton;
        this.scope = singleton ? "singleton" : "prototype";
    }

    public BeanDefinition(Class<?> beanClass, String scope) {
        this.beanClass = beanClass;
        this.scope = scope;
        this.singleton = "singleton".equals(scope);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public String getScope() {
        return scope;
    }

    public void setProperty(String name, Object value) {
        properties.put(name, value);
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
}