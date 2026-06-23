package com.example.demo.domain.board.dto;

import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.dramaImage.dto.DramaImageResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record BoardResponseDTO(
        long boardId,
        long userId,
        String nickname,
        long tmdbDramaId,
        String tmdbTitle,
        String tmdbPosterUrl,
        String tmdbOverview,
        String content,
        //String author
        LocalDateTime createdAt,
        LocalDateTime updated,
        List<DramaImageResponseDTO> images
) {
    public static BoardResponseDTO from(BoardEntity entity) {
        return new BoardResponseDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getNickname(),
                entity.getTmdbDramaId(),
                entity.getTmdbTitle(),
                entity.getTmdbPosterUrl(),
                entity.getTmdbOverview(),
                entity.getContent(),
                entity.getCreatedDate(),
                entity.getUpdatedDate(),
                entity.getImages().stream()
                        .map(DramaImageResponseDTO::from)
                        .toList()
        );
    }
}
