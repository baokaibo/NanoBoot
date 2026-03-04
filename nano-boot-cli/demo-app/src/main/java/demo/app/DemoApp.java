package demo.app;

import org.nanoboot.annotation.Annotation.NanoBootApplication;
import org.nanoboot.starter.NanoBootApplicationRunner;

@NanoBootApplication
public class DemoApp {

    public static void main(String[] args) {
        System.out.println("Starting NanoBoot application...");
        NanoBootApplicationRunner.run(DemoApp.class, args);
    }
}
