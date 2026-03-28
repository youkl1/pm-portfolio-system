package com.pmportfolio.hub.service.impl;

import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.Permission;
import com.pmportfolio.hub.model.RolePermission;
import com.pmportfolio.hub.repository.RoleRepository;
import com.pmportfolio.hub.repository.PermissionRepository;
import com.pmportfolio.hub.repository.RolePermissionRepository;
import com.pmportfolio.hub.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        
        // 先删除角色的所有权限
        rolePermissionRepository.deleteByRole(role);
        
        // 再添加新的权限
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId).orElseThrow(() -> new RuntimeException("Permission not found"));
            
            // 检查是否已存在该角色和权限的关联
            if (rolePermissionRepository.findByRoleAndPermission(role, permission).isEmpty()) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRole(role);
                rolePermission.setPermission(permission);
                rolePermissionRepository.save(rolePermission);
            }
        }
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        List<RolePermission> rolePermissions = rolePermissionRepository.findByRole(role);
        return rolePermissions.stream().map(RolePermission::getPermission).toList();
    }
}