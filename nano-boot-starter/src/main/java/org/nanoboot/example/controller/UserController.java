package org.nanoboot.example.controller;

import org.nanoboot.annotation.Annotation.Autowired;
import org.nanoboot.annotation.Annotation.Controller;
import org.nanoboot.annotation.Annotation.GetMapping;
import org.nanoboot.annotation.Annotation.RequestParam;
import org.nanoboot.example.service.UserService;
import org.nanoboot.example.service.DataService;

@Controller("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataService dataService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from NanoBoot! App: " + userService.getAppName();
    }

    @GetMapping("/user")
    public String getUser(@RequestParam("id") Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/data")
    public String getData(@RequestParam("key") String key) {
        return dataService.getData(key);
    }
}