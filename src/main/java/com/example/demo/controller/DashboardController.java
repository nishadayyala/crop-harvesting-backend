package com.example.demo.controller;

import com.example.demo.dto.DashboardStatsResponse;
import com.example.demo.entity.Crop;
import com.example.demo.entity.HarvestingRecord;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.HarvestingRecordRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final CropRepository cropRepository;
    private final HarvestingRecordRepository harvestingRecordRepository;

    public DashboardController(CropRepository cropRepository, HarvestingRecordRepository harvestingRecordRepository) {
        this.cropRepository = cropRepository;
        this.harvestingRecordRepository = harvestingRecordRepository;
    }

    @GetMapping("/dashboard/stats")
    public DashboardStatsResponse getDashboardStats(Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        List<Crop> crops = admin
                ? cropRepository.findAll()
                : cropRepository.findAllByOwner_Email(authentication.getName());
        List<HarvestingRecord> harvests = admin
                ? harvestingRecordRepository.findAll()
                : harvestingRecordRepository.findAllByCrop_Owner_Email(authentication.getName());

        long pendingTasks = harvests.stream()
                .filter(record -> "Pending".equalsIgnoreCase(record.getStatus()) || "In Progress".equalsIgnoreCase(record.getStatus()))
                .count();

        long completedTasks = harvests.stream()
                .filter(record -> "Completed".equalsIgnoreCase(record.getStatus()))
                .count();

        return new DashboardStatsResponse(
                crops.size(),
                harvests.size(),
                pendingTasks,
                completedTasks
        );
    }
}
