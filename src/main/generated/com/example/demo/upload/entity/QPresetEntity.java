package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetEntity is a Querydsl query type for PresetEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPresetEntity extends EntityPathBase<PresetEntity> {

    private static final long serialVersionUID = -1232673926L;

    public static final QPresetEntity presetEntity = new QPresetEntity("presetEntity");

    public final NumberPath<Integer> personId = createNumber("personId", Integer.class);

    public final DatePath<java.time.LocalDate> presetDate = createDate("presetDate", java.time.LocalDate.class);

    public final NumberPath<Integer> presetId = createNumber("presetId", Integer.class);

    public final ListPath<PresetListEntity, QPresetListEntity> presetListEntities = this.<PresetListEntity, QPresetListEntity>createList("presetListEntities", PresetListEntity.class, QPresetListEntity.class, PathInits.DIRECT2);

    public final StringPath presetName = createString("presetName");

    public QPresetEntity(String variable) {
        super(PresetEntity.class, forVariable(variable));
    }

    public QPresetEntity(Path<? extends PresetEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPresetEntity(PathMetadata metadata) {
        super(PresetEntity.class, metadata);
    }

}

