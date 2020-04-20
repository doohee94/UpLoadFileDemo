package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnnovarDBInformation is a Querydsl query type for AnnovarDBInformation
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QAnnovarDBInformation extends EntityPathBase<AnnovarDBInformation> {

    private static final long serialVersionUID = 1735011469L;

    public static final QAnnovarDBInformation annovarDBInformation = new QAnnovarDBInformation("annovarDBInformation");

    public final NumberPath<Integer> annovardbInformationId = createNumber("annovardbInformationId", Integer.class);

    public final StringPath group = createString("group");

    public final ListPath<AnnovarDBHeader, QAnnovarDBHeader> headerList = this.<AnnovarDBHeader, QAnnovarDBHeader>createList("headerList", AnnovarDBHeader.class, QAnnovarDBHeader.class, PathInits.DIRECT2);

    public final StringPath realName = createString("realName");

    public final StringPath viewName = createString("viewName");

    public QAnnovarDBInformation(String variable) {
        super(AnnovarDBInformation.class, forVariable(variable));
    }

    public QAnnovarDBInformation(Path<? extends AnnovarDBInformation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAnnovarDBInformation(PathMetadata metadata) {
        super(AnnovarDBInformation.class, metadata);
    }

}

