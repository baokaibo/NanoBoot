package org.nanoboot.core;

/**
 * Interface to be implemented by beans that need to release resources
 * before the container destroys the bean.
 *
 * Similar to Spring's DisposableBean interface.
 */
public interface DisposableBean {

    /**
     * Invoked by the container before destroying the bean.
     * This method allows the bean to perform cleanup operations.
     *
     * @throws Exception if destruction fails
     */
    void destroy() throws Exception;
}
