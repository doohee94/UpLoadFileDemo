package com.example.demo.upload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name="file_idx")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileIdx;

    @JoinColumn(name = "person_id")
    private int personId;

    private String fileName;

    private String fileFakeName;

    private long fileSize;

    private String fileContentType;

    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    private SaveStatus saveStatus;


}




