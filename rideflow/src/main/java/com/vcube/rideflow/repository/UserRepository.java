package com.vcube.rideflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vcube.rideflow.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
