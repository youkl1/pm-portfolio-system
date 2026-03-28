package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.TenantPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantPackageRepository extends JpaRepository<TenantPackage, Long> {
    TenantPackage findByPackageName(String packageName);
}