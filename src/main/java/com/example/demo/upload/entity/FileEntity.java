package com.example.demo.upload.entity;


import lombok.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.*;
import java.time.LocalDateTime;



@Entity
@Table(name = "files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @Column(name = "file_idx")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int fileIdx;

    @JoinColumn(name = "person_id")
    private int personId;

    private String fileName;

    private String fileFakeName;

    private long fileSize;

    private String fileContentType;

    private String attachmentUrl;

    private LocalDateTime uploadedDate;

    @Transient
    private Long analyzeCount;
//    private String status;


    @Builder
    public FileEntity(MultipartFile file) {

        this.personId = ((int) (Math.random() * 10) + 1);
        this.fileName = file.getOriginalFilename();
        this.fileFakeName = RandomStringUtils.randomAlphanumeric(32) + ".vcf";
        this.fileSize = file.getSize();
        this.fileContentType = file.getContentType();
        this.attachmentUrl = "C:/uploadedFile/" + this.fileFakeName;
        this. uploadedDate =  LocalDateTime.now();
//        this.status = "";
    }

    public void isValdateExtension() {
        if (!FilenameUtils.getExtension(fileName).toLowerCase().equals("vcf")) {
            throw new RuntimeException("vcf파일 아님");
        }
    }
}




