package com.example.demo.upload.entity.dto;

import com.example.demo.upload.entity.AnnovarDBInformation;
import com.example.demo.upload.entity.PresetEntity;
import lombok.Getter;

import java.util.List;

public class PresetDto {

    @Getter
    public static class ListResponse extends ListResult<PresetEntity> {

        public ListResponse(List<PresetEntity> content) {
            super(content);
        }
    }

}
