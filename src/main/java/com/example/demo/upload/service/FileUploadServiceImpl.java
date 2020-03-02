package com.example.demo.upload.service;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.PersonEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.example.demo.upload.repository.FileUploadRepository;
import com.example.demo.upload.repository.FileUploadRepositorySupport;
import com.example.demo.upload.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileUploadServiceImpl implements FileUploadService{

    @Autowired
    FileUploadRepository fileUploadRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    FileUploadRepositorySupport fileUploadRepositorySupport;

    @Override
    public void saveFile(FileEntity fileEntity) throws Exception {
        fileUploadRepository.save(fileEntity);
    }

        @Override
    public Page<FileEntity> getFileList(Pageable pageable) throws Exception {
        Page<FileEntity> test = fileUploadRepository.findBySaveStatus(SaveStatus.UPLOADED,pageable);
        return test;
    }

    @Override
    public List<FileEntity> getAllFileList() throws Exception {
        return fileUploadRepository.findAll();
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

    @Override
    public List<FileEntity> getFileListByPersonId(int personId) throws Exception {
        Optional<PersonEntity> optional = personRepository.findById(personId);
        PersonEntity personEntity =optional.get();
        List<FileEntity> list = personEntity.getFileEntity();
        return list;
    }

    @Override
    public List<PersonEntity> getPersonList() throws Exception {
        return personRepository.findAll();
    }


}
