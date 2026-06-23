package com.example.demo.domain.board.entity;

import com.example.demo.domain.dramaImage.dto.DramaImageRequestDTO;
import com.example.demo.domain.dramaImage.entity.DramaImageEntity;
import com.example.demo.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="board_entity")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    // TMDB 연동
    private Long tmdbDramaId;      // 필수 저장
    private String tmdbTitle;      // 선택적 캐싱
    private String tmdbPosterUrl;  // 선택적 캐싱

    @Column(columnDefinition = "TEXT")
    private String tmdbOverview;

    private String keyword;

    @Column(columnDefinition = "TEXT")
    private String content;
    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity author;

    /*public boolean isOwner(String username) {
        return this.author.getUsername().equals(username);
    }*/

    @Builder.Default//리스트가 null이 되지 않도록
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DramaImageEntity> images = new ArrayList<>();

    public void addImage(DramaImageEntity image){
        if(this.images==null){
            this.images = new ArrayList<>();
        }
        this.images.add(image);

        image.confirmBoard(this);
    }

    public void removeImage(DramaImageEntity image){
        images.remove(image);
    }

    public void update(String content){
        this.content = content;
    }
}
