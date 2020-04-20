package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QConvertFileEntity is a Querydsl query type for ConvertFileEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QConvertFileEntity extends EntityPathBase<ConvertFileEntity> {

    private static final long serialVersionUID = 844497338L;

    public static final QConvertFileEntity convertFileEntity = new QConvertFileEntity("convertFileEntity");

    public final DatePath<java.time.LocalDate> annotatedDate = createDate("annotatedDate", java.time.LocalDate.class);

    public final StringPath attachmentUrl = createString("attachmentUrl");

    public final StringPath fileFakeName = createString("fileFakeName");

    public final NumberPath<Integer> fileIdx = createNumber("fileIdx", Integer.class);

    public final NumberPath<Integer> originFileidx = createNumber("originFileidx", Integer.class);

    public QConvertFileEntity(String variable) {
        super(ConvertFileEntity.class, forVariable(variable));
    }

    public QConvertFileEntity(Path<? extends ConvertFileEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConvertFileEntity(PathMetadata metadata) {
        super(ConvertFileEntity.class, metadata);
    }

}

