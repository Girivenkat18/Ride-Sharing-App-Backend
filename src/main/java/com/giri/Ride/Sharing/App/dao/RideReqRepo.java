package com.giri.Ride.Sharing.App.dao;

import com.giri.Ride.Sharing.App.model.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.giri.Ride.Sharing.App.model.RidePost;

import java.util.List;

@Repository
public interface RideReqRepo extends JpaRepository<RideRequest, Long> {
    List<RideRequest> findByRequester_UserId(Long requesterId);
    List<RideRequest> findByRide_RidepostId(Long rideId);
    void deleteByRide(RidePost ride);
    List<RideRequest> findByRequesterEmailAndStatusNot(String email, String status);

}
