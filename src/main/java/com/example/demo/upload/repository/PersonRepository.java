package com.example.demo.upload.repository;

import com.example.demo.upload.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<PersonEntity, Integer> {

}
