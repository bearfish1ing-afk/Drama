package com.example.demo.api;

import com.example.demo.domain.board.dto.BoardRequestDTO;
import com.example.demo.domain.board.dto.BoardResponseDTO;
import com.example.demo.domain.board.dto.BoardUpDateRequestDTO;
import com.example.demo.domain.board.dto.BoardUpDateResponseDTO;
import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.board.repository.BoardRepository;
import com.example.demo.domain.board.service.BoardService;
import com.example.demo.domain.dramaImage.dto.DramaImageRequestDTO;
import com.example.demo.domain.dramaImage.entity.DramaImageEntity;
import com.example.demo.domain.elasticsearch.entity.DramaDocument;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    //게시물 올리기
    @PostMapping()
    public ResponseEntity<Map<String,Long>> uploadBoardApi(@AuthenticationPrincipal String username,@RequestBody BoardRequestDTO dto){
        Long id=boardService.uploadBoard(username,dto);
        Map<String,Long> responseBody= Collections.singletonMap("board",id);
        return ResponseEntity.ok(responseBody);
    }

    //게시물 수정하기
    @PutMapping("/{boardId}/update")
    public ResponseEntity<Void> updateBoardApi(@PathVariable Long boardId, @RequestBody BoardUpDateRequestDTO dto){
        boardService.updateBoard(boardId, dto);
        return ResponseEntity.noContent().build();
    }

    //게시물 삭제하기
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoardApi(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    // 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDTO> getBoardApi(@PathVariable Long boardId){
        return ResponseEntity.ok(boardService.getBoard(boardId));
    }

    // 목록 조회
    @GetMapping()
    public ResponseEntity<Slice<BoardResponseDTO>> getAllBoardsApi(
            @AuthenticationPrincipal String username,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(boardService.getBoards(username, pageable));
    }

}
