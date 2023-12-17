package com.ataste.ataste.dto;

import org.springframework.web.multipart.MultipartFile;

public class RestaurantImageDto {
    private Long restaurant_id;
    private MultipartFile image; // 이미지 파일

    public Long getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(Long restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
