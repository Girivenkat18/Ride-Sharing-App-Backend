package com.giri.Ride.Sharing.App.service;

import com.giri.Ride.Sharing.App.DTO.RideHistoryDTO;
import com.giri.Ride.Sharing.App.dao.RidePostRepo;
import com.giri.Ride.Sharing.App.model.RidePost;
import com.giri.Ride.Sharing.App.model.RideRequest;
import com.giri.Ride.Sharing.App.dao.RidePostRepo;
import com.giri.Ride.Sharing.App.dao.RideReqRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.giri.Ride.Sharing.App.dao.UserRepo;
import com.giri.Ride.Sharing.App.model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo repo;
    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    @Autowired
    RidePostRepo ridePostRepository;

    @Autowired
    RideReqRepo rideReqRepository;

    public User saveUser(User user) {
//        user.setUser_id(123);
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        return repo.save(user);
    }

    public User getUserInfo(String authHeader) {
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractEmail(jwt);

        // Get user by email
        User user = userRepo.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userRepo.findByEmail(userEmail);
    }

    public Map<String, List<RideHistoryDTO>> getRideHistory(User user) {
        List<RideHistoryDTO> upcoming = new ArrayList<>();
        List<RideHistoryDTO> previous = new ArrayList<>();
        List<RideHistoryDTO> pending = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // Posted rides
        List<RidePost> postedRides = ridePostRepository.findByOwnerUserId(user.getUserId());
        for (RidePost ride : postedRides) {
            RideHistoryDTO dto = new RideHistoryDTO(
                    ride.getSource(),
                    ride.getDestination(),
                    ride.getRideDate().toString(),
                    ride.getRideTime().toString(),
                    ride.getFare(),
                    ride.getOwner().getFull_name(),
                    ride.getPassengerLimit(),
                    ride.getOwner().getContact_no(),
                    ride.getVehicleName(),

                    ride.getStatus().equals("CANCELLED") ? "cancelled" :
                            (ride.getRideDate().isAfter(today) ||
                                    (ride.getRideDate().isEqual(today) && ride.getRideTime().isAfter(now))) ? "confirmed" : "completed",
                    "POSTED"
            );

            if (dto.getStatus().equals("confirmed")) {
                upcoming.add(dto);
            } else {
                previous.add(dto);
            }
        }

        // Requested rides
        List<RideRequest> requests = rideReqRepository.findByRequester_UserId(user.getUserId());
        for (RideRequest req : requests) {
            RidePost ride = req.getRide();
            String rideDate = ride.getRideDate().toString();
            String rideTime = ride.getRideTime().toString();
            boolean isFuture = ride.getRideDate().isAfter(today) ||
                    (ride.getRideDate().isEqual(today) && ride.getRideTime().isAfter(now));

            if (req.getStatus().equals("PENDING")) {
                pending.add(new RideHistoryDTO(
                        ride.getSource(),
                        ride.getDestination(),
                        rideDate,
                        rideTime,
                        ride.getFare(),
                        ride.getOwner().getFull_name(),
                        ride.getPassengerLimit(),
                        ride.getOwner().getContact_no(),
                        ride.getVehicleName(),
                        "pending",
                        "REQUESTED"
                ));
            } else if (req.getStatus().equals("ACCEPTED")) {
                RideHistoryDTO dto = new RideHistoryDTO(
                        ride.getSource(),
                        ride.getDestination(),
                        rideDate,
                        rideTime,
                        ride.getFare(),
                        ride.getOwner().getFull_name(),
                        ride.getPassengerLimit(),
                        ride.getOwner().getContact_no(),
                        ride.getVehicleName(),

                        isFuture ? "confirmed" : "completed",
                        "REQUESTED"
                );
                if (isFuture) {
                    upcoming.add(dto);
                } else {
                    previous.add(dto);
                }
            }
        }

        Map<String, List<RideHistoryDTO>> result = new HashMap<>();
        result.put("upcoming", upcoming);
        result.put("previous", previous);
        result.put("pending", pending);

        return result;
    }


    public List<User> getAllUsers() {
        return repo.findAll();
    }

}