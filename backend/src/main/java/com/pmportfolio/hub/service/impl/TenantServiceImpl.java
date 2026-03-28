package com.pmportfolio.hub.service.impl;

import com.pmportfolio.hub.model.Tenant;
import com.pmportfolio.hub.model.TenantPackage;
import com.pmportfolio.hub.repository.TenantPackageRepository;
import com.pmportfolio.hub.repository.TenantRepository;
import com.pmportfolio.hub.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantServiceImpl implements TenantService {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private TenantPackageRepository tenantPackageRepository;

    @Override
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant updateTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }

    @Override
    public Tenant getTenantById(Long id) {
        return tenantRepository.findById(id).orElse(null);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    public List<Tenant> getTenantsByStatus(String status) {
        return tenantRepository.findByStatus(status);
    }

    @Override
    public List<Tenant> getTenantsByType(String type) {
        return tenantRepository.findByTenantType(type);
    }

    @Override
    public Tenant getTenantByName(String name) {
        return tenantRepository.findByTenantName(name);
    }

    @Override
    public TenantPackage createPackage(TenantPackage tenantPackage) {
        return tenantPackageRepository.save(tenantPackage);
    }

    @Override
    public TenantPackage updatePackage(TenantPackage tenantPackage) {
        return tenantPackageRepository.save(tenantPackage);
    }

    @Override
    public void deletePackage(Long id) {
        tenantPackageRepository.deleteById(id);
    }

    @Override
    public TenantPackage getPackageById(Long id) {
        return tenantPackageRepository.findById(id).orElse(null);
    }

    @Override
    public List<TenantPackage> getAllPackages() {
        return tenantPackageRepository.findAll();
    }

    @Override
    public TenantPackage getPackageByName(String name) {
        return tenantPackageRepository.findByPackageName(name);
    }
}