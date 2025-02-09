package com.giri.Ride.Sharing.App.controller;

import com.giri.Ride.Sharing.App.model.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
//@CrossOrigin(origins = "http://localhost:3000")
public class UsersController {
    List<Users> users = new ArrayList<>(List.of(
            new Users(1, "Giri", "Student"),
            new Users(2, "Venkat", "Student")
    ));

    @GetMapping("csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request){
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("users")
    public List<Users> getUsers(){
        return users;
    }

    @PostMapping("users")
    public void addUser(@RequestBody Users user){
        users.add(user);
    }
}
