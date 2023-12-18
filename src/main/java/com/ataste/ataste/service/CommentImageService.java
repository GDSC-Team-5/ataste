package com.ataste.ataste.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.ataste.ataste.dto.CommentImageDto;
import com.ataste.ataste.entity.Comment;
import com.ataste.ataste.entity.CommentImage;
import com.ataste.ataste.repository.CommentImageRepository;
import com.ataste.ataste.repository.CommentRepository;
import com.ataste.ataste.utils.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CommentImageService {
    private final S3Uploader s3Uploader;
    private final AmazonS3Client amazonS3;
    private final CommentRepository commentRepository;
    private final CommentImageRepository commentImageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private String dir = "/commentImage"; // 레스토랑 후기 사진폴더랑, 레스토랑 대표사진 폴더 구분하기 위해서
    private String defaultUrl = "https://ataste-bucket.s3.ap-northeast-2.amazonaws.com";

    @Autowired
    public CommentImageService(S3Uploader s3Uploader, AmazonS3Client amazonS3, CommentRepository commentRepository, CommentImageRepository commentImageRepository) {
        this.s3Uploader = s3Uploader;
        this.amazonS3 = amazonS3;
        this.commentRepository = commentRepository;
        this.commentImageRepository = commentImageRepository;
    }

    public CommentImageDto saveCommentImage(Long commentId, List<MultipartFile> files) throws IOException {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        if (optionalComment.isPresent()) {
            CommentImageDto commentImageDto = new CommentImageDto();

            for (MultipartFile file : files) {
                CommentImage commentImage = new CommentImage();
                Comment comment = optionalComment.get();

                commentImage.setComment(comment);

                String bucketDir = bucketName + dir;
                String fileName = generateFileName(file);

                amazonS3.putObject(bucketDir, fileName, file.getInputStream(), getObjectMetadata(file));

                String image = defaultUrl + dir + "/" + fileName;
                commentImage.setImage(image);

                CommentImage savedCommentImage = commentImageRepository.save(commentImage);
                commentImageDto.addImage(savedCommentImage.getImage());
            }

            return commentImageDto;
        } else {
            // Comment가 존재하지 않을 경우 예외 처리 또는 적절한 로직 추가
            throw new CommentNotFoundException("해당 후기는 존재하지 않습니다." + commentId);
        }
    }


    private String extractFileNameFromUrl(String fileUrl) { // 해당 파일의 이름을 찾아서 삭제해야하니까
        // URL에서 파일 이름 추출
        // 예: https://your-s3-bucket-url.com/your-folder/your-file.jpg
        // 파일 이름: your-file.jpg
        Pattern pattern = Pattern.compile(".*/(.+)$");
        Matcher matcher = pattern.matcher(fileUrl);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("유효하지않은 URL");
        }
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }

    public class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(String message) {
            super(message);
        }
    }

    // 이미지 수정
    public CommentImageDto updateCommentImage(Long imageId, MultipartFile file) throws IOException {
        Optional<CommentImage> optionalCommentImage = commentImageRepository.findById(imageId);

        if (optionalCommentImage.isPresent()) {
            CommentImage commentImage = optionalCommentImage.get();

            String bucketDir = bucketName + dir;
            String fileName = generateFileName(file);

            // 기존 이미지 삭제
            deleteCommentImageFromS3(commentImage.getImage());

            // 새로운 이미지 업로드
            amazonS3.putObject(bucketDir, fileName, file.getInputStream(), getObjectMetadata(file));

            // 업로드된 새로운 이미지 정보 설정
            String imageUrl = defaultUrl + dir + "/" + fileName;
            commentImage.setImage(imageUrl);

            CommentImage savedCommentImage = commentImageRepository.save(commentImage);

            CommentImageDto commentImageDto = new CommentImageDto();
            commentImageDto.setImage(savedCommentImage.getImage());

            return commentImageDto;
        } else {
            throw new CommentImageNotFoundException("해당 이미지는 존재하지 않습니다. ID: " + imageId);
        }
    }

    // 이미지 삭제
    public void deleteCommentImage(Long imageId) {
        Optional<CommentImage> optionalCommentImage = commentImageRepository.findById(imageId);

        if (optionalCommentImage.isPresent()) {
            CommentImage commentImage = optionalCommentImage.get();

            // S3에서 이미지 삭제
            deleteCommentImageFromS3(commentImage.getImage());

            // 데이터베이스에서 이미지 삭제
            commentImageRepository.delete(commentImage);
        } else {
            throw new CommentImageNotFoundException("해당 이미지는 존재하지 않습니다. ID: " + imageId);
        }
    }

    // S3에서 이미지 삭제
    private void deleteCommentImageFromS3(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        String bucketDir = bucketName + dir;

        amazonS3.deleteObject(bucketDir, fileName);
    }

    public class CommentImageNotFoundException extends RuntimeException {
        public CommentImageNotFoundException(String message) {
            super(message);
        }
    }

    public CommentImageDto getLikelyCommentImageForRestaurant(Long restaurantId) {
        List<Comment> comments = commentRepository.findByRestaurantIdOrderByLikelyDesc(restaurantId);

        if (!comments.isEmpty()) {
            Comment comment = comments.get(0); // 가장 좋아요가 많은 후기를 가져옴
            List<CommentImage> commentImages = commentImageRepository.findByCommentOrderByComment_LikelyDesc(comment);

            CommentImageDto commentImageDto = new CommentImageDto();

            if (!commentImages.isEmpty()) {
                CommentImage likelyCommentImage = commentImages.get(0); // 가장 좋아요가 많은 이미지를 가져옴
                commentImageDto.setImageUrl(likelyCommentImage.getImage());
            } else {
                // 이미지가 없는 경우에 대한 처리
                commentImageDto.setImageUrl("url_of_default_image"); // 기본 이미지 URL로 설정하거나 다른 방식으로 처리
            }

            return commentImageDto;
        } else {
            throw new EntityNotFoundException("해당 레스토랑 후기가 존재하지 않습니다. Restaurant ID: " + restaurantId);
        }
    }
}