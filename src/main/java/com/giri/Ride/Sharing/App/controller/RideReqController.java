package com.giri.Ride.Sharing.App.controller;

import com.giri.Ride.Sharing.App.dao.UserRepo;
import com.giri.Ride.Sharing.App.model.RidePost;
import com.giri.Ride.Sharing.App.model.RideRequest;
import com.giri.Ride.Sharing.App.model.User;
import com.giri.Ride.Sharing.App.service.JwtService;
import com.giri.Ride.Sharing.App.service.RideReqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@CrossOrigin(origins = "http://10.0.2.2:8081")
@RestController
@RequestMapping("/api/ride-requests")
public class RideReqController {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepo userRepository;

    @Autowired
    private RideReqService rideRequestService;

    @PostMapping("/create")
    public RideRequest createRequest(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Long ridePostId,
            @RequestParam(required = false) String message
    ) {
        return rideRequestService.createRideRequest(authHeader, ridePostId, message);
    }

    @GetMapping("/by-user")
    public List<RideRequest> getRequestsByUser(@RequestHeader("Authorization") String authHeader) {
        String email = jwtService.extractEmail(authHeader.substring(7)); // Remove "Bearer "
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return rideRequestService.getRequestsByUser(user.getUserId());
    }


    @PutMapping("/{requestId}/accept")
    public ResponseEntity<String> acceptRideRequest(
            @PathVariable Long requestId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userEmail = jwtService.extractEmail(token);

        RideRequest rideRequest = rideRequestService.getRideRequestById(requestId);
        if (rideRequest == null) {
            return ResponseEntity.notFound().build();
        }

        RidePost ridePost = rideRequest.getRide();
        if (!ridePost.getOwner().getEmail().equals(userEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to accept this request.");
        }

        RideRequest updated = rideRequestService.updateRequestStatus(requestId, "ACCEPTED");
        return ResponseEntity.ok("Ride request accepted successfully.");
    }

    @PutMapping("/drop")
    public ResponseEntity<String> dropFromRide(
            @RequestParam Long requestId,
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtService.extractEmail(authHeader.substring(7));
        boolean dropped = rideRequestService.dropRideRequest(requestId, email);

        if (dropped) {
            return ResponseEntity.ok("Ride request has been dropped successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to drop this ride request.");
        }
    }

}

