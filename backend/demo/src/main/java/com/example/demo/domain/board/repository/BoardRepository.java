package com.example.demo.domain.board.repository;

import com.example.demo.domain.board.entity.BoardEntity;
import com.example.demo.domain.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity,Long> {
    @Query("SELECT b FROM BoardEntity b " +
            "WHERE b.user = :me OR b.user IN (SELECT f.receiver FROM FriendshipEntity f WHERE f.requester = :me AND f.status = 'ACCEPTED') " +
            "OR b.user IN (SELECT f.requester FROM FriendshipEntity f WHERE f.receiver = :me AND f.status = 'ACCEPTED')")
    Slice<BoardEntity> findFriendFeeds(@Param("me") UserEntity me, Pageable pageable);
}
