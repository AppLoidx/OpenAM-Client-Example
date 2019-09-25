package com.apploidxxx.openam.example;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arthur Kupriyanov
 */
@RestController
@RequestMapping(value = "/test")
public class TestController {
    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        UserDetails currentUser
                = (UserDetails) authentication.getDetails();
        return "Hello , Oneee - chan! [" + currentUser.getUsername() + "]!";
    }

    @GetMapping("/home")
    public String home() {
        return "my Personal Home Page";
    }

}