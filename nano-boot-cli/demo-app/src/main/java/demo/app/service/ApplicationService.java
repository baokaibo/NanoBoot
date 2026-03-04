package demo.app.service;

import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Value;

@Service
public class ApplicationService {

    @Value("${app.name:DefaultApp}")
    private String appName;

    public String getAppInfo() {
        return "Application: " + appName + " running on NanoBoot";
    }
    
    public String greet(String name) {
        return "Hello " + name + "! Welcome to " + appName + ".";
    }
}
