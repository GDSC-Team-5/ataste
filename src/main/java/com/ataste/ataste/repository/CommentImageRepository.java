package com.ataste.ataste.repository;

import com.ataste.ataste.entity.Comment;
import com.ataste.ataste.entity.CommentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentImageRepository extends JpaRepository<CommentImage, Long>{
    List<CommentImage> findByComment(Comment comment); // 댓글에 해당하는 이미지 찾기
    List<CommentImage> findByCommentId(Long comment_id);

    List<CommentImage> findByCommentOrderByComment_LikelyDesc(Comment comment);
}
