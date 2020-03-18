package com.example.demo.upload.entity;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.Optional;


@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileIdx;

    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id")
    private PersonEntity personEntity;

    private String fileName;

    private String fileFakeName;

    private long fileSize;

    private String fileContentType;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    private SaveStatus saveStatus;


}




