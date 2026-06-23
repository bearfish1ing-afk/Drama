package com.example.demo.domain.friend.dto;

import java.time.LocalDateTime;

public record FriendResponseDTO(
        Long friendshipId,
        String friendNickname,
        String status,
        LocalDateTime createdAt
) {
}
