package com.example.demo.domain.jwt.service;

import com.example.demo.domain.jwt.dto.JWTResponseDTO;
import com.example.demo.domain.jwt.dto.RefreshRequestDTO;
import com.example.demo.domain.jwt.entity.RefreshEntity;
import com.example.demo.domain.jwt.repository.RefreshRepository;
import com.example.demo.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RefreshRepository refreshRepository;

    @Transactional
    public JWTResponseDTO refreshRotate(RefreshRequestDTO dto){
        String refreshToken = dto.getRefreshToken();

        Boolean isValid= JWTUtil.isValid(refreshToken,false);
        if (!isValid) {
            throw new RuntimeException("유효하지 않는 refreshToken입니다.");
        }

        if(!refreshRepository.existsByRefresh(refreshToken)){
            throw new RuntimeException("유효하지 않은 refreshToken입니다.");
        }

        String username=JWTUtil.getUsername(refreshToken);
        String role=JWTUtil.getRole(refreshToken);

        String newAccessToken=JWTUtil.createJWT(username,role,true);
        String newRefreshToken=JWTUtil.createJWT(username,role,false);

        RefreshEntity newRefreshEntity=RefreshEntity.builder()
                .username(username)
                .refresh(newRefreshToken)
                .build();
        removeRefresh(refreshToken);
        refreshRepository.save(newRefreshEntity);

        return new JWTResponseDTO(newAccessToken,newRefreshToken);
    }

    @Transactional
    public void addRefresh(String username, String refreshToken){
        RefreshEntity entity=RefreshEntity.builder()
                .username(username)
                .refresh(refreshToken)
                .build();
        refreshRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Boolean existsByRefresh(String refreshToken){
        return refreshRepository.existsByRefresh(refreshToken);
    }

    public void removeRefresh(String refreshToken){
        refreshRepository.deleteByRefresh(refreshToken);
    }

    public void removeRefreshUser(String username){
        refreshRepository.deleteByUsername(username);
    }
}
