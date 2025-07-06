package com.giri.Ride.Sharing.App.service;

import com.giri.Ride.Sharing.App.dao.RideReqRepo;
import com.giri.Ride.Sharing.App.model.RidePost;
import com.giri.Ride.Sharing.App.model.User;
import com.giri.Ride.Sharing.App.dao.RidePostRepo;
import com.giri.Ride.Sharing.App.dao.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RidePostService {

    private final RidePostRepo ridePostRepository;
    private final UserRepo userRepository;
    private final RideReqRepo rideRequestRepository;

    public RidePost createRidePost(RidePost ridePost) {
        Optional<User> ownerOpt = userRepository.findById((long) Math.toIntExact(ridePost.getOwner().getUserId()));
        ownerOpt.ifPresent(ridePost::setOwner);
        return ridePostRepository.save(ridePost);
    }

    public List<RidePost> getAllRidePosts() {
        return ridePostRepository.findAll();
    }

    public List<RidePost> searchRides(String source, String destination, LocalDate rideDate, String vehicleType) {
        return ridePostRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndRideDateAndVehicleTypeIgnoreCase(
                source, destination, rideDate, vehicleType);
    }


    public Optional<RidePost> getRidePostById(Integer id) {
        return ridePostRepository.findById(Long.valueOf(id));
    }

    public RidePost updateRidePost(Integer id, RidePost updatedRidePost) {
        return ridePostRepository.findById(Long.valueOf(id))
                .map(existing -> {
                    existing.setSource(updatedRidePost.getSource());
                    existing.setDestination(updatedRidePost.getDestination());
                    existing.setRideDate(updatedRidePost.getRideDate());
                    existing.setRideTime(updatedRidePost.getRideTime());
                    existing.setFare(updatedRidePost.getFare());
                    existing.setPassengerLimit(updatedRidePost.getPassengerLimit());
                    existing.setVehicleType(updatedRidePost.getVehicleType());
                    existing.setVehicleName(updatedRidePost.getVehicleName());
                    existing.setNotes(updatedRidePost.getNotes());
                    existing.setStatus(updatedRidePost.getStatus());
                    return ridePostRepository.save(existing);
                }).orElse(null);
    }

    public boolean cancelRidePostIfOwner(Long ridePostId, Long userId) {
        Optional<RidePost> optionalPost = ridePostRepository.findById(ridePostId);
        if (optionalPost.isPresent()) {
            RidePost ridePost = optionalPost.get();
            if (ridePost.getOwner().getUserId().equals(userId)) {
                ridePost.setStatus("CANCELLED");
                ridePost.setUpdated_at(LocalDateTime.now());
                ridePostRepository.save(ridePost);
                return true;
            }
        }
        return false;
    }

}

