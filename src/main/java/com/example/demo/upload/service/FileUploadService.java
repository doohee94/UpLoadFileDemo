package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FileUploadService {

    void saveFile(FileEntity fileEntity) throws Exception;
    //List<FileEntity> getFileList(Pageable pageable) throws Exception;
    Page<FileEntity> getFileList(Pageable pageable) throws Exception;
    List<FileEntity> getAllFileList() throws Exception;
    FileEntity getFile(int fileIdx) throws Exception;
    void deleteFile(int fileIdx) throws Exception;
    void changeStatus(FileEntity fileEntity) throws Exception;
    List<FileEntity> getFileListByPersonId(int personId) throws Exception;
    List<PersonEntity> getPersonList()throws  Exception;
    FileEntity getFileByFileFakeName(String fileName)throws Exception;
}
