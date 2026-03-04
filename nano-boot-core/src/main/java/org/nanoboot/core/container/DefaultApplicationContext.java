package org.nanoboot.core.container;

import org.nanoboot.annotation.Annotation.Autowired;
import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Configuration;
import org.nanoboot.annotation.Annotation.Value;
import org.nanoboot.core.ApplicationContext;
import org.nanoboot.core.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的应用上下文实现，提供IOC容器功能
 */
public class DefaultApplicationContext implements ApplicationContext {

    // 存储Bean定义的注册表
    private final Map<String, BeanDefinition> beanDefinitionRegistry = new ConcurrentHashMap<>();

    // 存储单例Bean实例的缓存
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();

    // 已创建的单例Bean名称集合
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // 包扫描路径
    private final Set<String> basePackages = new HashSet<>();

    // 环境配置
    private Environment environment;

    // 是否已经刷新过
    private boolean refreshed = false;

    public DefaultApplicationContext(Set<String> basePackages, Environment environment) {
        this.basePackages.addAll(basePackages);
        this.environment = environment;
    }

    @Override
    public synchronized void refresh() {
        if (refreshed) {
            throw new IllegalStateException("ApplicationContext is already refreshed");
        }

        // 扫描并注册Bean定义
        scanAndRegisterBeans();

        // 预实例化单例Bean
        preInstantiateSingletons();

        refreshed = true;
    }

    @Override
    public void close() {
        // 清理资源
        singletonObjects.clear();
        beanDefinitionRegistry.clear();
        basePackages.clear();
    }

    @Override
    public <T> T getBean(Class<T> type) {
        String[] candidates = getBeanNamesForType(type);
        if (candidates.length == 0) {
            throw new NoSuchBeanDefinitionException("No bean of type " + type.getName() + " found");
        } else if (candidates.length > 1) {
            // 如果有多个候选者，查找默认名称对应的Bean
            for (String candidate : candidates) {
                if (candidate.equals(type.getSimpleName().toLowerCase())) {
                    return getBean(candidate, type);
                }
            }
            throw new NoUniqueBeanDefinitionException("Multiple beans of type " + type.getName() + " found: " + Arrays.toString(candidates));
        }
        return getBean(candidates[0], type);
    }

    @Override
    public <T> T getBean(String name, Class<T> type) {
        Object bean = getBean(name);
        if (!type.isInstance(bean)) {
            throw new BeanNotOfRequiredTypeException("Bean named '" + name + "' is not of type " + type.getName());
        }
        return type.cast(bean);
    }

    @Override
    public <T> T getBean(String name) {
        if (!singletonObjects.containsKey(name)) {
            BeanDefinition bd = beanDefinitionRegistry.get(name);
            if (bd == null) {
                throw new NoSuchBeanDefinitionException("No bean named '" + name + "' available");
            }

            return (T) createBean(bd);
        }
        return (T) singletonObjects.get(name);
    }

    @Override
    public boolean containsBean(String name) {
        return beanDefinitionRegistry.containsKey(name) || singletonObjects.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        BeanDefinition bd = beanDefinitionRegistry.get(name);
        return bd != null && bd.isSingleton();
    }

    @Override
    public Class<?> getType(String name) {
        Object bean = singletonObjects.get(name);
        if (bean != null) {
            return bean.getClass();
        }
        BeanDefinition bd = beanDefinitionRegistry.get(name);
        return bd != null ? bd.getBeanClass() : null;
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionRegistry.keySet().toArray(new String[0]);
    }

    @Override
    public int getBeanDefinitionCount() {
        return beanDefinitionRegistry.size();
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * 扫描包并注册Bean定义
     */
    private void scanAndRegisterBeans() {
        PackageScanner scanner = new PackageScanner();
        for (String basePackage : basePackages) {
            Set<Class<?>> classes = scanner.scan(basePackage);
            for (Class<?> clazz : classes) {
                registerBeanDefinition(clazz);
            }
        }
    }

    /**
     * 注册Bean定义
     */
    private void registerBeanDefinition(Class<?> clazz) {
        String beanName = generateBeanName(clazz);

        if (beanDefinitionRegistry.containsKey(beanName)) {
            throw new BeanDefinitionOverrideException("Bean definition for '" + beanName + "' already registered");
        }

        BeanDefinition bd = new BeanDefinition(clazz, true); // 默认单例
        beanDefinitionRegistry.put(beanName, bd);
    }

    /**
     * 生成Bean名称
     */
    private String generateBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null && !component.value().isEmpty()) {
            return component.value();
        }

        Service service = clazz.getAnnotation(Service.class);
        if (service != null && !service.value().isEmpty()) {
            return service.value();
        }

        Configuration config = clazz.getAnnotation(Configuration.class);
        if (config != null && !config.value().isEmpty()) {
            return config.value();
        }

        // 默认使用类名首字母小写
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    /**
     * 创建Bean实例
     */
    private Object createBean(BeanDefinition bd) {
        String beanName = findBeanNameByType(bd.getBeanClass());

        // 避免循环依赖
        if (singletonsCurrentlyInCreation.contains(beanName)) {
            throw new BeanCurrentlyInCreationException("Circular reference detected for bean '" + beanName + "'");
        }

        singletonsCurrentlyInCreation.add(beanName);

        try {
            Object bean = instantiateBean(bd);
            populateBean(bean, bd);
            initializeBean(bean, bd);

            // 将单例Bean放入缓存
            if (bd.isSingleton()) {
                singletonObjects.put(beanName, bean);
            }

            return bean;
        } finally {
            singletonsCurrentlyInCreation.remove(beanName);
        }
    }

