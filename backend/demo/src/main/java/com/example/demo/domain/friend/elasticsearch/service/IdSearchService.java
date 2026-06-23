package com.example.demo.domain.friend.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.friend.elasticsearch.entity.IdDocument;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<IdDocument> searchId(String username,String accessToken){
        String myName=JWTUtil.getUsername(accessToken);

        Query query=QueryBuilders.bool(b->b
                .must(m->m.wildcard(w->w
                        .field("username")
                        .value("*" + username.toLowerCase() + "*")
                        .caseInsensitive(true)
                ))
                .mustNot(mn->mn.match(mt->mt
                        .field("username")
                        .query(myName)
                ))
        );
        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(query).build();

        //검색 실행
        SearchHits<IdDocument> searchHits=elasticsearchOperations.search(nativeQuery, IdDocument.class);

        return searchHits.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    //front에서 누른 후 시행
    public Optional<IdDocument> searchByUserId(String id){
        IdDocument document=elasticsearchOperations.get(id, IdDocument.class);
        return Optional.ofNullable(document);
    }
}
