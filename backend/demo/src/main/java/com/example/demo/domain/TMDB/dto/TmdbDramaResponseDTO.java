package com.example.demo.domain.TMDB.dto;

import lombok.Data;

import java.util.List;

@Data
public class TmdbDramaResponseDTO {
    private int page;
    private List<TmdbDramaRequestDTO> results;
}
