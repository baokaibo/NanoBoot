package org.nanoboot.core.container;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Controller;
import org.nanoboot.annotation.Annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * 包扫描器，负责扫描指定包下的所有类
 */
public class PackageScanner {

    /**
     * 扫描指定包路径下的所有类
     */
    public Set<Class<?>> scan(String packageName) {
        Set<Class<?>> classes = new HashSet<>();

        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    scanDirectory(directory, packageName, classes);
                } else {
                    // 处理jar包中的类
                    scanJar(packageName, classes);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan package: " + packageName, e);
        }

        return classes;
    }

    /**
     * 扫描目录中的类文件
     */
    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                addClassIfAnnotated(className, classes);
            }
        }
    }

    /**
     * 扫描jar包中的类（简化版本，仅作为示例）
     */
    private void scanJar(String packageName, Set<Class<?>> classes) {
        // 在实际应用中，这里需要使用JarFile来遍历jar内容
        // 为简化实现，我们暂时跳过jar包扫描
    }

    /**
     * 加载类并检查是否有相关注解
     */
    private void addClassIfAnnotated(String className, Set<Class<?>> classes) {
        try {
            Class<?> clazz = Class.forName(className);

            // 检查是否是被注解的类
            if (clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(Configuration.class)) {
                classes.add(clazz);
            }
        } catch (ClassNotFoundException e) {
            // 忽略无法加载的类
        } catch (NoClassDefFoundError e) {
            // 忽略依赖缺失导致的错误
        }
    }
}