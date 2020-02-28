package com.example.demo.upload.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;



@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileIdx;

    private String fileName;

    private String fileFakeName;

    private long fileSize;

    private String fileContentType;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    private SaveStatus saveStatus;
}




