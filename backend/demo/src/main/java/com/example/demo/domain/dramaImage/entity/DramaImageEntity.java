package com.example.demo.domain.dramaImage.entity;

import com.example.demo.domain.board.entity.BoardEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="drama_image_entity")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DramaImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    // setter 제거, builder에서 board를 초기화
    // 이렇게 하면 외부에서 setter 호출 안 해도 됨

    public void confirmBoard(BoardEntity board) {
        this.board = board;
    }
}

