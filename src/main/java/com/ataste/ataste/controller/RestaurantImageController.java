package com.ataste.ataste.controller;


import com.ataste.ataste.service.CommentImageService;
import com.ataste.ataste.service.CommentService;
import com.ataste.ataste.service.RestaurantImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "taste.suitestudy.com:8082",allowedHeaders = "*")
public class RestaurantImageController {

    private final RestaurantImageService restaurantImageService;
    private final CommentImageService commentImageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    public RestaurantImageController(RestaurantImageService restaurantImageService, CommentImageService commentImageService) {
        this.restaurantImageService = restaurantImageService;
        this.commentImageService = commentImageService;
    }

    @PostMapping("/{restaurantId}/auto-image")
    public ResponseEntity<String> setAutoImageForRestaurant(@PathVariable Long restaurantId) {
        try {
            // 특정 레스토랑에 대해 좋아요가 가장 많은 후기 이미지 가져오기
            Long bestCommentId = commentService.findBestCommentImage(restaurantId);

            if (bestCommentId != null) {
                // 해당 이미지를 레스토랑 이미지 테이블에 저장
                String bestCommentImage = commentService.getBestCommentImage(bestCommentId);

                if (bestCommentImage != null) {
                    restaurantImageService.setAutoImageForRestaurant(restaurantId, bestCommentImage);
                    return new ResponseEntity<>("레스토랑 이미지 자동 저장 성공", HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("최고 평가 후기에 이미지가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("해당 레스토랑에 대한 후기가 없습니다.", HttpStatus.NOT_FOUND);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("오류 발생: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}