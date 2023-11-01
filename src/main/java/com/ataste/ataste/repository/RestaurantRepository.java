package com.ataste.ataste.repository;

import com.ataste.ataste.entity.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurants, Long> {
    List<Restaurants> findByCategory(String category);
    List<Restaurants> findByCategoryOrderByLikesDesc(String category);

}
