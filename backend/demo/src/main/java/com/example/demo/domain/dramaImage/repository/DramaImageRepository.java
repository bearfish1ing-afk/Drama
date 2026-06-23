package com.example.demo.domain.dramaImage.repository;

import com.example.demo.domain.dramaImage.entity.DramaImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DramaImageRepository extends JpaRepository<DramaImageEntity,Long> {
}
