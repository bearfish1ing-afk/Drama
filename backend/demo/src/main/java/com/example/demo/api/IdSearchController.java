package com.example.demo.api;

import com.example.demo.domain.TMDB.dto.TmdbDramaRequestDTO;
import com.example.demo.domain.TMDB.service.TmdbDramaSearchService;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.elasticsearch.service.DramaIndexService;
import com.example.demo.domain.elasticsearch.service.DramaSearchService;
import com.example.demo.domain.friend.elasticsearch.entity.IdDocument;
import com.example.demo.domain.friend.elasticsearch.service.IdIndexService;
import com.example.demo.domain.friend.elasticsearch.service.IdSearchService;
import com.example.demo.domain.friend.entity.FriendshipEntity;
import com.example.demo.domain.friend.repository.FriendShipRepository;
import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.repostiroy.UserRepository;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/id")
public class IdSearchController {
    private final IdSearchService idSearchService;
    private final IdIndexService idIndexService;
    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;

    private String getFriendshipStatus(Long myId, Long targetId){
        return friendShipRepository.findRelationById(myId,targetId)
                .map(f->f.getStatus().name())
                .orElse("NONE");
    }

    @GetMapping("/search")
    public ResponseEntity<List<IdDocument>> searchIdApi(@RequestParam String keyword,@RequestHeader("Authorization") String accessToken) {
        String token=accessToken;
        if(accessToken!=null&&accessToken.startsWith("Bearer ")){
            token=accessToken.substring(7);
        }
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        List<IdDocument> results = idSearchService.searchId(keyword,token);
        String myName = JWTUtil.getUsername(token);
        UserEntity me=userRepository.findByUsername(myName)
                .orElseThrow(()->new RuntimeException("내 정보를 찾을 수 없습니다."));

        if(!results.isEmpty()){
            results.forEach(doc->{
                String status=getFriendshipStatus(me.getId(),Long.parseLong(doc.getId()));
                doc.setFriendshipStatus(status);
            });
            return ResponseEntity.ok(results);
        }

        if (results.isEmpty()) {
            Optional<UserEntity> userOpt = userRepository.findByUsername(keyword);

            if (userOpt.isPresent()) {
                UserEntity u = userOpt.get();

                if (u.getUsername().equals(myName)) {
                    return ResponseEntity.ok(List.of());
                }

                String current=getFriendshipStatus(me.getId(),u.getId());

                // DTO 생성 (ID 누락되지 않게 주의!)
                UserRequestDTO dto = new UserRequestDTO();
                dto.setId(u.getId());
                dto.setUsername(u.getUsername());

                // Elasticsearch에 저장 요청 (백그라운드에서 진행)
                idIndexService.indexIfNotExist(List.of(dto));

                // 3. 핵심: 다시 검색하지 말고, DB에서 찾은 정보를 바로 리스트에 담아 응답!
                IdDocument newlyIndexed = IdDocument.builder()
                        .id(String.valueOf(u.getId()))
                        .username(u.getUsername())
                        .friendshipStatus(current)
                        .build();

                return ResponseEntity.ok(List.of(newlyIndexed));
            }

            System.out.println("검색 keyword = " + keyword);
            System.out.println("조회 결과 존재 여부 = " + userOpt.isPresent());
        }
        return ResponseEntity.ok(List.of());
    }
}
