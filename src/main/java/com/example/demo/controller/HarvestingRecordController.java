package com.example.demo.controller;

import com.example.demo.dto.HarvestingRecordRequest;
import com.example.demo.dto.HarvestingRecordResponse;
import com.example.demo.service.HarvestingRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HarvestingRecordController {

    private final HarvestingRecordService harvestingRecordService;

    public HarvestingRecordController(HarvestingRecordService harvestingRecordService) {
        this.harvestingRecordService = harvestingRecordService;
    }

    @GetMapping("/harvests")
    public List<HarvestingRecordResponse> getAllHarvestingRecords(Authentication authentication) {
        return harvestingRecordService.getAllHarvestingRecords(authentication.getName(), isAdmin(authentication));
    }

    @GetMapping("/harvests/{id}")
    public HarvestingRecordResponse getHarvestingRecordById(
            @PathVariable Long id,
            Authentication authentication) {
        return harvestingRecordService.getHarvestingRecordById(
                id, authentication.getName(), isAdmin(authentication));
    }

    @PostMapping("/harvests")
    public ResponseEntity<HarvestingRecordResponse> createHarvestingRecord(
            @Valid @RequestBody HarvestingRecordRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(
                harvestingRecordService.createHarvestingRecord(
                        request, authentication.getName(), isAdmin(authentication)),
                HttpStatus.CREATED);
    }

    @PutMapping("/harvests/{id}")
    public HarvestingRecordResponse updateHarvestingRecord(
            @PathVariable Long id,
            @Valid @RequestBody HarvestingRecordRequest request,
            Authentication authentication) {
        return harvestingRecordService.updateHarvestingRecord(
                id, request, authentication.getName(), isAdmin(authentication));
    }

    @DeleteMapping("/harvests/{id}")
    public ResponseEntity<Void> deleteHarvestingRecord(@PathVariable Long id, Authentication authentication) {
        harvestingRecordService.deleteHarvestingRecord(
                id, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
