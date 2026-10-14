package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class HarvestingRecordRequest {

    @NotNull(message = "Crop ID is required")
    @Positive(message = "Crop ID must be greater than zero")
    private Long cropId;

    @NotBlank(message = "Farmer name is required")
    @Size(max = 100, message = "Farmer name must be at most 100 characters")
    private String farmerName;

    @NotBlank(message = "Field location is required")
    @Size(max = 255, message = "Field location must be at most 255 characters")
    private String fieldLocation;

    @NotNull(message = "Planned harvesting date is required")
    private LocalDate plannedHarvestDate;

    private LocalDate actualHarvestDate;

    @NotNull(message = "Harvested quantity is required")
    @Positive(message = "Harvested quantity must be greater than zero")
    private Double harvestedQuantity;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Pending|In Progress|Completed",
            message = "Status must be Pending, In Progress, or Completed")
    private String status;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    private String notes;

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public String getFieldLocation() {
        return fieldLocation;
    }

    public void setFieldLocation(String fieldLocation) {
        this.fieldLocation = fieldLocation;
    }

    public LocalDate getPlannedHarvestDate() {
        return plannedHarvestDate;
    }

    public void setPlannedHarvestDate(LocalDate plannedHarvestDate) {
        this.plannedHarvestDate = plannedHarvestDate;
    }

    public LocalDate getActualHarvestDate() {
        return actualHarvestDate;
    }

    public void setActualHarvestDate(LocalDate actualHarvestDate) {
        this.actualHarvestDate = actualHarvestDate;
    }

    public Double getHarvestedQuantity() {
        return harvestedQuantity;
    }

    public void setHarvestedQuantity(Double harvestedQuantity) {
        this.harvestedQuantity = harvestedQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
