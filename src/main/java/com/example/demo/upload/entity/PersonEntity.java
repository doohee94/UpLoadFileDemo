package com.example.demo.upload.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    public PersonEntity(int personId) {
        this.personId = personId;
    }


}
