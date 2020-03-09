package com.example.demo.upload.entity.dto;

import lombok.Data;

@Data
public class Genome1000 {

    String annotationType;
    double alleleFrequency;
    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

}
