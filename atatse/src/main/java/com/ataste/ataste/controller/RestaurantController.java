package com.ataste.ataste.controller;

import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public List<Restaurants> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/{category}")
    public List<Restaurants> getRestaurantsByCategory(@PathVariable String category) {
        return restaurantRepository.findByCategory(category);
    }

    @GetMapping("/{category}/top-rated")
    public List<Restaurants> getTopRatedRestaurantsByCategory(@PathVariable String category) {
        return restaurantRepository.findByCategoryOrderByLikesDesc(category);
    }
}
