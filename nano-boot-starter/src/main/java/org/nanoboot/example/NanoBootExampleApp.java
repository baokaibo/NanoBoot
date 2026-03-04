package org.nanoboot.example;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.starter.NanoBootApplicationRunner;

@NanoBootApplication
public class NanoBootExampleApp {

    public static void main(String[] args) {
        System.out.println("Starting NanoBoot Example Application...");
        NanoBootApplicationRunner.run(NanoBootExampleApp.class, args);
    }
}