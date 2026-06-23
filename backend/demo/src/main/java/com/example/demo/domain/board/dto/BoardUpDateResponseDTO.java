package com.example.demo.domain.board.dto;

import com.example.demo.domain.dramaImage.dto.DramaImageResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record BoardUpDateResponseDTO(
        long tmdbDramaId,
        String content,
        //String author
        LocalDateTime updated
) {
}
