package org.nanoboot.core.scope;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Scope;

/**
 * Prototype scope test bean - each getBean creates a new instance
 */
@Component
@Scope("prototype")
public class PrototypeBean {
    private static int instanceCount = 0;
    private final int instanceId;

    public PrototypeBean() {
        instanceCount++;
        this.instanceId = instanceCount;
        System.out.println("PrototypeBean created, instance #" + instanceId);
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
