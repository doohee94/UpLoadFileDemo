package com.example.demo.upload.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
public class PersonEntity {

    @Id
    @Column(name="person_id")
    private int personId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personEntity") //mappedBy는 컬럼명이 아니라 엔티티들 이름으로....
    private List<FileEntity> fileEntity ;

    public PersonEntity(int personId) {
        this.personId = personId;
    }


}
