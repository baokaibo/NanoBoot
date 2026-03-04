package org.nanoboot.core.config;

import org.nanoboot.core.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 默认环境配置实现
 */
public class DefaultEnvironment implements Environment {

    private final Properties properties = new Properties();

    public DefaultEnvironment() {
        // 加载默认配置
        loadDefaultProperties();
    }

    public DefaultEnvironment(Properties properties) {
        this.properties.putAll(properties);
        loadDefaultProperties();
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        return getProperty(key, targetType, null);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }

        if (targetType == String.class) {
            return targetType.cast(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return targetType.cast(Integer.valueOf(value));
        } else if (targetType == Long.class || targetType == long.class) {
            return targetType.cast(Long.valueOf(value));
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return targetType.cast(Boolean.valueOf(value));
        } else if (targetType == Double.class || targetType == double.class) {
            return targetType.cast(Double.valueOf(value));
        } else if (targetType == Float.class || targetType == float.class) {
            return targetType.cast(Float.valueOf(value));
        } else {
            // 对于其他类型，尝试直接转换或返回原值
            return targetType.cast(value);
        }
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    /**
     * 设置属性值
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 添加所有属性
     */
    public void addAllProperties(Properties props) {
        this.properties.putAll(props);
    }

    /**
     * 加载默认配置
     */
    private void loadDefaultProperties() {
        // 可以从application.properties等文件加载默认配置
        loadPropertiesFromFile("application.properties");
    }

    /**
     * 从文件加载属性
     */
    private void loadPropertiesFromFile(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            // 忽略配置文件不存在的情况
        }
    }
}