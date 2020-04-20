package com.example.demo.upload.entity.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FilterList {

    List<String> filterList;

    public FilterList(List<String> filterList) {
        this.filterList = filterList;
    }



    public List<Filter> convertFilters() {
        List<Filter> filters = new ArrayList<>();

        if(filterList == null) return filters;

        for (String filterStr : filterList) {
            String[] split = filterStr.split(";");
            Filter filter = new Filter(split);
            filters.add(filter);
            System.out.println(filterStr);
            System.out.println(filter);
        }
        return filters;
    }
}
