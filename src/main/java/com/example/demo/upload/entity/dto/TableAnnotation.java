package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Builder
public class TableAnnotation {

    String chr;
    long start;
    long end;
    String ref;
    String alt;
    String funcRefGene;
    String geneRefGene;
    String geneDetail;
    List<String> geneDetailList;
    String exonicRefGene;
    String changRefGene;
    List<String> changeRefGeneList;
    String siftScore;
    List<String>siftScoreList;
    String ppScore;
    List<String>ppScoreList;
    String clnalleleid;
    String clndn;
    List<String> clndnList;
    String clndisdb;
    List<String> clndisdbList;
    String clnrevstat;
    List<String> clnrevstatList;
    String clnsig;
    String genome1000Alle;
    String exacAlle;
    String esp6500Alle;

    public static class TableAnnotationBuilder {

        public TableAnnotationBuilder geneDetailList(String geneDetail) {
            this.geneDetailList = spiltList(geneDetail, ";");
            return this;
        }

        public TableAnnotationBuilder changeRefGeneList(String changRefGene) {
            this.changeRefGeneList = spiltList(changRefGene, ",");
            return this;
        }
        public TableAnnotationBuilder clndnList(String clndn) {
            this.clndnList = spiltList(clndn, "|");
            return this;
        }
        public TableAnnotationBuilder clndisdbList(String clndisdb) {
            this.clndisdbList = spiltList(clndisdb, "|");
            return this;
        }
        public TableAnnotationBuilder clnrevstatList(String clnrevsta) {
            this.clnrevstatList = spiltList(clnrevsta, ",");
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


    }//end builder


}
