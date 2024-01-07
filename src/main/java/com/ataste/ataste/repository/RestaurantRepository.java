package com.ataste.ataste.repository;

import com.ataste.ataste.entity.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurants, Long> {
    Optional<Restaurants> findById(Long id);
    Optional<Restaurants> findByUid(Long uid);
}