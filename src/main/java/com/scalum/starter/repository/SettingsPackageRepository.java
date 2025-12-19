package com.scalum.starter.repository;

import com.scalum.starter.model.SettingsPackage;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsPackageRepository extends JpaRepository<SettingsPackage, Long> {

    Optional<SettingsPackage> findByPackageName(String packageName);
}
