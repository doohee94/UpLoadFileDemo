package com.example.demo.upload.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPresetListEntity is a Querydsl query type for PresetListEntity
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPresetListEntity extends EntityPathBase<PresetListEntity> {

    private static final long serialVersionUID = -550801864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPresetListEntity presetListEntity = new QPresetListEntity("presetListEntity");

    public final QAnnovarDBInformation annovarDBInformation;

    public final QPresetEntity presetEntity;

    public final NumberPath<Integer> presetListId = createNumber("presetListId", Integer.class);

    public QPresetListEntity(String variable) {
        this(PresetListEntity.class, forVariable(variable), INITS);
    }

    public QPresetListEntity(Path<? extends PresetListEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPresetListEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPresetListEntity(PathMetadata metadata, PathInits inits) {
        this(PresetListEntity.class, metadata, inits);
    }

    public QPresetListEntity(Class<? extends PresetListEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.annovarDBInformation = inits.isInitialized("annovarDBInformation") ? new QAnnovarDBInformation(forProperty("annovarDBInformation")) : null;
        this.presetEntity = inits.isInitialized("presetEntity") ? new QPresetEntity(forProperty("presetEntity")) : null;
    }

}

