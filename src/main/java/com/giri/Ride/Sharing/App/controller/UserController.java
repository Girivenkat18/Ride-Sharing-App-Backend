package com.giri.Ride.Sharing.App.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.giri.Ride.Sharing.App.DTO.RideHistoryDTO;
import com.giri.Ride.Sharing.App.dao.UserRepo;
import com.giri.Ride.Sharing.App.model.User;
import com.giri.Ride.Sharing.App.service.JwtService;
import com.giri.Ride.Sharing.App.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://10.0.2.2:8081")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepo repo;

    @GetMapping("/auth/users")
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping("/auth/register")
    public User register(@RequestBody User user) {
        return service.saveUser(user);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        System.out.println("Login endpoint hit with email: " + user.getEmail());
        System.out.println("\n\n=== LOGIN ENDPOINT HIT ===");
        System.out.println("Time: " + new java.util.Date());
        System.out.println("Received request with email: " + user.getEmail());
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("Request body: " + mapper.writeValueAsString(user));
        } catch (Exception e) {
            System.out.println("Could not log request body");
        }

        try {
            System.out.println("Starting authentication process...");
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            System.out.println("Authentication completed. Result: " + authentication.isAuthenticated());

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail());
                System.out.println("Token generated successfully");

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login successful");

                System.out.println("Sending success response");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Authentication failed - not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication failed"));
            }
        } catch (Exception e) {
            System.out.println("=== Authentication Exception ===");
            System.out.println("Error type: " + e.getClass().getName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication failed: " + e.getMessage()));
        }
    }


    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        User user = service.getUserInfo(authHeader);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/ride-history")
    public ResponseEntity<Map<String, List<RideHistoryDTO>>> getRideHistory(
            @RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7));
        User user = repo.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, List<RideHistoryDTO>> history = service.getRideHistory(user);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        try {
            System.out.println("Ping request received");
            return ResponseEntity.ok("pong");
        } catch (Exception e) {
            System.err.println("Ping error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing ping request");
        }
    }

    @PostMapping("/test-post")
    public ResponseEntity<?> testPost(@RequestBody Map<String, String> body) {
        try {
            System.out.println("\n=== TEST POST ENDPOINT HIT ===");
            System.out.println("Received body: " + body);
            return ResponseEntity.ok(Map.of(
                    "message", "Post test successful",
                    "received", body
            ));
        } catch (Exception e) {
            System.err.println("Test post error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing test post"));
        }
    }
}
