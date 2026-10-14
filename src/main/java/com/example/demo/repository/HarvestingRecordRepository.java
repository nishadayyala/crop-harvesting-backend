package com.example.demo.repository;

import com.example.demo.entity.HarvestingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestingRecordRepository extends JpaRepository<HarvestingRecord, Long> {
    boolean existsByCrop_Id(Long cropId);

    List<HarvestingRecord> findAllByCrop_Owner_Email(String email);

    Optional<HarvestingRecord> findByIdAndCrop_Owner_Email(Long id, String email);
}
