package com.giri.Ride.Sharing.App.controller;


import com.giri.Ride.Sharing.App.dao.RidePostRepo;
import com.giri.Ride.Sharing.App.dao.UserRepo;
import com.giri.Ride.Sharing.App.model.RidePost;
import com.giri.Ride.Sharing.App.model.User;
import com.giri.Ride.Sharing.App.service.JwtService;
import com.giri.Ride.Sharing.App.service.RidePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://10.0.2.2:8081")
@RestController
@RequestMapping("/api/rideposts")
@RequiredArgsConstructor
public class RidePostController {

    @Autowired
    JwtService jwtService;

    @Autowired
    private final UserRepo userRepository;

    @Autowired
    private final RidePostRepo ridePostRepository;

    private final RidePostService ridePostService;

    @PostMapping("/create")
    public ResponseEntity<String> createRidePost(@RequestBody RidePost ridePost,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();  // This comes from the JWT

        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user");
        }

        ridePost.setOwner(userOpt.get());
        ridePostService.createRidePost(ridePost);
        return ResponseEntity.ok("RidePost created successfully.");
    }


    @GetMapping
    public ResponseEntity<List<RidePost>> getAllRidePosts() {
        return ResponseEntity.ok(ridePostService.getAllRidePosts());
    }


    @GetMapping("/my-rides")
    public ResponseEntity<List<RidePost>> getMyRidePosts(
            @RequestHeader("Authorization") String authHeader
    ) {
        // Extract token from header
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractEmail(jwt);

        // Get user by email
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Fetch rides owned by this user
        List<RidePost> ridePosts = ridePostRepository.findByOwnerUserId(user.getUserId());

        return ResponseEntity.ok(ridePosts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RidePost>> searchRides(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate rideDate,
            @RequestParam String vehicleType
    ) {
        System.out.println("Searching for rides:");
        System.out.println("Source: " + source);
        System.out.println("Destination: " + destination);
        System.out.println("Date: " + rideDate);
        System.out.println("Vehicle Type: " + vehicleType);
        List<RidePost> rides = ridePostService.searchRides(source, destination, rideDate, vehicleType);
        return ResponseEntity.ok(rides);
    }

    @PutMapping("/{ridePostId}")
    public ResponseEntity<String> updateRidePost(
            @PathVariable Long ridePostId,
            @RequestBody RidePost ridePost,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Extract JWT token and get user email
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String userEmail = jwtService.extractEmail(token);  // or extractEmail if you renamed

        // Find the logged-in user
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Get the original ride post
        Optional<RidePost> existingRidePostOpt = ridePostService.getRidePostById(Math.toIntExact(ridePostId));
        if (existingRidePostOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        RidePost existingRidePost = existingRidePostOpt.get();

        // Check if the logged-in user is the owner
        if (!existingRidePost.getOwner().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update this ride post.");
        }

        // Perform the update
        RidePost updatedRidePost = ridePostService.updateRidePost(Math.toIntExact(ridePostId), ridePost);
        return ResponseEntity.ok("RidePost updated successfully.");
    }


    @PutMapping("/{ridePostId}/cancel")
    public ResponseEntity<String> cancelRidePost(
            @PathVariable Long ridePostId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractEmail(jwt);

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        boolean cancelled = ridePostService.cancelRidePostIfOwner(ridePostId, user.getUserId());

        if (cancelled) {
            return ResponseEntity.ok("RidePost has been cancelled.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to cancel this ride post.");
        }
    }
    
}

