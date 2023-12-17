package com.ataste.ataste.controller;
import com.ataste.ataste.dto.CommentImageDto;
import com.ataste.ataste.service.CommentImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RestController
@Validated
@RequestMapping("/restaurants/image")
@CrossOrigin(origins = "http://104.198.104.70:8082",allowedHeaders = "*")
public class CommentImageController {
    @Autowired
    private CommentImageService commentImageService;


    @Autowired
    public CommentImageController(CommentImageService commentImageService) {
        this.commentImageService = commentImageService;
    }

    // 이미지 업로드
    @PostMapping("/upload/{commentId}")
    public ResponseEntity<CommentImageDto> saveCommentImage(
            @PathVariable Long commentId,
            @RequestParam("file") List<MultipartFile> files
    ) throws IOException {
        CommentImageDto savedCommentImage = commentImageService.saveCommentImage(commentId, files);

        return new ResponseEntity<>(savedCommentImage, HttpStatus.CREATED);
    }

    // 이미지 수정
    // AWS는 Patch를 지원하지 않는다.
    @PutMapping("/update/{imageId}")
    public ResponseEntity<CommentImageDto> updateCommentImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        CommentImageDto updatedCommentImage = commentImageService.updateCommentImage(imageId, file);

        return new ResponseEntity<>(updatedCommentImage, HttpStatus.OK);
    }

    // 이미지 삭제
    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<String> deleteCommentImage(@PathVariable Long imageId) {
        commentImageService.deleteCommentImage(imageId);

        return new ResponseEntity<>("이미지가 삭제되었습니다.", HttpStatus.OK);
    }

}
