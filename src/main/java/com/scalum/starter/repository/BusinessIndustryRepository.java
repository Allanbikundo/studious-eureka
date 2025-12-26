package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessIndustry;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessIndustryRepository extends JpaRepository<BusinessIndustry, UUID> {}
