package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_role")
@Schema(name = "角色", description = "系统角色信息")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "角色ID")
    private Long id;
    
    @Column(name = "tenant_id")
    @Schema(description = "租户ID")
    private Long tenantId;
    
    @Column(name = "role_name", nullable = false, unique = true)
    @Schema(description = "角色名称", required = true)
    private String roleName;
    
    @Column(name = "description")
    @Schema(description = "角色描述")
    private String description;
    
    @Column(name = "is_enabled", nullable = false)
    @Schema(description = "是否启用", required = true)
    private Boolean isEnabled = true;
    
    @Column(name = "is_system", nullable = false, columnDefinition = "boolean default false")
    @Schema(description = "是否为系统角色")
    private Boolean isSystem = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "创建时间")
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "更新时间")
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}