    /**
     * 实例化Bean
     */
    private Object instantiateBean(BeanDefinition bd) {
        try {
            return bd.getBeanClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException("Failed to instantiate bean of type " + bd.getBeanClass().getName(), e);
        }
    }

    /**
     * 填充Bean属性
     */
    private void populateBean(Object bean, BeanDefinition bd) {
        Class<?> clazz = bd.getBeanClass();

        // 处理@Autowired注解的字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);

                try {
                    Object dependency = resolveDependency(field);
                    if (dependency != null) {
                        field.set(bean, dependency);
                    }
                    // 如果依赖为null且字段非必需，则不注入
                } catch (IllegalAccessException e) {
                    throw new BeanCreationException("Failed to inject dependency into field: " + field.getName(), e);
                }
            }

            // 处理@Value注解的字段
            if (field.isAnnotationPresent(Value.class)) {
                field.setAccessible(true);
                Value value = field.getAnnotation(Value.class);

                try {
                    String propertyValue = environment.getProperty(value.value());
                    Object convertedValue = convertValue(propertyValue, field.getType());
                    field.set(bean, convertedValue);
                } catch (IllegalAccessException e) {
                    throw new BeanCreationException("Failed to set property value for field: " + field.getName(), e);
                }
            }
        }
    }

    /**
     * 初始化Bean
     */
    private void initializeBean(Object bean, BeanDefinition bd) {
        Class<?> clazz = bd.getBeanClass();

        // 查找并执行@PostConstruct注解的方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(org.nanoboot.annotation.Annotation.PostConstruct.class)) {
                method.setAccessible(true);
                try {
                    method.invoke(bean);
                } catch (Exception e) {
                    throw new BeanInitializationException("Failed to execute @PostConstruct method: " + method.getName(), e);
                }
            }
        }
    }

    /**
     * 解析依赖
     */
    private Object resolveDependency(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName();

        // 检查字段上的@Autowire注解
        Autowired autowired = field.getAnnotation(Autowired.class);
        boolean required = autowired != null ? autowired.required() : true;

        String[] candidates = getBeanNamesForType(type);
        if (candidates.length == 0) {
            if (required) {
                throw new NoSuchBeanDefinitionException("No qualifying bean of type " + type.getName() + " found for dependency '" + fieldName + "'");
            } else {
                return null; // 如果非必需，则返回null
            }
        } else if (candidates.length == 1) {
            return getBean(candidates[0], type);
        } else {
            // 多个候选者，按名称匹配
            for (String candidate : candidates) {
                if (candidate.equals(fieldName)) {
                    return getBean(candidate, type);
                }
            }
            // 如果仍找不到精确匹配
            if (required) {
                throw new NoUniqueBeanDefinitionException("Multiple beans of type " + type.getName() + " found for dependency '" + fieldName + "', no exact name match");
            } else {
                return null; // 如果非必需，则返回null
            }
        }
    }

    /**
     * 根据类型获取Bean名称
     */
    private String[] getBeanNamesForType(Class<?> type) {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionRegistry.entrySet()) {
            if (type.isAssignableFrom(entry.getValue().getBeanClass())) {
                names.add(entry.getKey());
            }
        }
        return names.toArray(new String[0]);
    }

    /**
     * 根据类型查找Bean名称
     */
    private String findBeanNameByType(Class<?> type) {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionRegistry.entrySet()) {
            if (entry.getValue().getBeanClass() == type) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 转换值到指定类型
     */
    private Object convertValue(String value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(value);
        } else {
            // 对于其他类型，返回原始字符串或尝试转换
            return value;
        }
    }

    /**
     * 预实例化单例Bean
     */
    private void preInstantiateSingletons() {
        for (String beanName : beanDefinitionRegistry.keySet()) {
            BeanDefinition bd = beanDefinitionRegistry.get(beanName);
            if (bd.isSingleton()) {
                getBean(beanName);
            }
        }
    }

    // 自定义异常类
    public static class BeanDefinitionOverrideException extends RuntimeException {
        public BeanDefinitionOverrideException(String msg) { super(msg); }
    }

    public static class BeanCurrentlyInCreationException extends RuntimeException {
        public BeanCurrentlyInCreationException(String msg) { super(msg); }
    }

    public static class BeanInstantiationException extends RuntimeException {
        public BeanInstantiationException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class BeanCreationException extends RuntimeException {
        public BeanCreationException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class BeanInitializationException extends RuntimeException {
        public BeanInitializationException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static class NoSuchBeanDefinitionException extends RuntimeException {
        public NoSuchBeanDefinitionException(String msg) { super(msg); }
    }

    public static class NoUniqueBeanDefinitionException extends RuntimeException {
        public NoUniqueBeanDefinitionException(String msg) { super(msg); }
    }

    public static class BeanNotOfRequiredTypeException extends RuntimeException {
        public BeanNotOfRequiredTypeException(String msg) { super(msg); }
    }
}