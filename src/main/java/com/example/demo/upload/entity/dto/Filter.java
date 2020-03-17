package com.example.demo.upload.entity.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class Filter {

    String dbSelect;
    String condition;
    String comp;

    public Filter(String dbSelect, String condition, String comp) {
        this.dbSelect = dbSelect;
        this.condition = condition;
        this.comp = comp;
    }
}
