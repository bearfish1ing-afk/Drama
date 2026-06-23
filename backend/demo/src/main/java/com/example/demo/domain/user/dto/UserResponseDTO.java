package com.example.demo.domain.user.dto;

public record UserResponseDTO(String username, String nickname, String email) {
}
//record는 class와 다르게 답만 담는다