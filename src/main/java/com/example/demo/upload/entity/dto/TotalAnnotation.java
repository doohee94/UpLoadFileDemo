package com.example.demo.upload.entity.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TotalAnnotation {

    String annotationType;

    String clinvarDescription;//clinvar

    BigDecimal score = BigDecimal.valueOf(-100);//sift

    //refGene
    String line;
    String geneType;
    String geneNames;

    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

    List<String> geneList;
    Map<String, String> clinvarDescriptionMap;

    //Sift /polyphen / esp, exac, genome1000
    public TotalAnnotation(String annotationType, BigDecimal score, String chromNum, long start, long end, String ref, String alt, String t1, String t2, String t3) {
        this.annotationType = annotationType;
        this.score = score;
        this.chromNum = chromNum;
        this.start = start;
        this.end = end;
        this.ref = ref;
        this.alt = alt;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    //refGene_exonic
    public TotalAnnotation(String line, String geneType, String geneNames, String chromNum, long start, long end, String ref, String alt, String t1, String t2, String t3) {
        this.line = line;
        this.geneType = geneType;
        this.geneNames = geneNames;
        this.chromNum = chromNum;
        this.start = start;
        this.end = end;
        this.ref = ref;
        this.alt = alt;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    //clinvar / refGene all
    public TotalAnnotation(String annotationType, String clinvarDescription, String chromNum, long start, long end, String ref, String alt, String t1, String t2, String t3) {
        this.annotationType = annotationType;
        this.clinvarDescription = clinvarDescription;
        this.chromNum = chromNum;
        this.start = start;
        this.end = end;
        this.ref = ref;
        this.alt = alt;
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public void setGeneList(List<String> geneList) {
        this.geneList = geneList;
    }


    public Map<String, String> getClinvarDescriptionMap() {
        return clinvarDescriptionMap;
    }

    public void setClinvarDescriptionMap(Map<String, String> clinvarDescriptionMap) {
        this.clinvarDescriptionMap = clinvarDescriptionMap;
    }

    public String getAnnotationType() {
        return annotationType;
    }

    public String getClinvarDescription() {
        return clinvarDescription;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getLine() {
        return line;
    }

    public String getGeneType() {
        return geneType;
    }

    public String getGeneNames() {
        return geneNames;
    }

    public String getChromNum() {
        return chromNum;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getT1() {
        return t1;
    }

    public String getT2() {
        return t2;
    }

    public String getT3() {
        return t3;
    }

    public List<String> getGeneList() {
        return geneList;
    }



}
