package org.nanoboot.core.scope;

import org.nanoboot.annotation.Annotation.Component;

/**
 * Singleton scope test bean
 */
@Component
public class SingletonBean {
    private static int instanceCount = 0;
    private final int instanceId;

    public SingletonBean() {
        instanceCount++;
        this.instanceId = instanceCount;
        System.out.println("SingletonBean created, instance #" + instanceId);
    }

    public int getInstanceId() {
        return instanceId;
    }

    public static int getInstanceCount() {
        return instanceCount;
    }

    public static void resetInstanceCount() {
        instanceCount = 0;
    }
}
