package com.example.demo.upload.entity.dto;

import com.example.demo.upload.entity.AnnovarDBHeader;
import com.example.demo.upload.entity.AnnovarDBInformation;
import lombok.Getter;

import java.util.List;

public class AnnovarDBHeaderDto {
    @Getter
    public static class ListResponse extends ListResult<AnnovarDBHeader> {

        public ListResponse(List<AnnovarDBHeader> content) {
            super(content);
        }
    }

}
