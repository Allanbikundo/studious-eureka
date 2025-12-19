package com.scalum.starter.repository;

import com.scalum.starter.model.UsageEvent;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageEventRepository extends JpaRepository<UsageEvent, Long> {

    List<UsageEvent> findByBusinessIdAndTimestampBetween(UUID businessId, LocalDateTime start, LocalDateTime end);
}
