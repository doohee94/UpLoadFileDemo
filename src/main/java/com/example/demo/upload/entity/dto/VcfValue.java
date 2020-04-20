package com.example.demo.upload.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class VcfValue {
    private String header;
    private String value;

    @Builder
    public VcfValue(String header, String value) {
        this.header = header;
        this.value = value;
    }

    public boolean isMatchedHeader(String dbSelect) {
        if (header.equalsIgnoreCase(dbSelect)) {
            return true;
        }
        return false;
    }

    public boolean isMatchedValue(String comp, String condition) {

        if(condition.equals("~="))
            return this.value.startsWith(comp);

        if (condition.equals("="))
            return comp.equalsIgnoreCase(this.value);

        if (condition.equals("!="))
            return !comp.equalsIgnoreCase(this.value);

        if (condition.equals("<"))
            return (!this.value.equals(".")) && (Double.parseDouble(comp) < Double.parseDouble(this.value));

        if (condition.equals(">"))
            return (!this.value.equals(".")) && Double.parseDouble(comp) > Double.parseDouble(this.value);

        if (condition.equals("<="))
            return (!this.value.equals(".")) && Double.parseDouble(comp) <= Double.parseDouble(this.value);

        if (condition.equals(">="))
            return (!this.value.equals(".")) && Double.parseDouble(comp) >= Double.parseDouble(this.value);

        return false;
    }
}
