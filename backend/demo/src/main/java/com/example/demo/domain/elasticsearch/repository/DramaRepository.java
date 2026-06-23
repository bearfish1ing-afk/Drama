package com.example.demo.domain.elasticsearch.repository;

import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DramaRepository extends ElasticsearchRepository<DramaDocument,Long> {
}
