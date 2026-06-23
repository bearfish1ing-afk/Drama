package com.example.demo.domain.user.service;

import com.example.demo.domain.user.dto.UserRequestDTO;
import com.example.demo.domain.user.entity.UserEntity;
import com.example.demo.domain.user.entity.UserRoleType;
import com.example.demo.domain.user.repostiroy.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //자체 로그인 회원가입(존재 여부)
    @Transactional(readOnly = true)
    public Boolean existUser(UserRequestDTO dto){
        return userRepository.existsByUsername(dto.getUsername());
    }

    //로그인 회원가입
    @Transactional
    public Long addUser(UserRequestDTO dto){
        if(userRepository.existsByUsername(dto.getUsername())){
            throw new IllegalArgumentException("이미 유저가 존재합니다.");
        }

        UserEntity entity=UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .isLock(false)
                .roleType(UserRoleType.USER)
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();

        return userRepository.save(entity).getId();
    }

    //로그인
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
        UserEntity entity=userRepository.findByUsernameAndIsLock(username,false)
                .orElseThrow(()->new UsernameNotFoundException(username));

        return User.builder()
                .username(entity.getUsername())
                .password(entity.getPassword())
                .roles(entity.getRoleType().name())
                .accountLocked(entity.getIsLock())
                .build();
    }

    //정보수정
    @Transactional
    public Long updateUser(UserRequestDTO dto) throws AccessDeniedException {
        String sessionUsername= SecurityContextHolder.getContext().getAuthentication().getName();
        if(!sessionUsername.equals(dto.getUsername())){
            throw new AccessDeniedException("본인 계정만 수정가능");
        }

        UserEntity entity=userRepository.findByUsernameAndIsLock(dto.getUsername(),false)
                .orElseThrow(()->new UsernameNotFoundException(dto.getUsername()));

        entity.updateUser(dto);

        return userRepository.save(entity).getId();
    }

    //탈퇴
    @Transactional
    public void deleteUser(UserRequestDTO dto) throws AccessDeniedException {
        String sessionUsername= SecurityContextHolder.getContext().getAuthentication().getName();
        String sessionRole=SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        boolean isOwner=sessionUsername.equals(dto.getUsername());
        boolean isAdmin=sessionRole.equals(UserRoleType.ADMIN);

        if(!isOwner&&!isAdmin){
            throw new AccessDeniedException("본인 혹은 관리자만 삭제할 수 있습니다.");
        }

        // 유저 제거
        userRepository.deleteByUsername(dto.getUsername());

        // Refresh 토큰 제거
        //jwtService.removeRefreshUSer(dto.getUsername());
    }
}
