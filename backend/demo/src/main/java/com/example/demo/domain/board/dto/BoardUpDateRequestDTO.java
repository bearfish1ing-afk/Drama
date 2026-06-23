package com.example.demo.domain.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardUpDateRequestDTO {
    private String content;
    private List<Long> imageIds;
}
