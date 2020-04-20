package com.example.demo.upload.entity.dto;

import com.example.demo.upload.entity.AnnovarDBInformation;
import lombok.Getter;

import java.util.List;

public class AnnovarDBInformationDto {

      @Getter
    public static class ListResponse extends ListResult<AnnovarDBInformation> {

        public ListResponse(List<AnnovarDBInformation> content) {
            super(content);
        }
    }

}
