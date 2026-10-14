package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.model.User;

@Entity
@Table(name = "crops")
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Crop name is required")
    @Column(nullable = false)
    private String cropName;

    @NotBlank(message = "Crop type is required")
    @Column(nullable = false)
    private String cropType;

    @NotBlank(message = "Crop variety is required")
    @Column(nullable = false)
    private String cropVariety;

    @NotBlank(message = "Field location is required")
    @Column(nullable = false)
    private String fieldLocation;

    @NotNull(message = "Cultivated area is required")
    @Column(nullable = false)
    private Double cultivatedArea;

    @NotNull(message = "Sowing date is required")
    @Column(nullable = false)
    private LocalDate sowingDate;

    @NotNull(message = "Expected harvesting date is required")
    @Column(nullable = false)
    private LocalDate expectedHarvestDate;

    @NotBlank(message = "Farmer name is required")
    @Column(nullable = false)
    private String farmerName;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "crop", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<HarvestingRecord> harvestingRecords = new ArrayList<>();

    public Crop() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public List<HarvestingRecord> getHarvestingRecords() {
        return harvestingRecords;
    }

    public void setHarvestingRecords(List<HarvestingRecord> harvestingRecords) {
        this.harvestingRecords = harvestingRecords;
    }
}
