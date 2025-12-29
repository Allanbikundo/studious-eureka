package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessChannel;
import com.scalum.starter.model.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessChannelRepository extends JpaRepository<BusinessChannel, Long> {

    List<BusinessChannel> findByBusinessId(UUID businessId);

    List<BusinessChannel> findByBusinessIdAndType(UUID businessId, ChannelType type);
}
