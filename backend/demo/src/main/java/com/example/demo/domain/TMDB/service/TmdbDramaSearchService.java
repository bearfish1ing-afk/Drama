package com.example.demo.domain.TMDB.service;

import com.example.demo.domain.TMDB.dto.TmdbDramaRequestDTO;
import com.example.demo.domain.TMDB.dto.TmdbDramaResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TmdbDramaSearchService {
    private final WebClient webClient;
    private final String apiKey;

    public TmdbDramaSearchService(
            @Value("${tmdb.api-key}") String apiKey,
            @Value("${tmdb.base-url}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<TmdbDramaRequestDTO> searchDrama(String keyword) {
        TmdbDramaResponseDTO response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/tv")
                        .queryParam("query", keyword)
                        .queryParam("include_adult", false)
                        .queryParam("language", "ko-KR")
                        .queryParam("page", 1)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TmdbDramaResponseDTO.class)
                .block();

        if (response == null || response.getResults() == null) {
            return List.of();
        }
        return response.getResults();

    }
}