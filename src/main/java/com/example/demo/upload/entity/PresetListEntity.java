package com.example.demo.upload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "preset_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresetListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int presetListId;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="preset_id")
    @JsonBackReference
    private PresetEntity presetEntity;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name="annovardb_information_id")
    @JsonBackReference
    private AnnovarDBInformation annovarDBInformation;

}
