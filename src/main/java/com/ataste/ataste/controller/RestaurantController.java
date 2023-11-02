package com.ataste.ataste.controller;

import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.repository.RestaurantRepository;
import com.ataste.ataste.service.KakaoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final KakaoApiService kakaoApiService;

    public RestaurantController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/restaurants") // 외부 데이터
    public String getAllResultsFromKakao() {

        String[] queries = new String[] { "" };
        return kakaoApiService.getRestaurantsFromKakao();
    }

    @GetMapping("/restaurants/all") // 내부 데이터 - 모든 레스토랑 가져오기
    public List<Restaurants> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/restaurants/{category}") // 카테고리 별 레스토랑 가져오기
    public List<Restaurants> getRestaurantsByCategory(@PathVariable String category) {
        return restaurantRepository.findByCategory(category);
    }

    @GetMapping("/restaurants/{category}/top-rated") // 카테고리 중 좋아요 순 레스토랑 가져오기
    public List<Restaurants> getTopRatedRestaurantsByCategory(@PathVariable String category) {
        return restaurantRepository.findByCategoryOrderByLikesDesc(category);
    }
}
