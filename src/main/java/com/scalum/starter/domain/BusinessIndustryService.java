package com.scalum.starter.domain;

import com.scalum.starter.dto.BusinessIndustryRequest;
import com.scalum.starter.dto.BusinessIndustryResponse;
import com.scalum.starter.model.BusinessIndustry;
import com.scalum.starter.repository.BusinessIndustryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BusinessIndustryService {

    private final BusinessIndustryRepository repository;

    @Transactional
    public BusinessIndustryResponse create(BusinessIndustryRequest request) {
        BusinessIndustry entity = new BusinessIndustry();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity = repository.save(entity);
        return mapToResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<BusinessIndustryResponse> findAll() {
        return repository.findAll().stream().map(this::mapToResponse).toList();
    }

    private BusinessIndustryResponse mapToResponse(BusinessIndustry entity) {
        BusinessIndustryResponse response = new BusinessIndustryResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        return response;
    }
}
