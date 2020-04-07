package com.example.demo.upload.repository;

import com.example.demo.upload.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileUploadRepository extends JpaRepository<FileEntity, Integer> {

    FileEntity findByFileFakeName(String fileFakeName);
    Page<FileEntity> findByPersonId(int personId,Pageable pageable);
    FileEntity findByFileIdx(int fileIdx);
}
