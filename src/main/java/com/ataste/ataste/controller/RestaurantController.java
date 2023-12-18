package com.ataste.ataste.controller;


import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@CrossOrigin(origins = "http://localhost:8081",allowedHeaders = "*")
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    // url로 레스토랑 등록
    @PostMapping("/enroll/{uid}")
    public ResponseEntity<Restaurants> createRestaurant(@PathVariable("uid") Long uid) {
        Restaurants restaurant = restaurantService.createRestaurant(uid);
        return ResponseEntity.ok(restaurant);
    }
}
