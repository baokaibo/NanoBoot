package org.nanoboot.core;

/**
 * Environment configuration interface
 */
public interface Environment {

    /**
     * Get property value
     */
    String getProperty(String key);

    /**
     * Get property value with default value
     */
    String getProperty(String key, String defaultValue);

    /**
     * Get property value converted to specified type
     */
    <T> T getProperty(String key, Class<T> targetType);

    /**
     * Get property value converted to specified type with default value
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    /**
     * Check if contains specified property
     */
    boolean containsProperty(String key);
}