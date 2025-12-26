package com.scalum.starter.controller;

import com.scalum.starter.domain.business.BusinessContactService;
import com.scalum.starter.dto.*;
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
@RequestMapping("v1")
@RequiredArgsConstructor
@Tag(name = "Business Contact Management", description = "APIs for managing business contacts")
public class BusinessContactController {

    private final BusinessContactService contactService;

    @PostMapping("/businesses/{businessId}/contacts/instance")
    @Operation(summary = "Create a business contact and a new Evolution API instance")
    public ResponseEntity<BusinessContactWithInstanceDTO> createContactAndInstance(
            @PathVariable UUID businessId,
            @Valid @RequestBody CreateInstanceAndContactDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactService.createContactAndInstance(businessId, createDTO));
    }

    @GetMapping("/contacts/{contactId}/connect")
    @Operation(summary = "Get a new QR code to connect an existing instance")
    public ResponseEntity<BusinessContactWithQrDTO> connectInstance(@PathVariable Long contactId) {
        return ResponseEntity.ok(contactService.connectContactInstance(contactId));
    }

    @PostMapping("/contacts/{contactId}/webhook")
    @Operation(summary = "Set webhook for a contact's instance")
    public ResponseEntity<Void> setWebhook(
            @PathVariable Long contactId, @Valid @RequestBody SetWebhookDTO webhookDTO) {
        contactService.setWebhookForContact(contactId, webhookDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/businesses/{businessId}/contacts")
    @Operation(summary = "List contacts for a business")
    public ResponseEntity<List<BusinessContactDTO>> getContactsByBusiness(
            @PathVariable UUID businessId) {
        return ResponseEntity.ok(contactService.getContactsByBusinessId(businessId));
    }

    @PostMapping("/businesses/{businessId}/contacts")
    @Operation(summary = "Create a contact for a business (without Evolution API instance)")
    public ResponseEntity<BusinessContactDTO> createContact(
            @PathVariable UUID businessId, @Valid @RequestBody CreateBusinessContactDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactService.createContact(businessId, createDTO));
    }

    @GetMapping("/contacts/{contactId}")
    @Operation(summary = "Get a contact by ID")
    public ResponseEntity<BusinessContactDTO> getContact(@PathVariable Long contactId) {
        return ResponseEntity.ok(contactService.getContactById(contactId));
    }

    @PutMapping("/contacts/{contactId}")
    @Operation(summary = "Update a contact")
    public ResponseEntity<BusinessContactDTO> updateContact(
            @PathVariable Long contactId, @Valid @RequestBody CreateBusinessContactDTO updateDTO) {
        return ResponseEntity.ok(contactService.updateContact(contactId, updateDTO));
    }

    @DeleteMapping("/contacts/{contactId}")
    @Operation(summary = "Delete a contact")
    public ResponseEntity<Void> deleteContact(@PathVariable Long contactId) {
        contactService.deleteContact(contactId);
        return ResponseEntity.noContent().build();
    }
}
