package org.nanoboot.example;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.starter.NanoBootApplicationRunner;

@NanoBootApplication
public class SimpleNanoBootExampleApp {

    public static void main(String[] args) {
        System.out.println("Starting Simple NanoBoot Example Application...");
        NanoBootApplicationRunner.run(SimpleNanoBootExampleApp.class, args);
    }
}