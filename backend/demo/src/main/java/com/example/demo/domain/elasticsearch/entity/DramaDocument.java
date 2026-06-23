package com.example.demo.domain.elasticsearch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Document(indexName = "drama")
public class DramaDocument {
    @Id
    private Long id;
    private String name;
    private String overview;
    private String first_air_date;
    private String posterPath;

    @Builder
    public DramaDocument(Long id, String name, String overview, String first_air_date, String posterPath) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.first_air_date = first_air_date;
        this.posterPath = posterPath;
    }
}
