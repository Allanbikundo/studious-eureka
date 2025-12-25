package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessContactProperty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessContactPropertyRepository
        extends JpaRepository<BusinessContactProperty, Long> {
    List<BusinessContactProperty> findByContactId(Long contactId);
}
