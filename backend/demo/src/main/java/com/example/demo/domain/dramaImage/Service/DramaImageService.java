package com.example.demo.domain.dramaImage.Service;

import com.example.demo.domain.dramaImage.dto.DramaImageResponseDTO;
import com.example.demo.domain.dramaImage.entity.DramaImageEntity;
import com.example.demo.domain.dramaImage.repository.DramaImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DramaImageService {
    private final DramaImageRepository dramaImageRepository;

    private final Path rootPath=Paths.get("uploads");

    public DramaImageResponseDTO uploadImage(MultipartFile file) throws IOException {
        if(!Files.exists(rootPath)){
            Files.createDirectory(rootPath);
        }

        // 2. 파일명 중복 방지를 위해 UUID 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        // 3. 실물 파일을 'uploads' 폴더로 복사 (저장)/서버 컴퓨터 물리적저장
        Files.copy(file.getInputStream(), rootPath.resolve(fileName));
        
        //front에서 가짜주소
        String imageUrl = "http://localhost:8080/uploads/" + fileName;

        DramaImageEntity entity=DramaImageEntity.builder()
                .imageUrl(imageUrl)
                .build();
        DramaImageEntity savedEntity=dramaImageRepository.save(entity);

        return DramaImageResponseDTO.from(savedEntity);
    }

}
