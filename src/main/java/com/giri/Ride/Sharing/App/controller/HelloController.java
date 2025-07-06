package com.giri.Ride.Sharing.App.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HelloController {

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @GetMapping("hello")
    public String greet() {
        return "Hello there ";

    }

    @GetMapping("about")
    public String about(HttpServletRequest request) {
        return "Ride Sharing Application "+request.getSession().getId();
    }
}