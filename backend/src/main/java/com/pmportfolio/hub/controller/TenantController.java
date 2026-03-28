package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.model.Tenant;
import com.pmportfolio.hub.model.TenantPackage;
import com.pmportfolio.hub.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    // 租户管理
    @PostMapping("/create")
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantService.createTenant(tenant);
    }

    @PutMapping("/update")
    public Tenant updateTenant(@RequestBody Tenant tenant) {
        return tenantService.updateTenant(tenant);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
    }

    @GetMapping("/get/{id}")
    public Tenant getTenantById(@PathVariable Long id) {
        return tenantService.getTenantById(id);
    }

    @GetMapping("/list")
    public List<Tenant> getAllTenants() {
        return tenantService.getAllTenants();
    }

    @GetMapping("/list/status/{status}")
    public List<Tenant> getTenantsByStatus(@PathVariable String status) {
        return tenantService.getTenantsByStatus(status);
    }

    @GetMapping("/list/type/{type}")
    public List<Tenant> getTenantsByType(@PathVariable String type) {
        return tenantService.getTenantsByType(type);
    }

    @GetMapping("/get/name/{name}")
    public Tenant getTenantByName(@PathVariable String name) {
        return tenantService.getTenantByName(name);
    }

    // 套餐管理
    @PostMapping("/package/create")
    public TenantPackage createPackage(@RequestBody TenantPackage tenantPackage) {
        return tenantService.createPackage(tenantPackage);
    }

    @PutMapping("/package/update")
    public TenantPackage updatePackage(@RequestBody TenantPackage tenantPackage) {
        return tenantService.updatePackage(tenantPackage);
    }

    @DeleteMapping("/package/delete/{id}")
    public void deletePackage(@PathVariable Long id) {
        tenantService.deletePackage(id);
    }

    @GetMapping("/package/get/{id}")
    public TenantPackage getPackageById(@PathVariable Long id) {
        return tenantService.getPackageById(id);
    }

    @GetMapping("/package/list")
    public List<TenantPackage> getAllPackages() {
        return tenantService.getAllPackages();
    }

    @GetMapping("/package/get/name/{name}")
    public TenantPackage getPackageByName(@PathVariable String name) {
        return tenantService.getPackageByName(name);
    }
}