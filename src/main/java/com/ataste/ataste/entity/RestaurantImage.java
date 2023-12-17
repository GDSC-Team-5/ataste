package com.ataste.ataste.entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantImage {
    // 1. 레스토랑 사진이 없을 경우
    // => 후기 X => default 사진
    // => 가장 먼저 등록된 후기의 이미지 사진으르 레스토랑 대표 사진으로 사용

    // 2. 레스토랑 사진이 있을 경우
    // => 댓글 중 좋아요를 가장 많이 받은 후기의 사진 사용

    // 3. 후기가 삭제되었을 경우
    // => 그냥 쓴다. (정해야한다.)

    private String imageUrl;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurants restaurant;

    @Column
    private String image; // 레스토랑 대표사진

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
