package com.scalum.starter.repository;

import com.scalum.starter.model.BusinessUserRole;
import com.scalum.starter.model.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUserRoleRepository extends JpaRepository<BusinessUserRole, Long> {

    Optional<BusinessUserRole> findByUserIdAndBusinessId(UUID userId, UUID businessId);

    List<BusinessUserRole> findByUserId(UUID userId);

    List<BusinessUserRole> findByBusinessId(UUID businessId);

    boolean existsByUserIdAndBusinessIdAndRole(UUID userId, UUID businessId, Role role);
}
