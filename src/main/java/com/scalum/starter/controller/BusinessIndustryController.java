package com.scalum.starter.controller;

import com.scalum.starter.model.BusinessIndustry;
import com.scalum.starter.repository.BusinessIndustryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/industries")
@RequiredArgsConstructor
@Tag(name = "Business Industry Management", description = "APIs for managing business industries")
public class BusinessIndustryController {

    private final BusinessIndustryRepository industryRepository;

    @GetMapping
    @Operation(summary = "List all business industries")
    public List<BusinessIndustry> getAllIndustries() {
        return industryRepository.findAll();
    }

    @PostMapping
    @Operation(summary = "Create a new business industry")
    public ResponseEntity<BusinessIndustry> createIndustry(
            @Valid @RequestBody BusinessIndustry industry) {
        return ResponseEntity.status(HttpStatus.CREATED).body(industryRepository.save(industry));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a business industry")
    public ResponseEntity<Void> deleteIndustry(@PathVariable UUID id) {
        if (industryRepository.existsById(id)) {
            industryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
