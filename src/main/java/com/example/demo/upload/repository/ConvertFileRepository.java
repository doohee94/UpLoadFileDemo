package com.example.demo.upload.repository;

import com.example.demo.upload.entity.ConvertFileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvertFileRepository extends JpaRepository<ConvertFileEntity,Integer> {
    Long countByOriginFileidx(int fileIdx);
    Page<ConvertFileEntity> findAllByOriginFileidx(int fileIdx, Pageable pageable);
    ConvertFileEntity findByFileIdx(int fileIdx);
}
