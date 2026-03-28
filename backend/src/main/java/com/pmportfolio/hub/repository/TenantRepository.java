package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    List<Tenant> findByStatus(String status);
    List<Tenant> findByTenantType(String tenantType);
    Tenant findByTenantName(String tenantName);
}