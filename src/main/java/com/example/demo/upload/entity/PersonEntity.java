package com.example.demo.upload.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class PersonEntity {

    @Id
    private String personId;

    @OneToMany
    private List<FileEntity> fileEntityList;



}
