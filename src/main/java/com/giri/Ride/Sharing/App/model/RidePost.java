package com.giri.Ride.Sharing.App.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rideposts")
public class RidePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ridepostId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String source;
    private String destination;

    private LocalDate rideDate;
    private LocalTime rideTime;

    private Double fare;

    private int passengerLimit;
    private String vehicleType;
    private String vehicleName;

    private String notes;

    @Column(nullable = false)
    private String status = "OPEN";

    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
}

