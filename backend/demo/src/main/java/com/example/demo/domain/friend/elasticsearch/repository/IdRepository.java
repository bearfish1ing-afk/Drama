package com.example.demo.domain.friend.elasticsearch.repository;

import com.example.demo.domain.friend.elasticsearch.entity.IdDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IdRepository extends ElasticsearchRepository<IdDocument,Long> {
    boolean existsByUsername(String username);
}
