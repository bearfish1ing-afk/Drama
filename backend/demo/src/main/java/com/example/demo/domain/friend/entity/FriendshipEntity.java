package com.example.demo.domain.friend.entity;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friend_entity")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FriendshipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="requester_id")
    private UserEntity requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver_id")
    private UserEntity receiver;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;

    private LocalDateTime createdAt;

    public void acceptRoleChange(){
        if(this.status!=FriendStatus.WAITING){
            throw new RuntimeException("대기 상태인 요청만 수락 가능");
        }
        this.status=FriendStatus.ACCEPTED;
    }

    public void rejectRoleChange(){
        this.status=FriendStatus.BLOCK;
    }
}
