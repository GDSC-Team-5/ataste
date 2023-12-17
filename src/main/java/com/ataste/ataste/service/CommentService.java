package com.ataste.ataste.service;

import com.ataste.ataste.dto.CommentDto;
import com.ataste.ataste.entity.Comment;
import com.ataste.ataste.entity.CommentImage;
import com.ataste.ataste.entity.Member;
import com.ataste.ataste.entity.Restaurants;
import com.ataste.ataste.repository.CommentImageRepository;
import com.ataste.ataste.repository.CommentRepository;
import com.ataste.ataste.repository.MemberRepository;
import com.ataste.ataste.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CommentImageRepository commentImageRepository;


    // 댓글 작성
    @Transactional
    public Comment createComment(Long restaurantId, CommentDto commentDto) {

        Member member = memberRepository.findById(commentDto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("음식점을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setMember(member);
        comment.setLikely(0L);

        comment.getCreDate();



        comment.setRestaurant(restaurant);
        comment.setTitle(commentDto.getTitle());
        comment.setContent(commentDto.getContent());
        comment.setGrade(commentDto.getGrade());

        Comment savedComment = commentRepository.save(comment);

        // CommentImage => 댓글과 함께 첨부되는 이미지
        // MultipartFile - 다중파일 업로드

        List<CommentImage> commentImages = new ArrayList<>();

        List<MultipartFile> images = commentDto.getImages();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile imageFile : images) {
                CommentImage commentImage = new CommentImage();
                commentImage.setComment(savedComment);
                commentImage.setImage(imageFile.getOriginalFilename()); // URL이 저장됨

                commentImages.add(commentImage);
            }
            commentImageRepository.saveAll(commentImages);
        }

        return savedComment;
    }

    // 후기 삭제
    @Transactional
    public Comment deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("후기 Id를 찾을 수 없습니다"));

        List<CommentImage> commentImages = commentImageRepository.findByComment(comment);

        commentRepository.delete(comment);
        commentImageRepository.deleteAll(commentImages);

        return comment;  // 삭제된 댓글을 반환
    }

    // 댓글 수정
    // 1. 글 수정
    // 2. 사진 수정
    @Transactional
    public Comment updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다"));

        // 새로운 후기 내용과 평점으로 업데이트
        comment.setTitle(commentDto.getTitle());
        comment.setContent(commentDto.getContent());
        comment.setGrade(commentDto.getGrade());

        return commentRepository.save(comment);
    }

    // 좋아요 수 증가
    @Transactional
    public void increaseLikely(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("후기를 찾을 수 없습니다"));

        comment.setLikely(comment.getLikely() + 1);
        // 변경된 Comment 엔터티를 저장
        commentRepository.save(comment);
    }

    public Restaurants getRestaurantById(Long restaurantId) {
        Optional<Restaurants> restaurantOptional = restaurantRepository.findById(restaurantId);
        return restaurantOptional.orElseThrow(() -> new EntityNotFoundException("음식점을 찾을 수 없습니다."));
    }

    @Transactional
    public Comment updateCommentImage(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("후기를 찾을 수 없습니다"));

        // 기존 이미지 삭제
        List<CommentImage> existingImages = commentImageRepository.findByComment(comment);
        commentImageRepository.deleteAll(existingImages);

        // 새로운 이미지 추가
        List<CommentImage> newImages = new ArrayList<>();
        for (MultipartFile imageFile : commentDto.getImages()) {
            CommentImage commentImage = new CommentImage();
            commentImage.setComment(comment);
            commentImage.setImage(imageFile.getOriginalFilename()); // 새 이미지 설정

            newImages.add(commentImage);
        }
        commentImageRepository.saveAll(newImages);

        return comment;
    }

    // 좋아요 수가 가장 많으면서 이미지가 있는 후기 찾기
    public Long findBestCommentImage(Long restaurantId) {
        List<Comment> comments = commentRepository.findByRestaurantId(restaurantId);
        List<Comment> bestCommentsWithImages = new ArrayList<>();

        for (Comment comment : comments) {
            // 이미지가 있는 후기만 추출
            if (!comment.getImages().isEmpty()) {
                bestCommentsWithImages.add(comment);
            }
        }

        // 이미지가 있는 후기가 없으면 null 반환
        if (bestCommentsWithImages.isEmpty()) {
            return null;
        }

        // 좋아요 수가 가장 많은 후기 찾기
        Comment bestComment = bestCommentsWithImages.stream()
                .max(Comparator.comparing(Comment::getLikely))
                .orElseThrow(EntityNotFoundException::new);

        return bestComment.getId();
    }



    // findBestComment의 이미지 가지고 와서 레스토랑 image로 사용하기
    public String getBestCommentImage(Long bestCommentId) {

        Comment bestComment = commentRepository.findById(bestCommentId)
                .orElseThrow(() -> new EntityNotFoundException("후기를 찾을 수 없습니다."));

        List<CommentImage> commentImages = commentImageRepository.findByComment(bestComment);

        if (!commentImages.isEmpty()) {
            CommentImage selectedImage = commentImages.get(0); // 예시로 첫 번째 이미지를 선택하는 경우
            return selectedImage.getImage();
        } else {
            return null; // 이미지가 없을 경우
        }
    }

    @Transactional
    public boolean hasCommentsForRestaurant(Long restaurantId) {
        List<Comment> comments = commentRepository.findByRestaurantId(restaurantId);
        return !comments.isEmpty();
    }

    public List<CommentImage> getCommentImages(Long commentId) {
        return commentImageRepository.findByCommentId(commentId);
    }


}