package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;

@Getter
@Builder
public class TotalAnnotation {
    String dbType;
    String annotationType;

    String clinvarDescription;//clinvar

    @Builder.Default
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


    //Builder Custom
    public static class TotalAnnotationBuilder {

        public TotalAnnotationBuilder geneList(String geneNames) {
            this.geneList = spiltList(geneNames, ",");
            return this;
        }

        public TotalAnnotationBuilder clinvarDescriptionMap(String geneNames) {
            List<String> tempList = spiltList(geneNames, ";");
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < tempList.size(); j++) {
                map.put(spiltList(tempList.get(j), "=").get(0), spiltList(tempList.get(j), "=").get(1));
            }
            this.clinvarDescriptionMap = map;
            return this;
        }

        /**
         * method
         */
        List<String> spiltList(String genes, String delimiter) {
            List<String> list = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(genes, delimiter);

            while (st.hasMoreTokens()) {
                list.add(st.nextToken());
            }
            return list;
        }

    }//end builder class
}
