package org.nanoboot.core.scope;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nanoboot.core.container.DefaultApplicationContext;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Bean Scope Test
 * Tests singleton and prototype scopes
 */
public class ScopeTest {

    private DefaultApplicationContext context;

    @Before
    public void setUp() {
        // Reset instance counts
        SingletonBean.resetInstanceCount();
        PrototypeBean.resetInstanceCount();
        
        Set<String> basePackages = new HashSet<>();
        basePackages.add("org.nanoboot.core.scope");
        
        context = new DefaultApplicationContext(basePackages, null);
        context.refresh(); // 刷新上下文以注册和实例化Bean
    }

    @After
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    public void testSingletonScope() {
        // Get singleton bean twice - using Class
        SingletonBean singleton1 = context.getBean(SingletonBean.class);
        SingletonBean singleton2 = context.getBean(SingletonBean.class);
        
        // Should be the same instance
        assertSame("Singleton should return the same instance", singleton1, singleton2);
        assertEquals("Instance ID should be the same", singleton1.getInstanceId(), singleton2.getInstanceId());
        
        // Only one instance should be created
        assertEquals("Only one instance should be created", 1, SingletonBean.getInstanceCount());
    }

    @Test
    public void testPrototypeScope() {
        // Get prototype bean multiple times - using Class
        PrototypeBean prototype1 = context.getBean(PrototypeBean.class);
        PrototypeBean prototype2 = context.getBean(PrototypeBean.class);
        PrototypeBean prototype3 = context.getBean(PrototypeBean.class);
        
        // Each should be a different instance
        assertNotSame("Prototype should create new instances", prototype1, prototype2);
        assertNotSame("Prototype should create new instances", prototype2, prototype3);
        
        // Instance IDs should be different
        assertNotEquals("Each prototype should have unique instance ID", 
            prototype1.getInstanceId(), prototype2.getInstanceId());
        
        // Three instances should be created
        assertEquals("Three instances should be created", 3, PrototypeBean.getInstanceCount());
    }

    @Test
    public void testSingletonAndPrototypeTogether() {
        // Get both types of beans - using Class
        SingletonBean singleton = context.getBean(SingletonBean.class);
        PrototypeBean prototype1 = context.getBean(PrototypeBean.class);
        PrototypeBean prototype2 = context.getBean(PrototypeBean.class);
        
        // Verify singleton behavior
        SingletonBean singleton2 = context.getBean(SingletonBean.class);
        assertSame("Singleton should be same", singleton, singleton2);
        
        // Verify prototype behavior
        assertNotSame("Prototypes should be different", prototype1, prototype2);
        
        // Verify counts
        assertEquals("Singleton count should be 1", 1, SingletonBean.getInstanceCount());
        assertEquals("Prototype count should be 2", 2, PrototypeBean.getInstanceCount());
    }
}
