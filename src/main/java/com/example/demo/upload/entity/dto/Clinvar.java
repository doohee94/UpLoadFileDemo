package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;


@Getter
@Builder
public class Clinvar {

    String annotationType;
    String clinvarDescription;
    Map<String, String> clinvarDescriptionMap;
    String chromNum;
    long start;
    long end;
    String ref;
    String alt;
    String t1;
    String t2;
    String t3;

    //Builder Custom
    public static class ClinvarBuilder {


        public Clinvar.ClinvarBuilder clinvarDescriptionMap(String geneNames) {
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
