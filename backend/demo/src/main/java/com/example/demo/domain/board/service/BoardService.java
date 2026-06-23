package com.example.demo.domain.board.service;

import com.example.demo.domain.board.dto.BoardRequestDTO;
import com.example.demo.domain.board.dto.BoardResponseDTO;
import com.example.demo.domain.board.dto.BoardUpDateRequestDTO;
import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.board.repository.BoardRepository;
import com.example.demo.domain.dramaImage.dto.DramaImageRequestDTO;
import com.example.demo.domain.dramaImage.entity.DramaImageEntity;
import com.example.demo.domain.dramaImage.repository.DramaImageRepository;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import com.example.demo.domain.elasticsearch.service.DramaSearchService;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.repostiroy.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final DramaSearchService dramaSearchService;
    private final DramaImageRepository dramaImageRepository;
    private final UserRepository userRepository;

    //게시물 올리기
    @Transactional
    public Long uploadBoard(String username,BoardRequestDTO dto){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<DramaDocument> dramaCh=dramaSearchService.searchByTmdbId(dto.getTmdbDramaId());

        DramaDocument drama=dramaCh
                .orElseThrow(() -> new EntityNotFoundException("Drama Not Found"));

        BoardEntity entity = BoardEntity.builder()
                .user(user)
                .tmdbDramaId(drama.getId())
                .tmdbTitle(drama.getName())
                .tmdbPosterUrl(drama.getPosterPath())
                .tmdbOverview(drama.getOverview())
                .content(dto.getContent())
                .build();

        boardRepository.save(entity);

        if(dto.getImageIds()!=null&&!dto.getImageIds().isEmpty()){
            List<DramaImageEntity> images=dramaImageRepository.findAllById(dto.getImageIds());

            for(DramaImageEntity image:images){
                entity.addImage(image);
            }
        }

        return entity.getId();
    }

    //게시물 수정하기
    @Transactional
    public void updateBoard(Long boardId, BoardUpDateRequestDTO dto){
        BoardEntity entity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        entity.update(dto.getContent());

        if(dto.getImageIds()!=null){
            List<DramaImageEntity> images=dramaImageRepository.findAllById(dto.getImageIds());

            entity.getImages().clear();
            for(DramaImageEntity image:images){
                entity.addImage(image);
            }
        }
    }//image 다 삭제한다-> image중 내가 지울려고한 것만 front에서 제외하고 list에 넣는다

    //추가
    @Transactional
    public void addBoardImage(Long boardId, DramaImageRequestDTO dto) {
        /*Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        Long userId= (Long) auth.getPrincipal();*/

        BoardEntity entity=boardRepository.findById(boardId)
                .orElseThrow(()->new EntityNotFoundException("게시판을 찾지 못했습니다."));

        //userId 확인

        DramaImageEntity imageEntity=DramaImageEntity.builder()
                .imageUrl(dto.getImageUrl())
                .board(entity)
                .build();

        entity.addImage(imageEntity);
    }

    //image 삭제
    @Transactional
    public void deleteBoardImage(Long boardId,Long imageId){
        //user 확인
        BoardEntity entity=boardRepository.findById(boardId)
                .orElseThrow(()->new RuntimeException("게시판이 없습니다"));

        entity.getImages().removeIf(img->img.getId()==imageId);
    }

    //게시물 삭제하기
    @Transactional
    public void deleteBoard(Long boardId) {
        BoardEntity entity = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        boardRepository.delete(entity);
    }

    // 상세 조회
    @Transactional(readOnly = true)
    public BoardResponseDTO getBoard(Long boardId) {
        BoardEntity entity=boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("Board not found"));

        return BoardResponseDTO.from(entity);
    }

    // 목록 조회
    @Transactional(readOnly = true)
    public Slice<BoardResponseDTO> getBoards(String myUsername, Pageable pageable) {
        UserEntity me=userRepository.findByUsername(myUsername)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        Slice<BoardEntity> boardSlice=boardRepository.findFriendFeeds(me,pageable);

        return boardSlice.map(BoardResponseDTO::from);
    }
}
