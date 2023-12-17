package com.ataste.ataste.dto;

import java.util.ArrayList;
import java.util.List;
public class CommentImageDto {
    private Long id;
    private Long comment_id;
    private List<String> images;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CommentImageDto() {
        this.images = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Long getComment_id() {
        return comment_id;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImage(String image) {
        this.images.add(image);
    }

    public void addImage(String image) {
        this.images.add(image);
    }
}
