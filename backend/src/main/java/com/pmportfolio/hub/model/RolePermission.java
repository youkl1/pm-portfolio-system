package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "sys_role_permission", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "permission_id"})
})
@Schema(name = "角色权限关联", description = "角色与权限的关联关系")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "关联ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @Schema(description = "角色ID", required = true)
    private Role role;
    
    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    @Schema(description = "权限ID", required = true)
    private Permission permission;
}