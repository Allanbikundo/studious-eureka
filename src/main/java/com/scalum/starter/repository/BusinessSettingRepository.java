package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessSetting;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessSettingRepository extends JpaRepository<BusinessSetting, Long> {
    List<BusinessSetting> findByBusinessId(UUID businessId);

    Optional<BusinessSetting> findByBusinessIdAndSettingKey(UUID businessId, String key);
}
