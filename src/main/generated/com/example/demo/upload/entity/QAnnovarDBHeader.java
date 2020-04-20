package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnnovarDBHeader is a Querydsl query type for AnnovarDBHeader
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAnnovarDBHeader extends EntityPathBase<AnnovarDBHeader> {

    private static final long serialVersionUID = -1940761972L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnnovarDBHeader annovarDBHeader = new QAnnovarDBHeader("annovarDBHeader");

    public final NumberPath<Integer> annovardbHeaderId = createNumber("annovardbHeaderId", Integer.class);

    public final QAnnovarDBInformation annovarDBInformation;

    public final StringPath header = createString("header");

    public QAnnovarDBHeader(String variable) {
        this(AnnovarDBHeader.class, forVariable(variable), INITS);
    }

    public QAnnovarDBHeader(Path<? extends AnnovarDBHeader> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnnovarDBHeader(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnnovarDBHeader(PathMetadata metadata, PathInits inits) {
        this(AnnovarDBHeader.class, metadata, inits);
    }

    public QAnnovarDBHeader(Class<? extends AnnovarDBHeader> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.annovarDBInformation = inits.isInitialized("annovarDBInformation") ? new QAnnovarDBInformation(forProperty("annovarDBInformation")) : null;
    }

}

