package com.example.demo.upload.entity.dto;

import lombok.Getter;
import java.util.List;

public class HeaderDto {

    @Getter
    public static class Response extends SingleResult<Header> {

        String header;

        public Response(String id, String type, String value, String header) {
            super(id, type, value);
            this.header = header;
        }
    }


    @Getter
    public static class ListResponse extends ListResult<Header> {

        public ListResponse(List<Header> content) {
            super(content);
        }
    }
}
