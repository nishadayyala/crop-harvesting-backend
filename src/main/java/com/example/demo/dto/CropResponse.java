package com.example.demo.dto;

import com.example.demo.entity.Crop;
import java.time.LocalDate;

public record CropResponse(
        Long id,
        String cropName,
        String cropType,
        String cropVariety,
        String fieldLocation,
        Double cultivatedArea,
        LocalDate sowingDate,
        LocalDate expectedHarvestDate,
        String farmerName,
        String notes
) {
    public static CropResponse from(Crop crop) {
        return new CropResponse(
                crop.getId(),
                crop.getCropName(),
                crop.getCropType(),
                crop.getCropVariety(),
                crop.getFieldLocation(),
                crop.getCultivatedArea(),
                crop.getSowingDate(),
                crop.getExpectedHarvestDate(),
                crop.getFarmerName(),
                crop.getNotes()
        );
    }
}
