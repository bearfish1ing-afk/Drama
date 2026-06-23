package com.example.demo.domain.elasticsearch.service;

import com.example.demo.domain.TMDB.dto.TmdbDramaRequestDTO;
import com.example.demo.domain.TMDB.service.TmdbDramaSearchService;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.elasticsearch.repository.DramaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DramaIndexService {
    private final TmdbDramaSearchService tmdbDramaSearchService;
    private final DramaRepository dramaRepository;

    public void indexIfNotExist(List<TmdbDramaRequestDTO> dtos) {
        for(TmdbDramaRequestDTO dto: dtos){
            if(!dramaRepository.existsById(dto.getId())){
                DramaDocument doc = DramaDocument.builder()
                        .id(dto.getId().longValue())
                        .name(dto.getName())
                        .overview(dto.getOverview())
                        .first_air_date(dto.getFirst_air_date())
                        .posterPath(dto.getPosterPath())
                        .build();
                dramaRepository.save(doc);
            }
        }
    }
}
