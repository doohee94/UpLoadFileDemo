package com.example.demo.upload.repository;

import com.example.demo.upload.entity.FileEntity;
import com.example.demo.upload.entity.QFileEntity;
import com.example.demo.upload.entity.SaveStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.demo.upload.entity.QFileEntity.fileEntity;

@Repository
public class FileUploadRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public FileUploadRepositorySupport(JPAQueryFactory queryFactory){
        super(FileEntity.class);
        this.queryFactory = queryFactory;
    }



}
