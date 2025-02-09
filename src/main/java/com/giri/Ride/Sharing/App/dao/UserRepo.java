package com.giri.Ride.Sharing.App.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.giri.Ride.Sharing.App.model.User;
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    User findByUsername(String username);
}
