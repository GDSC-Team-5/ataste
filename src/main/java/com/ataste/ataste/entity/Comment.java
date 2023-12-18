package com.ataste.ataste.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // Member 엔티티와의 관계

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurants restaurant; // Restaurants 엔티티와의 관계

    @Column
    private String title;

    @Column
    private String content; // 후기

    // 일대다 => 후기에는 여러 사진이 연결 될 수 있기에 -> 이건 후기에 첨부되는 사진들이다.
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentImage> images = new ArrayList<>();

    @UpdateTimestamp // 후기 작성할때 시간 저장 + update시 자동으로 시간 변경
    @Column
    private Date creDate;

    @Column
    private int grade; // 평점 (1~5)

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Long likely; // 후기에 좋아요 누름 + 1
}
