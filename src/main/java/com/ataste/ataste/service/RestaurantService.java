package com.ataste.ataste.service;

import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    public Restaurants createRestaurant(Long uid) {
        // 이미 존재하는 uid인지 확인
        Optional<Restaurants> existingRestaurant = restaurantRepository.findByUid(uid);

        if (existingRestaurant.isPresent()) {
            // 이미 존재하는 경우 저장하지 않음
            return null;
        }
        // 존재하지 않는 경우 uid 저장
        Restaurants restaurant = new Restaurants();
        restaurant.setUid(uid);
        return restaurantRepository.save(restaurant);
    }


}