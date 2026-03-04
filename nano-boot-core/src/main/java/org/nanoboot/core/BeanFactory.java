package org.nanoboot.core;

import java.util.Map;

/**
 * Bean factory interface defining basic Bean operations
 */
public interface BeanFactory {

    /**
     * Get Bean instance by type
     */
    <T> T getBean(Class<T> type);

    /**
     * Get Bean instance by name
     */
    <T> T getBean(String name);

    /**
     * Get Bean instance by name and type
     */
    <T> T getBean(String name, Class<T> type);

    /**
     * Check if contains Bean with specified name
     */
    boolean containsBean(String name);

    /**
     * Check if Bean with specified name is singleton
     */
    boolean isSingleton(String name);

    /**
     * Get Bean type
     */
    Class<?> getType(String name);
}