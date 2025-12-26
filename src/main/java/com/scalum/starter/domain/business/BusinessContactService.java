package com.scalum.starter.domain.business;

import com.scalum.starter.domain.evolution.EvolutionApiClient;
import com.scalum.starter.domain.evolution.EvolutionWebhookEvent;
import com.scalum.starter.domain.evolution.dto.ConnectInstanceResponse;
import com.scalum.starter.domain.evolution.dto.CreateInstanceResponse;
import com.scalum.starter.dto.*;
import com.scalum.starter.model.Business;
import com.scalum.starter.model.BusinessContact;
import com.scalum.starter.model.BusinessContactProperty;
import com.scalum.starter.model.ContactPropertyKey;
import com.scalum.starter.repository.BusinessContactPropertyRepository;
import com.scalum.starter.repository.BusinessContactRepository;
import com.scalum.starter.repository.BusinessRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
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
    private final EvolutionApiClient evolutionApiClient;

    @Value("${app.webhook-url}")
    private String appWebhookUrl;

    @Transactional
    public BusinessContactWithInstanceDTO createContactAndInstance(
            UUID businessId, CreateInstanceAndContactDTO createDTO) {
        String instanceName =
                "biz_"
                        + businessId.toString().replaceAll("-", "").substring(0, 10)
                        + "_"
                        + createDTO.getValue().replaceAll("[^0-9]", "");
        String instanceToken = UUID.randomUUID().toString();

        CreateInstanceResponse instanceResponse =
                evolutionApiClient.createInstance(instanceName, instanceToken);

        if (instanceResponse.getInstance() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve instance details from Evolution API after creation.");
        }

        // Set default webhook
        if (appWebhookUrl != null && !appWebhookUrl.isBlank()) {
            evolutionApiClient.setWebhook(
                    instanceName,
                    appWebhookUrl,
                    List.of(
                            EvolutionWebhookEvent.MESSAGES_UPSERT,
                            EvolutionWebhookEvent.CONNECTION_UPDATE));
        }

        Map<ContactPropertyKey, String> properties = new HashMap<>();
        properties.put(
                ContactPropertyKey.INSTANCE_ID, instanceResponse.getInstance().getInstanceName());
        properties.put(ContactPropertyKey.API_TOKEN, instanceResponse.getHash());

        CreateBusinessContactDTO contactDTO = new CreateBusinessContactDTO();
        contactDTO.setType(createDTO.getType());
        contactDTO.setValue(createDTO.getValue());
        contactDTO.setLabel(createDTO.getLabel());
        contactDTO.setPrimary(createDTO.isPrimary());
        contactDTO.setProperties(properties);

        BusinessContactDTO createdContact = createContact(businessId, contactDTO);

        BusinessContactWithInstanceDTO resultDTO = new BusinessContactWithInstanceDTO();
        BeanUtils.copyProperties(createdContact, resultDTO);
        resultDTO.setInstanceName(instanceResponse.getInstance().getInstanceName());
        resultDTO.setInstanceStatus(instanceResponse.getInstance().getStatus());

        return resultDTO;
    }

    @Transactional(readOnly = true)
    public BusinessContactWithQrDTO connectContactInstance(Long contactId) {
        BusinessContact contact =
                contactRepository
                        .findById(contactId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Contact not found"));

        String instanceName =
                contact.getProperties().stream()
                        .filter(p -> p.getKey() == ContactPropertyKey.INSTANCE_ID)
                        .findFirst()
                        .map(BusinessContactProperty::getValue)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Instance ID not configured for this contact"));

        ConnectInstanceResponse connectResponse =
                evolutionApiClient
                        .connectInstance(instanceName)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Instance not found in Evolution API"));

        BusinessContactDTO contactDTO = convertToDTO(contact);
        BusinessContactWithQrDTO resultDTO = new BusinessContactWithQrDTO();
        BeanUtils.copyProperties(contactDTO, resultDTO);

        CreateInstanceResponse.Qrcode qrCode = new CreateInstanceResponse.Qrcode();
        qrCode.setBase64(connectResponse.getBase64());
        qrCode.setCode(connectResponse.getCode());

        resultDTO.setQrcode(qrCode);
        resultDTO.setInstanceName(instanceName);

        return resultDTO;
    }

    @Transactional
    public void setWebhookForContact(Long contactId, SetWebhookDTO webhookDTO) {
        BusinessContact contact =
                contactRepository
                        .findById(contactId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Contact not found"));

        String instanceName =
                contact.getProperties().stream()
                        .filter(p -> p.getKey() == ContactPropertyKey.INSTANCE_ID)
                        .findFirst()
                        .map(BusinessContactProperty::getValue)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Instance ID not configured for this contact"));

        evolutionApiClient.setWebhook(
                instanceName, webhookDTO.getWebhookUrl(), webhookDTO.getEvents());
    }

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
        return contactRepository
                .findById(id)
                .map(this::convertToDTO)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Transactional
    public BusinessContactDTO createContact(UUID businessId, CreateBusinessContactDTO createDTO) {
        Business business =
                businessRepository
                        .findById(businessId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Business not found"));

        BusinessContact contact = new BusinessContact();
        contact.setBusiness(business);
        contact.setType(createDTO.getType());
        contact.setValue(createDTO.getValue());
        contact.setLabel(createDTO.getLabel());
        contact.setPrimary(createDTO.isPrimary());
        contact.setConnected(false); // Default to false
        contact.setActive(true); // Default to true

        BusinessContact savedContact = contactRepository.save(contact);

        if (createDTO.getProperties() != null) {
            List<BusinessContactProperty> properties =
                    createDTO.getProperties().entrySet().stream()
                            .map(
                                    entry -> {
                                        BusinessContactProperty property =
                                                new BusinessContactProperty();
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
        BusinessContact contact =
                contactRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setType(updateDTO.getType());
        contact.setValue(updateDTO.getValue());
        contact.setLabel(updateDTO.getLabel());
        contact.setPrimary(updateDTO.isPrimary());

        if (updateDTO.getProperties() != null) {
            contact.getProperties().clear();

            updateDTO
                    .getProperties()
                    .forEach(
                            (key, value) -> {
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
        dto.setConnected(contact.isConnected());
        dto.setActive(contact.isActive());

        if (contact.getProperties() != null) {
            Map<ContactPropertyKey, String> properties =
                    contact.getProperties().stream()
                            .collect(
                                    Collectors.toMap(
                                            BusinessContactProperty::getKey,
                                            BusinessContactProperty::getValue));
            dto.setProperties(properties);
        }

        return dto;
    }
}
