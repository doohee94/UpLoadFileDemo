package com.example.demo.upload.repository;

import com.example.demo.upload.entity.FileEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileUploadRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public FileUploadRepositorySupport(JPAQueryFactory queryFactory){
        super(FileEntity.class);
        this.queryFactory = queryFactory;
    }

//    public void Test(int fileIdx){
//
//        List<FileEntity> list = queryFactory.selectFrom(fileEntity).where(fileEntity.fileIdx.eq(fileIdx).and(fileEntity.statusSave.eq("UPLODED")));
//
//    }



}
