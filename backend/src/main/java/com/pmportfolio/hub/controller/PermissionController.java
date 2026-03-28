package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.common.PermissionCheck;
import com.pmportfolio.hub.model.Role;
import com.pmportfolio.hub.model.Permission;
import com.pmportfolio.hub.service.RoleService;
import com.pmportfolio.hub.service.PermissionService;
import com.pmportfolio.hub.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permission")
@Tag(name = "权限管理", description = "权限管理相关接口")
public class PermissionController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserRoleService userRoleService;

    // 角色管理
    @Operation(summary = "获取所有角色", description = "获取系统中所有的角色")
    @GetMapping("/roles")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详情")
    @GetMapping("/roles/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Role> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "创建角色", description = "创建新的角色")
    @PostMapping("/roles")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Role> createRole(@Parameter(description = "角色信息") @RequestBody Map<String, Object> roleData) {
        Role role = new Role();
        role.setRoleName((String) roleData.get("roleName"));
        role.setDescription((String) roleData.get("description"));
        role.setIsEnabled((Boolean) roleData.getOrDefault("isEnabled", true));
        
        Role createdRole = roleService.createRole(role);
        
        // 分配权限
        List<?> permissionIdObjects = (List<?>) roleData.get("permissionIds");
        List<Long> permissionIds = convertToLongList(permissionIdObjects);
        if (permissionIds != null && !permissionIds.isEmpty()) {
            roleService.assignPermissions(createdRole.getId(), permissionIds);
        }
        
        return ResponseEntity.ok(createdRole);
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping("/roles/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Role> updateRole(@Parameter(description = "角色ID") @PathVariable Long id, @Parameter(description = "角色信息") @RequestBody Map<String, Object> roleData) {
        Role role = new Role();
        role.setId(id);
        role.setRoleName((String) roleData.get("roleName"));
        role.setDescription((String) roleData.get("description"));
        role.setIsEnabled((Boolean) roleData.getOrDefault("isEnabled", true));
        
        Role updatedRole = roleService.updateRole(role);
        
        // 分配权限
        List<?> permissionIdObjects = (List<?>) roleData.get("permissionIds");
        List<Long> permissionIds = convertToLongList(permissionIdObjects);
        if (permissionIds != null) {
            roleService.assignPermissions(updatedRole.getId(), permissionIds);
        }
        
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @DeleteMapping("/roles/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // 权限管理
    @Operation(summary = "获取所有权限", description = "获取系统中所有的权限")
    @GetMapping("/permissions")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(summary = "获取权限详情", description = "根据权限ID获取权限详情")
    @GetMapping("/permissions/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Permission> getPermissionById(@Parameter(description = "权限ID") @PathVariable Long id) {
        return permissionService.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "创建权限", description = "创建新的权限")
    @PostMapping("/permissions")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Permission> createPermission(@Parameter(description = "权限信息") @RequestBody Permission permission) {
        return ResponseEntity.ok(permissionService.createPermission(permission));
    }

    @Operation(summary = "更新权限", description = "更新权限信息")
    @PutMapping("/permissions/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Permission> updatePermission(@Parameter(description = "权限ID") @PathVariable Long id, @Parameter(description = "权限信息") @RequestBody Permission permission) {
        permission.setId(id);
        return ResponseEntity.ok(permissionService.updatePermission(permission));
    }

    @Operation(summary = "删除权限", description = "根据权限ID删除权限")
    @DeleteMapping("/permissions/{id}")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Void> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    // 角色权限管理
    @Operation(summary = "为角色分配权限", description = "为指定角色分配权限")
    @PostMapping("/roles/{roleId}/permissions")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Void> assignPermissions(@Parameter(description = "角色ID") @PathVariable Long roleId, @Parameter(description = "权限ID列表") @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    @GetMapping("/roles/{roleId}/permissions")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<List<Permission>> getRolePermissions(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRolePermissions(roleId));
    }

    // 用户角色管理
    @Operation(summary = "为用户分配角色", description = "为指定用户分配角色")
    @PostMapping("/users/{userId}/roles")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<Void> assignRoles(@Parameter(description = "用户ID") @PathVariable Long userId, @Parameter(description = "角色ID列表") @RequestBody List<Long> roleIds) {
        userRoleService.assignRoles(userId, roleIds);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取用户角色", description = "获取指定用户的角色列表")
    @GetMapping("/users/{userId}/roles")
    @PermissionCheck(roles = {"admin"})
    public ResponseEntity<List<Role>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.getUserRoles(userId));
    }

    /**
     * 将对象列表转换为Long列表，处理Integer和String类型
     */
    private List<Long> convertToLongList(List<?> objects) {
        if (objects == null) {
            return null;
        }
        return objects.stream()
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    } else if (obj instanceof Long) {
                        return (Long) obj;
                    } else if (obj instanceof String) {
                        return Long.parseLong((String) obj);
                    }
                    return null;
                })
                .filter(obj -> obj != null)
                .toList();
    }
}