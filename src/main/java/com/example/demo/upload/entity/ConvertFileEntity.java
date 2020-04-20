package com.example.demo.upload.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "convertFiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConvertFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileIdx;

    @JoinColumn(name = "file_idx")
    private int originFileidx;

    private String fileFakeName;

    private String attachmentUrl;

    private LocalDate annotatedDate;


    @Builder
    public ConvertFileEntity(FileEntity fileEntity) {

        this.originFileidx = fileEntity.getFileIdx();
        this.fileFakeName = fileEntity.getFileFakeName()+".hg19_multianno.txt";
        this.attachmentUrl = "C:/convertFile/" + fileEntity.getFileFakeName() +".hg19_multianno.txt";
        this.annotatedDate = LocalDate.now();
    }
}
