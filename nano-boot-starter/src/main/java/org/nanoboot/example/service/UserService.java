package org.nanoboot.example.service;

import org.nanoboot.annotation.Annotation.Service;
import org.nanoboot.annotation.Annotation.Value;

@Service
public class UserService {

    @Value("${app.name:DefaultApp}")
    private String appName;

    public String getAppName() {
        return appName;
    }

    public String getUserById(Long id) {
        return "User-" + id + " from " + appName;
    }

    public String createUser(String name) {
        return "Created user: " + name;
    }
}