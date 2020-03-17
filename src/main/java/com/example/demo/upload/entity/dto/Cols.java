package com.example.demo.upload.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Cols {
    Integer colsNum;
    String colsName;
    boolean colsStatus;

    public Cols(Integer colsNum, String colsName, boolean colsStatus) {
        this.colsNum = colsNum;
        this.colsName = colsName;
        this.colsStatus = colsStatus;
    }
}
