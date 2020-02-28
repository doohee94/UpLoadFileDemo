package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QFileEntity extends EntityPathBase<FileEntity> {

    private static final long serialVersionUID = 388096599L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFileEntity fileEntity = new QFileEntity("fileEntity");

    public final StringPath attachmentUrl = createString("attachmentUrl");

    public final StringPath fileContentType = createString("fileContentType");

    public final StringPath fileFakeName = createString("fileFakeName");

    public final NumberPath<Integer> fileIdx = createNumber("fileIdx", Integer.class);

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final QPersonEntity personEntity;

    public final EnumPath<SaveStatus> saveStatus = createEnum("saveStatus", SaveStatus.class);

    public QFileEntity(String variable) {
        this(FileEntity.class, forVariable(variable), INITS);
    }

    public QFileEntity(Path<? extends FileEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFileEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFileEntity(PathMetadata metadata, PathInits inits) {
        this(FileEntity.class, metadata, inits);
    }

    public QFileEntity(Class<? extends FileEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.personEntity = inits.isInitialized("personEntity") ? new QPersonEntity(forProperty("personEntity")) : null;
    }

}

