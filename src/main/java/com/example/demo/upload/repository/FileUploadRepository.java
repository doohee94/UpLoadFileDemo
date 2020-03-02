package com.example.demo.upload.repository;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;
import com.example.demo.upload.entity.SaveStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileUploadRepository extends JpaRepository<FileEntity, Integer> {
    List<FileEntity> findBySaveStatus(SaveStatus saveStatus, Pageable pageable);
}
