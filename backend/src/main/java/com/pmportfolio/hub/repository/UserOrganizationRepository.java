package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {
    List<UserOrganization> findByUserId(Long userId);
    List<UserOrganization> findByOrgId(Long orgId);
}