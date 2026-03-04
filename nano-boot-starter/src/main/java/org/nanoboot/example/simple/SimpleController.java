package org.nanoboot.example.simple;

import org.nanoboot.annotation.Annotation.Controller;
import org.nanoboot.annotation.Annotation.GetMapping;
import org.nanoboot.example.service.UserService;

@Controller("/simple")
public class SimpleController {

    private final UserService userService;

    public SimpleController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Simple Hello from NanoBoot! App: " + userService.getAppName();
    }
}