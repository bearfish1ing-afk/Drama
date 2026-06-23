package com.example.demo.domain.user.repostiroy;

import com.example.demo.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Boolean existsByUsername(String username);
    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.isLock = :isLock")
    Optional<UserEntity> findByUsernameAndIsLockAndIsSocial(
            @Param("username") String username,
            @Param("isLock") Boolean isLock
    );

    @Transactional
    void deleteByUsername(String username);

    Optional<UserEntity> findByUsernameAndIsLock(String username, Boolean isLock);

    //Optional<Object> findByUsername(String username);
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findEntityByUsername(String username);

}
