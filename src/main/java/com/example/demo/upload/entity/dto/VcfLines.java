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

    public VcfLines(List<VcfLine> vcfLines){
        this.vcfLines = vcfLines;
    }

    public Page<?> convertPagination(Pageable pageable) {

        return PaginationUtil.convertListToPage(vcfLines, pageable);
    }

    public List<VcfLine> filter(Filter filter) {
        return vcfLines.stream().filter(vcfLine -> vcfLine.isFiltered(filter)).collect(Collectors.toList());
    }

    public List<VcfLine> convertVcfLinesToList() {
        return vcfLines.stream().collect(Collectors.toList());
    }

}
