package com.example.demo.api;

import com.example.demo.domain.TMDB.dto.TmdbDramaRequestDTO;
import com.example.demo.domain.TMDB.service.TmdbDramaSearchService;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.elasticsearch.service.DramaIndexService;
import com.example.demo.domain.elasticsearch.service.DramaSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drama")
public class DramaSearchController {
    private final TmdbDramaSearchService tmdbDramaService;
    private final DramaSearchService dramaSearchService;
    private final DramaIndexService dramaIndexService;

    @GetMapping("/search")
    public ResponseEntity<List<DramaDocument>> searchEsApi(@RequestParam String keyword){
        if(keyword==null || keyword.isBlank()){
            return ResponseEntity.badRequest().build();
        }

        List<DramaDocument> results=dramaSearchService.searchDramaNameFuzzy(keyword);

        if(results.isEmpty()){
            List<TmdbDramaRequestDTO> tmdbResults=tmdbDramaService.searchDrama(keyword);
            dramaIndexService.indexIfNotExist(tmdbResults);
            results=dramaSearchService.searchDramaNameFuzzy(keyword);
        }
        return ResponseEntity.ok(results);
    }
}
