package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@Builder
public class RefGene {

    String line;
    String consequences;
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


    public static class RefGeneBuilder {

        public RefGene.RefGeneBuilder geneList(String geneNames) {
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
