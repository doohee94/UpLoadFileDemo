package com.example.demo.upload.entity.dto;

import lombok.Data;

@Data
public class Header {

    String headerName;
    boolean checked=false;

    public Header(String header) {
      this.headerName = header;
      this.checked = true;
    }

    public Header(String header,boolean isChecked) {
        this.headerName = header;
        this.checked = isChecked;
    }
}
