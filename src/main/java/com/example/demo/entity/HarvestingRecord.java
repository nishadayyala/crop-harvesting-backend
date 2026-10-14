package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "harvesting_records")
public class HarvestingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    @NotBlank(message = "Farmer name is required")
    @Column(nullable = false)
    private String farmerName;

    @NotBlank(message = "Field location is required")
    @Column(nullable = false)
    private String fieldLocation;

    @NotNull(message = "Planned harvesting date is required")
    @Column(nullable = false)
    private LocalDate plannedHarvestDate;

    private LocalDate actualHarvestDate;

    @NotNull(message = "Harvested quantity is required")
    @Column(nullable = false)
    private Double harvestedQuantity;

    @NotBlank(message = "Status is required")
    @Column(nullable = false)
    private String status;

    @Column(length = 1000)
    private String notes;

    public HarvestingRecord() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Crop getCrop() {
        return crop;
    }

    public void setCrop(Crop crop) {
        this.crop = crop;
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
