package com.giri.Ride.Sharing.App.dao;

import com.giri.Ride.Sharing.App.model.RidePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RidePostRepo extends JpaRepository<RidePost, Long> {
    List<RidePost> findByOwnerUserId(Long userId);
    List<RidePost> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndRideDateAndVehicleTypeIgnoreCase(
            String source,
            String destination,
            LocalDate rideDate,
            String vehicleType
    );

}