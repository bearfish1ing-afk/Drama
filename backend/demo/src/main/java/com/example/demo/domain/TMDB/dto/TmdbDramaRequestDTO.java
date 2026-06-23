package com.example.demo.domain.TMDB.dto;

import lombok.Data;

@Data
public class TmdbDramaRequestDTO {
    private Long id;
    private String name;
    private String overview;
    private String first_air_date;
    private String posterPath;
}
