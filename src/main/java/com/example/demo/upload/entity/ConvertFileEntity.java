package com.example.demo.upload.entity;

import lombok.*;

import javax.persistence.*;
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

    private String fileName;

    private String fileFakeName;

    private String attachmentUrl;

    private LocalDateTime annotatedDate;


    @Builder
    public ConvertFileEntity(FileEntity fileEntity, String inputFileName, Long countConvertFile) {

        this.originFileidx = fileEntity.getFileIdx();
        this.fileName = inputFileName;
        this.fileFakeName = fileEntity.getFileFakeName()+"("+(countConvertFile+1)+")"+".hg19_multianno.txt";
        this.attachmentUrl = "C:/convertFile/" + fileEntity.getFileFakeName() +"("+(countConvertFile+1)+")"+ ".hg19_multianno.txt";
        this.annotatedDate = LocalDateTime.now();
    }
}
