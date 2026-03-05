package org.nanoboot.core;

/**
 * Interface to be implemented by beans that need to perform initialization work
 * after the container has set all necessary bean properties.
 *
 * Similar to Spring's InitializingBean interface.
 */
public interface InitializingBean {

    /**
     * Invoked by the container after property setting is complete.
     * This method allows the bean to perform validation and final initialization.
     *
     * @throws Exception if initialization fails
     */
    void afterPropertiesSet() throws Exception;
}
