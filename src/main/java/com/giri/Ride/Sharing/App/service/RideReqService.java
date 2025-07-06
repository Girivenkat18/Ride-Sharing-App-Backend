package com.giri.Ride.Sharing.App.service;

import com.giri.Ride.Sharing.App.dao.RidePostRepo;
import com.giri.Ride.Sharing.App.dao.RideReqRepo;
import com.giri.Ride.Sharing.App.model.RidePost;
import com.giri.Ride.Sharing.App.model.RideRequest;
import com.giri.Ride.Sharing.App.model.User;
import com.giri.Ride.Sharing.App.dao.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideReqService {

    private final JwtService jwtService;

    @Autowired
    private RideReqRepo rideRequestRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RidePostRepo ridePostRepository;

    public RideRequest createRideRequest(String authHeader, Long ridePostId, String message) {
        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7);

        // Extract email (username) from token
        String email = jwtService.extractEmail(token);

        // Find the user
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Find the ride post
        RidePost ridePost = ridePostRepository.findById(ridePostId)
                .orElseThrow(() -> new RuntimeException("Ride post not found"));

        if (ridePost.getOwner().getEmail().equals(email)) {
            throw new RuntimeException("You cannot request to join your own ride post.");
        }

        // Create request
        RideRequest request = RideRequest.builder()
                .requester(user)
                .ride(ridePost)
                .message(message)
                .status("PENDING")
                .created_at(LocalDateTime.now())
                .updated_at(LocalDateTime.now())
                .build();

        return rideRequestRepository.save(request);
    }

    public List<RideRequest> getRequestsByUser(Long userId) {
        return rideRequestRepository.findByRequester_UserId(userId);
    }

    public RideRequest getRideRequestById(Long requestId) {
        return rideRequestRepository.findById(requestId)
                .orElse(null);
    }


    public RideRequest updateRequestStatus(Long requestId, String status) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        request.setUpdated_at(java.time.LocalDateTime.now());
        return rideRequestRepository.save(request);
    }

    public boolean dropRideRequest(Long requestId, String userEmail) {
        Optional<RideRequest> optionalRequest = rideRequestRepository.findById(requestId);

        if (optionalRequest.isPresent()) {
            RideRequest request = optionalRequest.get();
            if (request.getRequester().getEmail().equals(userEmail)) {
                request.setStatus("DROPPED");
                request.setUpdated_at(LocalDateTime.now());
                rideRequestRepository.save(request);
                return true;
            }
        }
        return false; // Not authorized or request not found
    }

}
