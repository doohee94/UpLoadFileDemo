package com.example.demo.upload.entity.dto;

import lombok.Getter;

import java.util.List;
@Getter

public class ListResult<T> {

    List<T> content;

    int totalElement;

    public ListResult(List<T> content) {
        this.content = content;
        this.totalElement = content.size();
    }
}
