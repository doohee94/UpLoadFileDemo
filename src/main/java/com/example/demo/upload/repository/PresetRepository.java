package com.example.demo.upload.repository;

import com.example.demo.upload.entity.AnnovarDBInformation;
import com.example.demo.upload.entity.PresetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresetRepository extends JpaRepository<PresetEntity,Integer> {

    List<PresetEntity> findByPersonId(int personId);


}
