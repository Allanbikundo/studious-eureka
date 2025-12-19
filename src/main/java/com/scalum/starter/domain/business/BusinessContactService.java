package com.scalum.starter.domain.business;

import com.scalum.starter.dto.BusinessContactDTO;
import com.scalum.starter.dto.CreateBusinessContactDTO;
import com.scalum.starter.model.Business;
import com.scalum.starter.model.BusinessContact;
import com.scalum.starter.model.BusinessContactProperty;
import com.scalum.starter.model.ContactPropertyKey;
import com.scalum.starter.repository.BusinessContactPropertyRepository;
import com.scalum.starter.repository.BusinessContactRepository;
import com.scalum.starter.repository.BusinessRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BusinessContactService {

    private final BusinessContactRepository contactRepository;
    private final BusinessContactPropertyRepository propertyRepository;
    private final BusinessRepository businessRepository;

    @Transactional(readOnly = true)
    public List<BusinessContactDTO> getContactsByBusinessId(UUID businessId) {
        if (!businessRepository.existsById(businessId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found");
        }
        return contactRepository.findByBusinessId(businessId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusinessContactDTO getContactById(Long id) {
        return contactRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Transactional
    public BusinessContactDTO createContact(UUID businessId, CreateBusinessContactDTO createDTO) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found"));

        BusinessContact contact = new BusinessContact();
        contact.setBusiness(business);
        contact.setType(createDTO.getType());
        contact.setValue(createDTO.getValue());
        contact.setLabel(createDTO.getLabel());
        contact.setPrimary(createDTO.isPrimary());

        BusinessContact savedContact = contactRepository.save(contact);

        if (createDTO.getProperties() != null) {
            List<BusinessContactProperty> properties = createDTO.getProperties().entrySet().stream()
                    .map(entry -> {
                        BusinessContactProperty property = new BusinessContactProperty();
                        property.setContact(savedContact);
                        property.setKey(entry.getKey());
                        property.setValue(entry.getValue());
                        return property;
                    })
                    .collect(Collectors.toList());
            propertyRepository.saveAll(properties);
            savedContact.setProperties(properties);
        }

        return convertToDTO(savedContact);
    }

    @Transactional
    public BusinessContactDTO updateContact(Long id, CreateBusinessContactDTO updateDTO) {
        BusinessContact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setType(updateDTO.getType());
        contact.setValue(updateDTO.getValue());
        contact.setLabel(updateDTO.getLabel());
        contact.setPrimary(updateDTO.isPrimary());

        // Update properties
        if (updateDTO.getProperties() != null) {
            // Remove existing properties not in the new map? Or merge?
            // Usually full update replaces everything.
            // But here we have a list in entity.
            
            // Simple approach: clear and re-add. 
            // Note: orphanRemoval=true on entity handles deletion.
            contact.getProperties().clear();
            
            updateDTO.getProperties().forEach((key, value) -> {
                BusinessContactProperty property = new BusinessContactProperty();
                property.setContact(contact);
                property.setKey(key);
                property.setValue(value);
                contact.getProperties().add(property);
            });
        }

        BusinessContact savedContact = contactRepository.save(contact);
        return convertToDTO(savedContact);
    }

    @Transactional
    public void deleteContact(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        }
        contactRepository.deleteById(id);
    }

    private BusinessContactDTO convertToDTO(BusinessContact contact) {
        BusinessContactDTO dto = new BusinessContactDTO();
        dto.setId(contact.getId());
        dto.setBusinessId(contact.getBusiness().getId());
        dto.setType(contact.getType());
        dto.setValue(contact.getValue());
        dto.setLabel(contact.getLabel());
        dto.setPrimary(contact.isPrimary());
        
        if (contact.getProperties() != null) {
            Map<ContactPropertyKey, String> properties = contact.getProperties().stream()
                    .collect(Collectors.toMap(BusinessContactProperty::getKey, BusinessContactProperty::getValue));
            dto.setProperties(properties);
        }
        
        return dto;
    }
}
