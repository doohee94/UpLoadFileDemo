package com.example.demo.upload.entity.dto;

public class SingleResult<T> {
    private String id;
    private String type;
    private String value;

    public SingleResult(String id, String type, String value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }
}
