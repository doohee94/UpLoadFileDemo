package com.example.demo.upload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "annovardb_header")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnnovarDBHeader {

    @Id
    int annovardbHeaderId;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="annovardb_information_id")
    @JsonBackReference
    private AnnovarDBInformation annovarDBInformation;

    String header;

    @Transient
    @Setter
    boolean displayChecked = false;



}
