package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.RolePermission;
import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRole(Role role);
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    void deleteByRole(Role role);
}