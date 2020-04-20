package com.example.demo.upload.entity.dto;

import lombok.*;

@Getter
@Setter
public class Filter {

    String dbSelect; //db
    String condition; // 비교 값
    CompType comp; //비교 기호

    public Filter() {
    }

    public Filter(String dbSelect, String condition, String comp) {
        this.dbSelect = dbSelect;
        this.condition = condition;
        this.comp = CompType.valueOf(comp);
    }

    public Filter(String[] split) {
        if (split.length != 3)
            throw new IllegalArgumentException("Wrong Filters");

        this.dbSelect=split[0];
        this.condition=split[1];
        this.comp= CompType.valueOf(split[2]);
    }
}
