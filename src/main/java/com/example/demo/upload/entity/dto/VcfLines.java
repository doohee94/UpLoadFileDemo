package com.example.demo.upload.entity.dto;

import com.example.demo.upload.common.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data

public class VcfLines {
    private List<VcfLine> vcfLines = new ArrayList<>();


    public void add(VcfLine vcfLine){
        this.vcfLines.add(vcfLine);
    }

    public VcfLines(){}

    public Page<?> convertPagination(Pageable pageable) {

        return PaginationUtil.convertListToPage(vcfLines, pageable);
    }

    public List<VcfLine> filter(FilterList filterList) {

        if(filterList.convertFilters().isEmpty()) return vcfLines;

        for (Filter filter : filterList.convertFilters()) {
            vcfLines =  vcfLines.stream().filter(vcfLine -> vcfLine.isFiltered(filter)).collect(Collectors.toList());
        }

        return vcfLines;
    }
}
