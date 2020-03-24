package com.example.demo.upload.entity;

import lombok.*;

import javax.persistence.*;
@Entity
@Table(name = "convertFiles")
@Getter
@Setter
@Builder
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

    private long fileSize;

    private String fileContentType;

    private String attachmentUrl;

}
