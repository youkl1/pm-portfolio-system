package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.model.Organization;
import com.pmportfolio.hub.model.UserOrganization;
import com.pmportfolio.hub.repository.OrganizationRepository;
import com.pmportfolio.hub.repository.UserOrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserOrganizationRepository userOrganizationRepository;

    // 组织架构管理
    @PostMapping("/create")
    public Organization createOrganization(@RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @PutMapping("/update")
    public Organization updateOrganization(@RequestBody Organization organization) {
        return organizationRepository.save(organization);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteOrganization(@PathVariable Long id) {
        organizationRepository.deleteById(id);
    }

    @GetMapping("/get/{id}")
    public Organization getOrganizationById(@PathVariable Long id) {
        return organizationRepository.findById(id).orElse(null);
    }

    @GetMapping("/list/tenant/{tenantId}")
    public List<Organization> getOrganizationsByTenant(@PathVariable Long tenantId) {
        return organizationRepository.findByTenantId(tenantId);
    }

    @GetMapping("/list/parent/{parentId}")
    public List<Organization> getOrganizationsByParent(@PathVariable Long parentId) {
        Organization parent = organizationRepository.findById(parentId).orElse(null);
        return organizationRepository.findByParentId(parent);
    }

    @GetMapping("/list/root/{tenantId}")
    public List<Organization> getRootOrganizations(@PathVariable Long tenantId) {
        List<Organization> allOrgs = organizationRepository.findByTenantId(tenantId);
        return allOrgs.stream().filter(org -> org.getParentId() == null).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/tree/{tenantId}")
    public List<Organization> getOrganizationTree(@PathVariable Long tenantId) {
        // 这里可以实现组织架构树的构建逻辑
        return organizationRepository.findByTenantId(tenantId);
    }

    // 用户组织关联管理
    @PostMapping("/user-org/create")
    public UserOrganization createUserOrganization(@RequestBody UserOrganization userOrganization) {
        return userOrganizationRepository.save(userOrganization);
    }

    @DeleteMapping("/user-org/delete/{id}")
    public void deleteUserOrganization(@PathVariable Long id) {
        userOrganizationRepository.deleteById(id);
    }

    @GetMapping("/user-org/list/user/{userId}")
    public List<UserOrganization> getUserOrganizations(@PathVariable Long userId) {
        return userOrganizationRepository.findByUserId(userId);
    }

    @GetMapping("/user-org/list/org/{orgId}")
    public List<UserOrganization> getOrganizationUsers(@PathVariable Long orgId) {
        return userOrganizationRepository.findByOrgId(orgId);
    }
}