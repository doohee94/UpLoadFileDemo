package com.example.demo.upload.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "preset")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int presetId;

    private int personId;

    String presetName;
    LocalDate presetDate;

    @OneToMany(mappedBy = "presetEntity", fetch = FetchType.LAZY)
    List<PresetListEntity> presetListEntities;

}
