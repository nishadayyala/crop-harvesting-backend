package com.example.demo.controller;

import com.example.demo.dto.CropRequest;
import com.example.demo.dto.CropResponse;
import com.example.demo.service.CropService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CropController {

    private final CropService cropService;

    public CropController(CropService cropService) {
        this.cropService = cropService;
    }

    @GetMapping("/crops")
    public List<CropResponse> getAllCrops(Authentication authentication) {
        return cropService.getAllCrops(authentication.getName(), isAdmin(authentication));
    }

    @GetMapping("/crops/{id}")
    public CropResponse getCropById(@PathVariable Long id, Authentication authentication) {
        return cropService.getCropById(id, authentication.getName(), isAdmin(authentication));
    }

    @PostMapping("/crops")
    public ResponseEntity<CropResponse> createCrop(
            @Valid @RequestBody CropRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(cropService.createCrop(request, authentication.getName()), HttpStatus.CREATED);
    }

    @PutMapping("/crops/{id}")
    public CropResponse updateCrop(
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request,
            Authentication authentication) {
        return cropService.updateCrop(id, request, authentication.getName(), isAdmin(authentication));
    }

    @DeleteMapping("/crops/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id, Authentication authentication) {
        cropService.deleteCrop(id, authentication.getName(), isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
