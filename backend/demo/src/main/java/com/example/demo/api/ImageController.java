package com.example.demo.api;

import com.example.demo.domain.dramaImage.Service.DramaImageService;
import com.example.demo.domain.dramaImage.dto.DramaImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final DramaImageService  dramaImageService;

    @PostMapping
    public ResponseEntity<DramaImageResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {
        try{
            DramaImageResponseDTO response=dramaImageService.uploadImage(file);
            return ResponseEntity.ok(response);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
