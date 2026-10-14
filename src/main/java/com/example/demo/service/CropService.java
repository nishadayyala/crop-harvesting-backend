package com.example.demo.service;

import com.example.demo.dto.CropRequest;
import com.example.demo.dto.CropResponse;
import com.example.demo.entity.Crop;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.CropRepository;
import com.example.demo.repository.HarvestingRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CropService {

    private final CropRepository cropRepository;
    private final HarvestingRecordRepository harvestingRecordRepository;
    private final UserRepository userRepository;

    public CropService(
            CropRepository cropRepository,
            HarvestingRecordRepository harvestingRecordRepository,
            UserRepository userRepository) {
        this.cropRepository = cropRepository;
        this.harvestingRecordRepository = harvestingRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CropResponse> getAllCrops(String email, boolean admin) {
        List<Crop> crops = admin ? cropRepository.findAll() : cropRepository.findAllByOwner_Email(email);
        return crops.stream().map(CropResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CropResponse getCropById(Long id, String email, boolean admin) {
        return CropResponse.from(findCropByIdForUser(id, email, admin));
    }

    @Transactional
    public CropResponse createCrop(CropRequest request, String email) {
        Crop crop = new Crop();
        mapRequestToEntity(request, crop);
        User owner = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found"));
        crop.setOwner(owner);
        return CropResponse.from(cropRepository.save(crop));
    }

    @Transactional
    public CropResponse updateCrop(Long id, CropRequest request, String email, boolean admin) {
        Crop crop = findCropByIdForUser(id, email, admin);
        mapRequestToEntity(request, crop);
        return CropResponse.from(cropRepository.save(crop));
    }

    @Transactional
    public void deleteCrop(Long id, String email, boolean admin) {
        Crop crop = findCropByIdForUser(id, email, admin);
        if (harvestingRecordRepository.existsByCrop_Id(id)) {
            throw new IllegalStateException("Cannot delete crop because it has associated harvesting records.");
        }
        cropRepository.delete(crop);
    }

    @Transactional(readOnly = true)
    public Crop findCropByIdForUser(Long id, String email, boolean admin) {
        return (admin
                ? cropRepository.findById(id)
                : cropRepository.findByIdAndOwner_Email(id, email))
                .orElseThrow(() -> new ResourceNotFoundException("Crop not found with id: " + id));
    }

    private void mapRequestToEntity(CropRequest request, Crop crop) {
        crop.setCropName(request.getCropName());
        crop.setCropType(request.getCropType());
        crop.setCropVariety(request.getCropVariety());
        crop.setFieldLocation(request.getFieldLocation());
        crop.setCultivatedArea(request.getCultivatedArea());
        crop.setSowingDate(request.getSowingDate());
        crop.setExpectedHarvestDate(request.getExpectedHarvestDate());
        crop.setFarmerName(request.getFarmerName());
        crop.setNotes(request.getNotes());
    }
}
