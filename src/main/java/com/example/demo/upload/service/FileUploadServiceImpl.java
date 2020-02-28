package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.repository.FileUploadRepository;
import com.example.demo.upload.repository.FileUploadRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    @Autowired
    FileUploadRepository fileUploadRepository;

    @Autowired
    FileUploadRepositorySupport fileUploadRepositorySupport;

    @Override
    public void saveFile(FileEntity fileEntity) throws Exception {
        fileUploadRepository.save(fileEntity);
    }

    @Override
    public List<FileEntity> getFileList() throws Exception {
        List<FileEntity> test = fileUploadRepository.findBySaveStatus(SaveStatus.UPLOADED);
        return test;
    }

    @Override
    public FileEntity getFile(int fileIdx) throws Exception {

        Optional<FileEntity> optional = fileUploadRepository.findById(fileIdx);
        FileEntity fileEntity = null;
        if(optional.isPresent()){
            fileEntity = optional.get();
            return fileEntity;
        }else{
            throw new NullPointerException();
        }


    }

    @Override
    public void deleteFile(int fileIdx) throws Exception {
        fileUploadRepository.deleteById(fileIdx);
    }

    @Override
    public void changeStatus(FileEntity fileEntity) throws Exception {
        fileUploadRepository.save(fileEntity);
    }
}
