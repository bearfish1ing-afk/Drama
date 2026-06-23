package com.example.demo.domain.friend.repository;

import com.example.demo.domain.friend.entity.FriendshipEntity;
import com.example.demo.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendShipRepository extends JpaRepository<FriendshipEntity,Long> {
    @Query("SELECT f FROM FriendshipEntity f " +
            "JOIN FETCH f.requester " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.requester = :user OR f.receiver = :user) " +
            "AND f.status = 'ACCEPTED'")
    List<FriendshipEntity> findFollowFriends(@Param("user") UserEntity user);

    @Query("SELECT f FROM FriendshipEntity f " +
            "JOIN FETCH f.requester " +
            "JOIN FETCH f.receiver " +
            "WHERE (f.requester = :user OR f.receiver = :user) " +
            "AND f.status = 'ACCEPTED' OR f.status='WAITING'")
    List<FriendshipEntity> findAllFriend(@Param("user") UserEntity user);

    // 또는 좀 더 유연하게 (A-B 혹은 B-A 관계 모두 포함)
    @Query("SELECT f FROM FriendshipEntity f WHERE " +
            "(f.requester.id = :id1 AND f.receiver.id = :id2) OR " +
            "(f.requester.id = :id2 AND f.receiver.id = :id1) " +
            "ORDER BY f.createdAt DESC LIMIT 1") // 가장 최근 상태 1개만 가져옴
    Optional<FriendshipEntity> findRelationById(@Param("id1") Long id1, @Param("id2") Long id2);
    Optional<FriendshipEntity> findByRequesterAndReceiver(UserEntity req, UserEntity rec);
}
