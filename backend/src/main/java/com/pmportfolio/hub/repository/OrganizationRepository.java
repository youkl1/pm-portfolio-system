package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findByTenantId(Long tenantId);
    List<Organization> findByParentId(Organization parentId);
    List<Organization> findByParentIdIsNull();
}