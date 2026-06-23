package com.example.demo.domain.friend.elasticsearch.service;

import com.example.demo.domain.TMDB.dto.TmdbDramaRequestDTO;
import com.example.demo.domain.TMDB.service.TmdbDramaSearchService;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.elasticsearch.repository.DramaRepository;
import com.example.demo.domain.friend.elasticsearch.entity.IdDocument;
import com.example.demo.domain.friend.elasticsearch.repository.IdRepository;
import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdIndexService {
    private final UserService userService;
    private final IdRepository idRepository;

    public void indexIfNotExist(List<UserRequestDTO> dtos) {
        for(UserRequestDTO dto: dtos){
            if(!idRepository.existsByUsername((dto.getUsername()))){
                IdDocument doc=IdDocument.builder()
                        .id(String.valueOf(dto.getId()))
                        .username(dto.getUsername())
                        .build();
                idRepository.save(doc);
            }
        }
    }
}

