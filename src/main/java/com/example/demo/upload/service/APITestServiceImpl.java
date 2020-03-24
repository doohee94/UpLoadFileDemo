package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.repository.ConvertFileRepository;
import com.example.demo.upload.repository.FileUploadRepository;
import com.example.demo.upload.repository.FileUploadRepositorySupport;
import com.example.demo.upload.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class APITestServiceImpl implements APITestService{
    @Autowired
    FileUploadRepository fileUploadRepository;
    @Autowired
    ConvertFileRepository convertFileRepository;
    @Autowired
    FileUploadRepositorySupport fileUploadRepositorySupport;

    @Override
    public void saveOriginFile(FileEntity fileEntity) throws Exception {
        fileUploadRepository.save(fileEntity);
    }

    @Override
    public Page<FileEntity> getFileListByPersonId(int personId, Pageable pageable) throws Exception {
        return fileUploadRepository.findByPersonId(personId, pageable);
    }

    @Override
    public Long getConvertFileCount(int fileIdx) throws Exception{
        Long count = convertFileRepository.countByOriginFileidx(fileIdx);
        return count;
    }

    @Override
    public FileEntity getFileByFileIdx(int fileIdx) throws Exception {
        return fileUploadRepository.findByFileIdx(fileIdx);
    }

}
