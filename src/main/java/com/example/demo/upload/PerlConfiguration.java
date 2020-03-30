package com.example.demo.upload;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "perl")
@Getter
@Setter
public class PerlConfiguration {

    private String command;
    private String annovarDB;
    private String uploadedPath;
    private String convertPath;

}
