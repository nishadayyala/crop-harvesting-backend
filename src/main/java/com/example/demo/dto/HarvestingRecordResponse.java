package com.example.demo.dto;

import com.example.demo.entity.HarvestingRecord;
import java.time.LocalDate;

public record HarvestingRecordResponse(
        Long id,
        CropReference crop,
        String farmerName,
        String fieldLocation,
        LocalDate plannedHarvestDate,
        LocalDate actualHarvestDate,
        Double harvestedQuantity,
        String status,
        String notes
) {
    public static HarvestingRecordResponse from(HarvestingRecord record) {
        return new HarvestingRecordResponse(
                record.getId(),
                new CropReference(record.getCrop().getId(), record.getCrop().getCropName()),
                record.getFarmerName(),
                record.getFieldLocation(),
                record.getPlannedHarvestDate(),
                record.getActualHarvestDate(),
                record.getHarvestedQuantity(),
                record.getStatus(),
                record.getNotes()
        );
    }

    public record CropReference(Long id, String cropName) {
    }
}
