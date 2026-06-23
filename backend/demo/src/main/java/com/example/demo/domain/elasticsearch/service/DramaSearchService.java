package com.example.demo.domain.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DramaSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<DramaDocument> searchDramaNameFuzzy(String name){
        Query query= QueryBuilders.match(m->m
                .field("name")
                .query(name)
                .fuzziness("AUTO")
        );

        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(query).build();
        
        //검색 실행
        SearchHits<DramaDocument> searchHits= elasticsearchOperations.search(nativeQuery, DramaDocument.class);

        //결과 변환
        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    //front에서 누른 후 시행
    public Optional<DramaDocument> searchByTmdbId(Long id){
        DramaDocument document=elasticsearchOperations.get(String.valueOf(id), DramaDocument.class);
        return Optional.ofNullable(document);
    }
}

