package com.example.demo.upload.entity.dto;

import lombok.Data;

@Data
public class Clinvar {

    String annotationType;
    String clinvarDescription;
    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

}
