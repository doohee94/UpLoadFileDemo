package com.example.demo.upload.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "annovardb_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnnovarDBInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int annovardbInformationId;
    private String group;
    private String viewName;
    private String realName;


    @OneToMany(mappedBy = "annovarDBInformation", fetch = FetchType.LAZY)
    List<AnnovarDBHeader> headerList = new ArrayList<>();




}
