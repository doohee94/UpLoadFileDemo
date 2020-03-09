package com.example.demo.upload.entity.dto;

import lombok.Data;

@Data
public class RefGene {

    String line;
    String consequences;
    String geneType;
    String geneNames;
    int chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

}
