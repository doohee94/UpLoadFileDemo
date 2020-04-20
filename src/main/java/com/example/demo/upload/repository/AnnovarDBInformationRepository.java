package com.example.demo.upload.repository;

import com.example.demo.upload.entity.AnnovarDBHeader;
import com.example.demo.upload.entity.AnnovarDBInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnovarDBInformationRepository extends JpaRepository<AnnovarDBInformation,Integer> {
    List<AnnovarDBInformation> findAll();
    List<AnnovarDBInformation> findByGroup(String group);
}
