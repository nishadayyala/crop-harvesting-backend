package com.example.demo.service;

import com.example.demo.dto.HarvestingRecordRequest;
import com.example.demo.dto.HarvestingRecordResponse;
import com.example.demo.entity.Crop;
import com.example.demo.entity.HarvestingRecord;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.HarvestingRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HarvestingRecordService {

    private final HarvestingRecordRepository harvestingRecordRepository;
    private final CropService cropService;

    public HarvestingRecordService(HarvestingRecordRepository harvestingRecordRepository, CropService cropService) {
        this.harvestingRecordRepository = harvestingRecordRepository;
        this.cropService = cropService;
    }

    @Transactional(readOnly = true)
    public List<HarvestingRecordResponse> getAllHarvestingRecords(String email, boolean admin) {
        List<HarvestingRecord> records = admin
                ? harvestingRecordRepository.findAll()
                : harvestingRecordRepository.findAllByCrop_Owner_Email(email);
        return records.stream().map(HarvestingRecordResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public HarvestingRecordResponse getHarvestingRecordById(Long id, String email, boolean admin) {
        return HarvestingRecordResponse.from(findHarvestingRecordByUser(id, email, admin));
    }

    @Transactional
    public HarvestingRecordResponse createHarvestingRecord(
            HarvestingRecordRequest request,
            String email,
            boolean admin) {
        Crop crop = cropService.findCropByIdForUser(request.getCropId(), email, admin);

        HarvestingRecord record = new HarvestingRecord();
        record.setCrop(crop);
        record.setFarmerName(request.getFarmerName());
        record.setFieldLocation(request.getFieldLocation());
        record.setPlannedHarvestDate(request.getPlannedHarvestDate());
        record.setActualHarvestDate(request.getActualHarvestDate());
        record.setHarvestedQuantity(request.getHarvestedQuantity());
        record.setStatus(request.getStatus());
        record.setNotes(request.getNotes());

        return HarvestingRecordResponse.from(harvestingRecordRepository.save(record));
    }

    @Transactional
    public HarvestingRecordResponse updateHarvestingRecord(
            Long id,
            HarvestingRecordRequest request,
            String email,
            boolean admin) {
        HarvestingRecord record = findHarvestingRecordByUser(id, email, admin);
        Crop crop = cropService.findCropByIdForUser(request.getCropId(), email, admin);

        record.setCrop(crop);
        record.setFarmerName(request.getFarmerName());
        record.setFieldLocation(request.getFieldLocation());
        record.setPlannedHarvestDate(request.getPlannedHarvestDate());
        record.setActualHarvestDate(request.getActualHarvestDate());
        record.setHarvestedQuantity(request.getHarvestedQuantity());
        record.setStatus(request.getStatus());
        record.setNotes(request.getNotes());

        return HarvestingRecordResponse.from(harvestingRecordRepository.save(record));
    }

    @Transactional
    public void deleteHarvestingRecord(Long id, String email, boolean admin) {
        HarvestingRecord record = findHarvestingRecordByUser(id, email, admin);
        harvestingRecordRepository.delete(record);
    }

    private HarvestingRecord findHarvestingRecordByUser(Long id, String email, boolean admin) {
        return (admin
                ? harvestingRecordRepository.findById(id)
                : harvestingRecordRepository.findByIdAndCrop_Owner_Email(id, email))
                .orElseThrow(() -> new ResourceNotFoundException("Harvesting record not found with id: " + id));
    }
}
