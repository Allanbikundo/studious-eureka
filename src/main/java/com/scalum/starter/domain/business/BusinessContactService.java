package com.scalum.starter.domain.business;

import com.scalum.starter.domain.evolution.EvolutionApiClient;
import com.scalum.starter.domain.evolution.EvolutionWebhookEvent;
import com.scalum.starter.domain.evolution.dto.ConnectInstanceResponse;
import com.scalum.starter.domain.evolution.dto.CreateInstanceResponse;
import com.scalum.starter.dto.*;
import com.scalum.starter.model.Business;
import com.scalum.starter.model.BusinessChannel;
import com.scalum.starter.model.BusinessChannelProperty;
import com.scalum.starter.model.ChannelPropertyKey;
import com.scalum.starter.repository.BusinessChannelPropertyRepository;
import com.scalum.starter.repository.BusinessChannelRepository;
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

    private final BusinessChannelRepository channelRepository;
    private final BusinessChannelPropertyRepository propertyRepository;
    private final BusinessRepository businessRepository;
    private final EvolutionApiClient evolutionApiClient;

    @Value("${app.webhook-url}")
    private String appWebhookUrl;

    @Transactional
    public BusinessChannelWithInstanceDTO createContactAndInstance(
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

        Map<ChannelPropertyKey, String> properties = new HashMap<>();
        properties.put(
                ChannelPropertyKey.INSTANCE_ID, instanceResponse.getInstance().getInstanceName());
        properties.put(ChannelPropertyKey.API_TOKEN, instanceResponse.getHash());

        CreateBusinessChannelDTO contactDTO = new CreateBusinessChannelDTO();
        contactDTO.setType(createDTO.getType());
        contactDTO.setValue(createDTO.getValue());
        contactDTO.setLabel(createDTO.getLabel());
        contactDTO.setPrimary(createDTO.isPrimary());
        contactDTO.setProperties(properties);

        BusinessChannelDTO createdContact = createContact(businessId, contactDTO);

        BusinessChannelWithInstanceDTO resultDTO = new BusinessChannelWithInstanceDTO();
        BeanUtils.copyProperties(createdContact, resultDTO);
        resultDTO.setInstanceName(instanceResponse.getInstance().getInstanceName());
        resultDTO.setInstanceStatus(instanceResponse.getInstance().getStatus());

        return resultDTO;
    }

    @Transactional(readOnly = true)
    public BusinessContactWithQrDTO connectContactInstance(Long contactId) {
        BusinessChannel contact =
                channelRepository
                        .findById(contactId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Contact not found"));

        String instanceName =
                contact.getProperties().stream()
                        .filter(p -> p.getKey() == ChannelPropertyKey.INSTANCE_ID)
                        .findFirst()
                        .map(BusinessChannelProperty::getValue)
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

        BusinessChannelDTO contactDTO = convertToDTO(contact);
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
        BusinessChannel contact =
                channelRepository
                        .findById(contactId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Contact not found"));

        String instanceName =
                contact.getProperties().stream()
                        .filter(p -> p.getKey() == ChannelPropertyKey.INSTANCE_ID)
                        .findFirst()
                        .map(BusinessChannelProperty::getValue)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "Instance ID not configured for this contact"));

        evolutionApiClient.setWebhook(
                instanceName, webhookDTO.getWebhookUrl(), webhookDTO.getEvents());
    }

    @Transactional(readOnly = true)
    public List<BusinessChannelDTO> getContactsByBusinessId(UUID businessId) {
        if (!businessRepository.existsById(businessId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Business not found");
        }
        return channelRepository.findByBusinessId(businessId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusinessChannelDTO getContactById(Long id) {
        return channelRepository
                .findById(id)
                .map(this::convertToDTO)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Contact not found"));
    }

    @Transactional
    public BusinessChannelDTO createContact(UUID businessId, CreateBusinessChannelDTO createDTO) {
        Business business =
                businessRepository
                        .findById(businessId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Business not found"));

        BusinessChannel contact = new BusinessChannel();
        contact.setBusiness(business);
        contact.setType(createDTO.getType());
        contact.setValue(createDTO.getValue());
        contact.setLabel(createDTO.getLabel());
        contact.setPrimary(createDTO.isPrimary());
        contact.setConnected(false); // Default to false
        contact.setActive(true); // Default to true

        BusinessChannel savedChannel = channelRepository.save(contact);

        if (createDTO.getProperties() != null) {
            List<BusinessChannelProperty> properties =
                    createDTO.getProperties().entrySet().stream()
                            .map(
                                    entry -> {
                                        BusinessChannelProperty property =
                                                new BusinessChannelProperty();
                                        property.setChannel(savedChannel);
                                        property.setKey(entry.getKey());
                                        property.setValue(entry.getValue());
                                        return property;
                                    })
                            .collect(Collectors.toList());
            propertyRepository.saveAll(properties);
            savedChannel.setProperties(properties);
        }

        return convertToDTO(savedChannel);
    }

    @Transactional
    public BusinessChannelDTO updateContact(Long id, CreateBusinessChannelDTO updateDTO) {
        BusinessChannel contact =
                channelRepository
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
                                BusinessChannelProperty property = new BusinessChannelProperty();
                                property.setChannel(contact);
                                property.setKey(key);
                                property.setValue(value);
                                contact.getProperties().add(property);
                            });
        }

        BusinessChannel savedContact = channelRepository.save(contact);
        return convertToDTO(savedContact);
    }

    @Transactional
    public void deleteContact(Long id) {
        if (!channelRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found");
        }
        channelRepository.deleteById(id);
    }

    private BusinessChannelDTO convertToDTO(BusinessChannel contact) {
        BusinessChannelDTO dto = new BusinessChannelDTO();
        dto.setId(contact.getId());
        dto.setBusinessId(contact.getBusiness().getId());
        dto.setType(contact.getType());
        dto.setValue(contact.getValue());
        dto.setLabel(contact.getLabel());
        dto.setPrimary(contact.isPrimary());
        dto.setConnected(contact.isConnected());
        dto.setActive(contact.isActive());

        if (contact.getProperties() != null) {
            Map<ChannelPropertyKey, String> properties =
                    contact.getProperties().stream()
                            .collect(
                                    Collectors.toMap(
                                            BusinessChannelProperty::getKey,
                                            BusinessChannelProperty::getValue));
            dto.setProperties(properties);
        }

        return dto;
    }
}
