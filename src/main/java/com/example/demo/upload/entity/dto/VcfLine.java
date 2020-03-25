package com.example.demo.upload.entity.dto;

import lombok.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

@Data
public class VcfLine {

    public List<VcfValue> vcfValues = new ArrayList<>();

    public VcfLine(List<String> headers, List<String> vcfValues) {

        for (int i = 0; i < headers.size(); i++) {

            String vcfValue = vcfValues.get(i);

            DecimalFormat format = new DecimalFormat("#.########");
            format.setGroupingUsed(false);

            switch (headers.get(i)) {
                case "ljb23_sift":
                    vcfValue = spiltList(vcfValue, ",").get(0);
                    break;
                case "ljb23_pp2hvar":
                    vcfValue = spiltList(vcfValue, ",").get(0);
                    break;
            }//end switch

            if(!vcfValue.equals(".") && headers.get(i).contains("ExAC_")){
                vcfValue = format.format(Double.parseDouble(vcfValue));
            }

            if(!vcfValue.equals(".") && headers.get(i).equals("1000g2015aug_all")){
                vcfValue = format.format(Double.parseDouble(vcfValue));
            }

            if(!vcfValue.equals(".") && headers.get(i).equals("esp6500si_all")){
                vcfValue = format.format(Double.parseDouble(vcfValue));
            }

            this.vcfValues.add(VcfValue.builder()
                    .header(headers.get(i))
                    .value(vcfValue)
                    .build());
        }

    }

    public Boolean isFiltered(Filter filter) {

        return this.vcfValues.stream().anyMatch(vcfValue -> {
                if (vcfValue.isMatchedHeader(filter.getDbSelect())) {
                    return vcfValue.isMatchedValue(filter.getComp(), filter.getCondition());
                }
            return false;
        });

    }

    List<String> spiltList(String genes, String delimiter) {
        List<String> list = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(genes, delimiter);

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }


}
