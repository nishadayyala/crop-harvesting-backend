package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CropRequest {

    @NotBlank(message = "Crop name is required")
    @Size(max = 100, message = "Crop name must be at most 100 characters")
    private String cropName;

    @NotBlank(message = "Crop type is required")
    @Size(max = 100, message = "Crop type must be at most 100 characters")
    private String cropType;

    @NotBlank(message = "Crop variety is required")
    @Size(max = 100, message = "Crop variety must be at most 100 characters")
    private String cropVariety;

    @NotBlank(message = "Field location is required")
    @Size(max = 255, message = "Field location must be at most 255 characters")
    private String fieldLocation;

    @NotNull(message = "Cultivated area is required")
    @Positive(message = "Cultivated area must be greater than zero")
    private Double cultivatedArea;

    @NotNull(message = "Sowing date is required")
    private LocalDate sowingDate;

    @NotNull(message = "Expected harvesting date is required")
    private LocalDate expectedHarvestDate;

    @NotBlank(message = "Farmer name is required")
    @Size(max = 100, message = "Farmer name must be at most 100 characters")
    private String farmerName;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getCropVariety() {
        return cropVariety;
    }

    public void setCropVariety(String cropVariety) {
        this.cropVariety = cropVariety;
    }

    public String getFieldLocation() {
        return fieldLocation;
    }

    public void setFieldLocation(String fieldLocation) {
        this.fieldLocation = fieldLocation;
    }

    public Double getCultivatedArea() {
        return cultivatedArea;
    }

    public void setCultivatedArea(Double cultivatedArea) {
        this.cultivatedArea = cultivatedArea;
    }

    public LocalDate getSowingDate() {
        return sowingDate;
    }

    public void setSowingDate(LocalDate sowingDate) {
        this.sowingDate = sowingDate;
    }

    public LocalDate getExpectedHarvestDate() {
        return expectedHarvestDate;
    }

    public void setExpectedHarvestDate(LocalDate expectedHarvestDate) {
        this.expectedHarvestDate = expectedHarvestDate;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
