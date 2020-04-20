package com.example.demo.upload.entity.dto;

public enum CompType {
    EQUAL("="),
    NOTEQUAL("!="),
    GreaterThan(">"),
    GreaterThanEQ(">="),
    LessThan("<"),
    LessThanEQ("<="),
    StartWith("~=");

    String value;

    CompType(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }
}
