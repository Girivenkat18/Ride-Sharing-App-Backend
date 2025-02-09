package com.giri.Ride.Sharing.App.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    private int user_id;
    private String user_name;
    private String role;

}
