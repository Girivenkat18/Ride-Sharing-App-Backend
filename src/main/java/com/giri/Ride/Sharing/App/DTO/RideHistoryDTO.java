package com.giri.Ride.Sharing.App.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideHistoryDTO {
    private String source;
    private String destination;
    private String rideDate;
    private String rideTime;
    private double fare;
    private String full_name;
    private int passengerLimit;
    private String contact_no;
    private String vehicleName;
    private String status; // confirmed, completed, pending
    private String type; // POSTED or REQUESTED

}