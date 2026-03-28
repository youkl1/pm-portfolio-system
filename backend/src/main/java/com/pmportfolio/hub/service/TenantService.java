package com.pmportfolio.hub.service;

import com.pmportfolio.hub.model.Tenant;
import com.pmportfolio.hub.model.TenantPackage;

import java.util.List;

public interface TenantService {
    Tenant createTenant(Tenant tenant);
    Tenant updateTenant(Tenant tenant);
    void deleteTenant(Long id);
    Tenant getTenantById(Long id);
    List<Tenant> getAllTenants();
    List<Tenant> getTenantsByStatus(String status);
    List<Tenant> getTenantsByType(String type);
    Tenant getTenantByName(String name);

    TenantPackage createPackage(TenantPackage tenantPackage);
    TenantPackage updatePackage(TenantPackage tenantPackage);
    void deletePackage(Long id);
    TenantPackage getPackageById(Long id);
    List<TenantPackage> getAllPackages();
    TenantPackage getPackageByName(String name);
}