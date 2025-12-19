package com.scalum.starter.repository;

import com.scalum.starter.model.UsageRollup;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageRollupRepository extends JpaRepository<UsageRollup, Long> {

    Optional<UsageRollup> findByRootBusinessIdAndPeriod(UUID rootBusinessId, LocalDate period);
}
