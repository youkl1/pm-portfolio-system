package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_permission")
@Schema(name = "权限", description = "系统权限信息")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "权限ID")
    private Long id;
    
    @Column(name = "permission_code", nullable = false, unique = true)
    @Schema(description = "权限编码", required = true)
    private String permissionCode;
    
    @Column(name = "permission_name", nullable = false)
    @Schema(description = "权限名称", required = true)
    private String permissionName;
    
    @Column(name = "description")
    @Schema(description = "权限描述")
    private String description;
    
    @Column(name = "permission_type", nullable = false, columnDefinition = "varchar(20) default 'function'")
    @Schema(description = "权限类型")
    private String permissionType;
    
    @Column(name = "is_enabled", nullable = false)
    @Schema(description = "是否启用", required = true)
    private Boolean isEnabled = true;

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