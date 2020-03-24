package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface APITestService{
    void saveOriginFile(FileEntity fileEntity) throws Exception;
    Page<FileEntity> getFileListByPersonId(int personId, Pageable pageable) throws Exception;
    Long getConvertFileCount(int fileIdx) throws Exception;
    FileEntity getFileByFileIdx(int fileIdx) throws Exception;
}
