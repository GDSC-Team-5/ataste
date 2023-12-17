package com.ataste.ataste.service;

import com.ataste.ataste.dto.CommentImageDto;
import com.ataste.ataste.dto.RestaurantImageDto;
import com.ataste.ataste.entity.CommentImage;
import com.ataste.ataste.entity.RestaurantImage;
import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.repository.RestaurantImageRepository;
import com.ataste.ataste.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.io.IOException;
import java.util.Optional;

@Service
public class RestaurantImageService {

    @Autowired
    private RestaurantImageRepository restaurantImageRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RestaurantService RestaurantService;


    // 1. 후기 X -> 디폴트 사진
    // 2. 후기 O , 이미지 사진이 없는 후기 -> 디폴트 사진

    // 1,2번 => 해당 restaurant_id에 comment가 존재하는지 확인
    @Transactional
    public RestaurantImage defaultRestaurantImage(Long restaurantId) {
        // 해당 restaurant_id에 후기가 존재하는지 확인
        boolean hasComments = commentService.hasCommentsForRestaurant(restaurantId);

        if (hasComments) {
            // 레스토랑 이미지가 존재하는지 확인 -> list로 묶은 이유는 restaurant_id가 같으면 해당 레스토랑의 이미지이기에
            List<RestaurantImage> existingImages = restaurantImageRepository.findByRestaurantId(restaurantId);
            if (existingImages != null && !existingImages.isEmpty()) {
                return setBestCommentImageForRestaurant(restaurantId); // 좋아요가 많은 사진으로 바꾸기 위해서
            }
        }
        // 후기가 없거나 이미지가 없는 경우 디폴트 이미지 반환 -> 이거는 restaurant_image 0번째 테이블에 넣어두고 사용하면 될듯 (수정사항)
        String defaultImage = "default_image.jpg";

        RestaurantImage defaultRestaurantImage = restaurantImageRepository.findByImage(defaultImage);

        if (defaultRestaurantImage == null) {
            defaultRestaurantImage = new RestaurantImage();
            // 레스토랑 정보가 아닌 레스토랑 이미지에 레스토랑 정보를 설정합니다.
            Restaurants restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));
            defaultRestaurantImage.setRestaurant(restaurant);
            defaultRestaurantImage.setImage(defaultImage);
            restaurantImageRepository.save(defaultRestaurantImage);
        }
        return defaultRestaurantImage;
    }

    // 3. 이미 레스토랑 이미지가 존재하는 경우 => 좋아요가 많은 후기의 이미지를 레스토랑 이미지로 변경
    @Transactional
    public RestaurantImage setBestCommentImageForRestaurant(Long restaurantId) {
        // 레스토랑의 좋아요를 가장 많이 받은 후기의 ID를 가져온다.
        Long bestCommentId = commentService.findBestCommentImage(restaurantId);

        if (bestCommentId != null) {
            List<CommentImage> bestCommentImages = commentService.getCommentImages(bestCommentId);

            if (bestCommentImages != null && !bestCommentImages.isEmpty()) {
                RestaurantImage restaurantImage = new RestaurantImage();

                Restaurants restaurant = restaurantRepository.findById(restaurantId)
                        .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));

                restaurantImage.setRestaurant(restaurant);

                // 해당 후기의 여러가지 사진 중 맨 앞에 있는 사진을 레스토랑 대표사진으로 사용
                CommentImage firstImage = bestCommentImages.get(0);
                restaurantImage.setImage(firstImage.getImage());

                restaurantImageRepository.save(restaurantImage);
                return restaurantImage;
            }
        }
        return null;
    }

    @Autowired
    public RestaurantImageService(RestaurantImageRepository restaurantImageRepository) {
        this.restaurantImageRepository = restaurantImageRepository;
    }

    @Transactional
    public RestaurantImage setRestaurantImage(Long restaurantId, CommentImageDto commentImageDto) throws IOException {

        List<RestaurantImage> existingImages = restaurantImageRepository.findByRestaurantId(restaurantId);

        if (existingImages.isEmpty()) {
            return setDefaultRestaurantImage(restaurantId, commentImageDto);
        }

        Optional<RestaurantImage> likelyImage = findLikelyRestaurantImage(restaurantId);

        if (likelyImage.isPresent()) {
            return likelyImage.get();
        } else {
            return setDefaultRestaurantImage(restaurantId, commentImageDto);
        }
    }

    @Transactional
    public void setAutoImageForRestaurant(Long restaurantId, String imageUrl) {
        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));

        // 이미 레스토랑 이미지가 존재하는 경우 업데이트
        List<RestaurantImage> existingImages = restaurantImageRepository.findByRestaurantId(restaurantId);

        if (!existingImages.isEmpty()) {
            RestaurantImage existingImage = existingImages.get(0); // 예시로 첫 번째 이미지를 선택하는 경우
            existingImage.setImageUrl(imageUrl);
            restaurantImageRepository.save(existingImage);
        } else {
            // 레스토랑 이미지가 존재하지 않는 경우 새로 생성
            RestaurantImage newImage = new RestaurantImage();
            newImage.setRestaurant(restaurant);
            newImage.setImageUrl(imageUrl);
            restaurantImageRepository.save(newImage);
        }
    }

    private RestaurantImage setDefaultRestaurantImage(Long restaurantId, CommentImageDto commentImageDto) throws IOException {
        String defaultImage = "default_image.jpg";
        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));

        RestaurantImage existingDefaultImage = restaurantImageRepository.findByImage(defaultImage);

        if (existingDefaultImage == null) {
            RestaurantImage defaultRestaurantImage = new RestaurantImage();
            defaultRestaurantImage.setRestaurant(restaurant);
            defaultRestaurantImage.setImageUrl(defaultImage);
            return restaurantImageRepository.save(defaultRestaurantImage);
        } else {
            return existingDefaultImage;
        }
    }

    private Optional<RestaurantImage> findLikelyRestaurantImage(Long restaurantId) {
        Long bestCommentId = commentService.findBestCommentImage(restaurantId);

        if (bestCommentId != null) {
            List<CommentImage> bestCommentImages = commentService.getCommentImages(bestCommentId);

            return bestCommentImages.stream()
                    .findFirst()
                    .map(commentImage -> {
                        RestaurantImage restaurantImage = new RestaurantImage();
                        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                                .orElseThrow(() -> new EntityNotFoundException("레스토랑을 찾을 수 없습니다."));
                        restaurantImage.setRestaurant(restaurant);
                        restaurantImage.setImageUrl(commentImage.getImage());
                        return restaurantImageRepository.save(restaurantImage);
                    });
        }

        return Optional.empty();
    }

}
