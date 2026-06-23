package com.example.demo.domain.user.entity;

import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.friend.entity.FriendshipEntity;
import com.example.demo.domain.user.dto.UserRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="user_user_entity")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username",unique=true,nullable = false,updatable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @CreatedDate
    @Column(name="created_date",updatable=false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;//수정일을 알려준다

    // 내가 보낸 친구 신청 목록
    @OneToMany(mappedBy = "requester")
    private List<FriendshipEntity> sentFriendRequests = new ArrayList<>();

    // 내가 받은 친구 신청 목록
    @OneToMany(mappedBy = "receiver")
    private List<FriendshipEntity> receivedFriendRequests = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BoardEntity>boards=new ArrayList<>();

    public void updateUser(UserRequestDTO dto){
        this.email=dto.getEmail();
        this.nickname=dto.getNickname();
    }
}