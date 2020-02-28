package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPersonEntity is a Querydsl query type for PersonEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPersonEntity extends EntityPathBase<PersonEntity> {

    private static final long serialVersionUID = 1611328368L;

    public static final QPersonEntity personEntity = new QPersonEntity("personEntity");

    public final ListPath<FileEntity, QFileEntity> fileEntity = this.<FileEntity, QFileEntity>createList("fileEntity", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final NumberPath<Integer> personId = createNumber("personId", Integer.class);

    public QPersonEntity(String variable) {
        super(PersonEntity.class, forVariable(variable));
    }

    public QPersonEntity(Path<? extends PersonEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPersonEntity(PathMetadata metadata) {
        super(PersonEntity.class, metadata);
    }

}

