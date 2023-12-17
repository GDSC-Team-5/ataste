package com.ataste.ataste.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
public class CommentDto {
    private Long id;
    private Long memberId;
    private Long restaurant_id;
    private String title;
    private String content;
    private List<MultipartFile> images;
    private int grade;
    private Long likely;
    private LocalDateTime regdate;

    public LocalDateTime getRegdate(){return regdate;}


    public Long getId() {return id;}
    public Long getMemberId() {return memberId;}

    public Long getRestaurantId() {
        return restaurant_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public void setLikely(Long likely) {this.likely =likely;}

    public Long getLikely(){return  likely;}
}
