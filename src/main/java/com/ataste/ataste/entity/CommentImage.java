package com.ataste.ataste.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "comment_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column// 이미지 URL을 저장
    private String image; // comment_id가 같으면 하나의 후기에 여러가지 사진이 존재하는 것
}
