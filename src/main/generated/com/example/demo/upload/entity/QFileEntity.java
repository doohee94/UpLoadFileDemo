package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFileEntity extends EntityPathBase<FileEntity> {

    private static final long serialVersionUID = 388096599L;

    public static final QFileEntity fileEntity = new QFileEntity("fileEntity");

    public final StringPath attachmentUrl = createString("attachmentUrl");

    public final StringPath fileContentType = createString("fileContentType");

    public final StringPath fileFakeName = createString("fileFakeName");

    public final NumberPath<Integer> fileIdx = createNumber("fileIdx", Integer.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final EnumPath<SaveStatus> saveStatus = createEnum("saveStatus", SaveStatus.class);

    public QFileEntity(String variable) {
        super(FileEntity.class, forVariable(variable));
    }

    public QFileEntity(Path<? extends FileEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileEntity(PathMetadata metadata) {
        super(FileEntity.class, metadata);
    }

}

