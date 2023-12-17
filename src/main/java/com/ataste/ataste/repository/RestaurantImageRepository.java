package com.ataste.ataste.repository;

import com.ataste.ataste.entity.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {
    List<RestaurantImage> findByRestaurantId(Long restaurant_id);
    RestaurantImage findByImage(String image);
}
