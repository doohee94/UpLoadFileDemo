package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;

import java.util.List;

public interface FileUploadService {

    void saveFile(FileEntity fileEntity) throws Exception;
    List<FileEntity> getFileList() throws Exception;
    List<FileEntity> getAllFileList() throws Exception;
    FileEntity getFile(int fileIdx) throws Exception;
    void deleteFile(int fileIdx) throws Exception;
    void changeStatus(FileEntity fileEntity) throws Exception;
    List<FileEntity> getFileListByPersonId(int personId) throws Exception;
    List<PersonEntity> getPersonList()throws  Exception;

}
