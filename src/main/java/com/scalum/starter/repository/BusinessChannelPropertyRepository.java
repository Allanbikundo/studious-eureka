package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessChannelProperty;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessChannelPropertyRepository
        extends JpaRepository<BusinessChannelProperty, Long> {
    List<BusinessChannelProperty> findByChannelId(Long channelId);
}
