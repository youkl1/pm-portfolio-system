package com.pmportfolio.hub.service;

import com.pmportfolio.hub.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Optional<Permission> getPermissionById(Long id);
    Optional<Permission> getPermissionByCode(String permissionCode);
    Permission createPermission(Permission permission);
    Permission updatePermission(Permission permission);
    void deletePermission(Long id);
}