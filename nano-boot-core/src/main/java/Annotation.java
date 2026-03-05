package org.nanoboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Core annotations for the framework
 */
public class Annotation {

    /**
     * Marks a class as a component managed by the container
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Component {
        String value() default "";
    }

    /**
     * Marks a class as a service layer component
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Service {
        String value() default "";
    }

    /**
     * Marks a class as a controller for handling HTTP requests
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Controller {
        String value() default "";
    }

    /**
     * Marks a class as a configuration class
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Configuration {
        String value() default "";
    }

    /**
     * Annotation for dependency injection
     */
    @Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Autowired {
        boolean required() default true;
    }

    /**
     * Marks a method to be called after initialization
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PostConstruct {
    }

    /**
     * Marks a method to be called before destruction
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PreDestroy {
    }

    /**
     * Annotation for injecting property values
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Value {
        String value();
    }

    /**
     * Marks a class as a configuration properties holder
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConfigurationProperties {
        String prefix();
    }

    /**
     * Marks a class as a boot application
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NanoBootApplication {
    }

    /**
     * GET request mapping
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GetMapping {
        String value() default "";
    }

    /**
     * POST request mapping
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PostMapping {
        String value() default "";
    }

    /**
     * Generic request mapping annotation
     * Can be used on classes to define base path, or on methods to define specific routes
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequestMapping {
        String value() default "";
        RequestMethod[] method() default {};
    }

    /**
     * HTTP request method enum
     */
    public enum RequestMethod {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE
    }

    /**
     * Path variable annotation
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PathVariable {
        String value() default "";
    }

    /**
     * Request parameter annotation
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequestParam {
        String value() default "";
        boolean required() default true;
        String defaultValue() default "";
    }

    /**
     * Request body annotation
     */
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequestBody {
    }
}