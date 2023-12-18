package com.ataste.ataste.repository;

import com.ataste.ataste.entity.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {
    List<RestaurantImage> findByRestaurantId(Long restaurant_id);
    RestaurantImage findByImage(String image);
}
