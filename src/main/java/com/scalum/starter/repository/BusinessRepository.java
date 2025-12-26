package com.scalum.starter.repository;

import com.scalum.starter.model.Business;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business, UUID> {

    List<Business> findByParentId(UUID parentId);

    @Query(
            value = "SELECT * FROM business WHERE tree_path <@ CAST(:treePath AS ltree)",
            nativeQuery = true)
    List<Business> findSubtree(String treePath);

    Optional<Business> findByBusinessName(String businessName);

    List<Business> findByCreatedByUserId(UUID createdByUserId);
}
