package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Getter
@Builder
public class SIFT {

    String annotationType;
    @Builder.Default
    BigDecimal siftScore = BigDecimal.valueOf(-100);
    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;



}
