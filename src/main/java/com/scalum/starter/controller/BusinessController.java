package com.scalum.starter.controller;

import com.scalum.starter.dto.BusinessDTO;
import com.scalum.starter.dto.CreateBusinessDTO;
import com.scalum.starter.model.Business;
import com.scalum.starter.repository.BusinessRepository;
import com.scalum.starter.repository.BusinessUserRoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
@Tag(name = "Business Management", description = "APIs for managing businesses")
public class BusinessController {

    private final BusinessRepository businessRepository;
    private final BusinessUserRoleRepository businessUserRoleRepository;

    @GetMapping
    @Operation(summary = "List all businesses")
    public List<BusinessDTO> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get business by ID")
    public ResponseEntity<BusinessDTO> getBusinessById(@PathVariable UUID id) {
        return businessRepository
                .findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    @Operation(summary = "Create a new business")
    public ResponseEntity<BusinessDTO> createBusiness(
            @Valid @RequestBody CreateBusinessDTO createDTO, @AuthenticationPrincipal Jwt jwt) {
        // In a real app, we would extract user ID from JWT and validate permissions
        // UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        // For now, we'll assume a placeholder or extract if possible.
        // Since the User entity uses UUID, we need a valid UUID.
        // Let's assume the JWT 'sub' is the UUID.

        UUID userId;
        try {
            userId = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException e) {
            // Fallback or error if subject is not UUID (e.g. keycloak ID might be UUID)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid User ID in token");
        }

        Business business = new Business();
        business.setBusinessName(createDTO.getBusinessName());
        business.setTaxId(createDTO.getTaxId());
        business.setParentId(createDTO.getParentId());
        business.setCreatedByUserId(userId);

        // Logic for treePath would go here or in a service
        // For root business:
        if (createDTO.getParentId() == null) {
            // It's a root
            // We need to save first to get ID, then update treePath?
            // Or generate ID manually.
            // JPA @GeneratedValue(strategy = GenerationType.UUID) generates it before persist
            // usually.
            // But we might need to flush.
        } else {
            Business parent =
                    businessRepository
                            .findById(createDTO.getParentId())
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.NOT_FOUND,
                                                    "Parent business not found"));
            // business.setTreePath(parent.getTreePath() + "." + business.getId()); // Logic needs
            // ID
        }

        // Since we are in a controller, we should probably delegate complex logic to a Service.
        // But for this task, I'll do a simple save.
        // Note: treePath logic is missing here as it requires ID which is generated on save.

        Business savedBusiness = businessRepository.save(business);

        // Update treePath after save if needed
        if (savedBusiness.getTreePath() == null) {
            if (savedBusiness.getParentId() == null) {
                savedBusiness.setTreePath(savedBusiness.getId().toString());
            } else {
                Business parent =
                        businessRepository.findById(savedBusiness.getParentId()).orElseThrow();
                savedBusiness.setTreePath(parent.getTreePath() + "." + savedBusiness.getId());
            }
            savedBusiness = businessRepository.save(savedBusiness);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedBusiness));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Update a business")
    public ResponseEntity<BusinessDTO> updateBusiness(
            @PathVariable UUID id, @Valid @RequestBody CreateBusinessDTO updateDTO) {
        return businessRepository
                .findById(id)
                .map(
                        business -> {
                            business.setBusinessName(updateDTO.getBusinessName());
                            business.setTaxId(updateDTO.getTaxId());
                            // Changing parent is complex (tree move), skipping for simple CRUD
                            return businessRepository.save(business);
                        })
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a business")
    public ResponseEntity<Void> deleteBusiness(@PathVariable UUID id) {
        if (businessRepository.existsById(id)) {
            businessRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private BusinessDTO convertToDTO(Business business) {
        BusinessDTO dto = new BusinessDTO();
        dto.setId(business.getId());
        dto.setParentId(business.getParentId());
        dto.setBusinessName(business.getBusinessName());
        dto.setTaxId(business.getTaxId());
        dto.setCreatedByUserId(business.getCreatedByUserId());
        dto.setTreePath(business.getTreePath());
        dto.setSettingsSnapshot(business.getSettingsSnapshot());
        dto.setActive(business.isActive());
        return dto;
    }
}
