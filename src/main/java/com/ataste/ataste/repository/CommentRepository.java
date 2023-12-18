package com.ataste.ataste.repository;

import com.ataste.ataste.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>{
    List<Comment> findByRestaurantId(Long restaurant_id);
    List<Comment> findByRestaurantIdOrderByLikelyDesc(Long restaurantId);
}
