package org.nanoboot.core;

/**
 * Factory hook that allows for custom modification of new bean instances.
 * Provides callbacks for post-processing before and after bean initialization.
 *
 * Similar to Spring's BeanPostProcessor interface.
 */
public interface BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor to the given new bean instance before any
     * bean initialization callbacks (like InitializingBean's afterPropertiesSet
     * or a custom init-method).
     *
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    /**
     * Apply this BeanPostProcessor to the given new bean instance after any
     * bean initialization callbacks (like InitializingBean's afterPropertiesSet
     * or a custom init-method).
     *
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
