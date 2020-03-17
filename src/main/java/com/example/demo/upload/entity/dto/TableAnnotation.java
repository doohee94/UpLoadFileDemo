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
    String ppScore;
    String clinvar;
    Map<String, String> clinvarMap;
    double genome1000Alle;
    double exacAlle;
    double esp6500Alle;

    public static class TableAnnotationBuilder{

        public TableAnnotationBuilder geneDetailList(String geneDetail){
            this.geneDetailList = spiltList(geneDetail,";");
            return this;
        }

        public TableAnnotationBuilder changeRefGeneList(String changRefGene){
            this.changeRefGeneList = spiltList(changRefGene,",");
            return this;
        }

        public TableAnnotation.TableAnnotationBuilder clinvarMap(String geneNames) {
            if(geneNames.equals(".")){
                this.clinvarMap = null;
                return this;
            }

            List<String> tempList = spiltList(geneNames, ";");
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < tempList.size(); j++) {
                map.put(spiltList(tempList.get(j), "=").get(0), spiltList(tempList.get(j), "=").get(1));
            }
            this.clinvarMap = map;
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
