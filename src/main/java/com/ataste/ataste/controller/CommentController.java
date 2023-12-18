package com.ataste.ataste.controller;


import com.ataste.ataste.dto.CommentDto;
import com.ataste.ataste.entity.Comment;
import com.ataste.ataste.repository.CommentRepository;
import com.ataste.ataste.service.CommentService;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

<<<<<<< HEAD
@CrossOrigin(origins = "http://localhost:8081",allowedHeaders = "*")
=======
@CrossOrigin(origins = "http://localhost:3000",allowedHeaders = "*")
>>>>>>> 790ba187b7f3b91e1ad2a36ed5f090896b456920
@RestController
@RequestMapping("/restaurants") //  몇번째 쓰여진 comment인지도 알아야하니 마지막에 {comment_id} => 레스토랑 페이지가 생성되고 변경
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/{restaurant_id}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable("restaurant_id") Long restaurantId, // 이 부분이 수정된 부분
            @RequestBody CommentDto commentDto) {
        try {
            Comment createdComment = commentService.createComment(restaurantId, commentDto);
            return new ResponseEntity<>("후기 작성 성공 " + createdComment.getId(), HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("오류: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("사진 업로드 시 오류 발생: ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{restaurant_id}/delete/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable("comment_id") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return new ResponseEntity<>("후기 삭제 성공", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("오류 발생: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //댓글 수정 - 글 수정
    @PatchMapping("/{restaurant_id}/updateComment/{comment_id}")
    public ResponseEntity<String> updateComment(@PathVariable("comment_id") Long commentId, @RequestBody CommentDto commentDto) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, commentDto);
            return new ResponseEntity<>("업데이트 완료" + updatedComment.getId(), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("오류 발생: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // 후기 좋아요 눌렀을 경우 좋아요 수 +1 증가
    @PutMapping("/increaseLikely/{comment_id}")
    public ResponseEntity<String> increaseLikely(@PathVariable("comment_id") Long commentId) {
        try {
            commentService.increaseLikely(commentId);
            Comment updatedComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("후기를 찾을 수 없습니다"));

            return new ResponseEntity<>("좋아요 수 증가 성공. 현재 좋아요 수: " + updatedComment.getLikely(), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("후기를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }


}
