package com.example.demo.domain.dramaImage.dto;

import com.example.demo.domain.board.dto.BoardUpDateResponseDTO;
import com.example.demo.domain.dramaImage.entity.DramaImageEntity;

public record DramaImageResponseDTO(
        Long id,
        String imageUrl
) {
    public static DramaImageResponseDTO from(DramaImageEntity image) {
        return new DramaImageResponseDTO(
                image.getId(),
                image.getImageUrl()
        );
    }
}
