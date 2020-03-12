package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Getter
@Builder
public class RefGeneAll {

    String geneType;
    String geneNames;
    List<String> geneList;
    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

    public static class RefGeneAllBuilder {

        public RefGeneAll.RefGeneAllBuilder geneList(String geneNames) {
            this.geneList = spiltList(geneNames, ",");
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
