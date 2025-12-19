package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessContact;
import com.scalum.starter.model.ContactType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessContactRepository extends JpaRepository<BusinessContact, Long> {

    List<BusinessContact> findByBusinessId(UUID businessId);

    List<BusinessContact> findByBusinessIdAndType(UUID businessId, ContactType type);
}
