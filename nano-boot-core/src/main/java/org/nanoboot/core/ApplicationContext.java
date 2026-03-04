package org.nanoboot.core;

/**
 * Application context interface extending BeanFactory with additional functionality
 */
public interface ApplicationContext extends BeanFactory {

    /**
     * Refresh application context, reload all Beans
     */
    void refresh();

    /**
     * Close application context, release resources
     */
    void close();

    /**
     * Get all Bean definition names
     */
    String[] getBeanDefinitionNames();

    /**
     * Get Bean definition count
     */
    int getBeanDefinitionCount();

    /**
     * Get environment configuration
     */
    Environment getEnvironment();
}