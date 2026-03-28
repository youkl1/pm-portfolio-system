package com.pmportfolio.hub.service;

import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.Permission;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(String roleName);
    Role createRole(Role role);
    Role updateRole(Role role);
    void deleteRole(Long id);
    void assignPermissions(Long roleId, List<Long> permissionIds);
    List<Permission> getRolePermissions(Long roleId);
}