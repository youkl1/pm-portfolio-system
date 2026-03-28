package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "sys_user")
@Schema(name = "用户", description = "系统用户信息")
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID")
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true)
    @Schema(description = "用户名", required = true)
    private String username;
    
    @Column(name = "password", nullable = false)
    @Schema(description = "密码", required = true)
    private String password;
    
    @Column(name = "role", nullable = false)
    @Schema(description = "角色", required = true)
    private String role;
    
    @Column(name = "expire_time", nullable = false)
    @Schema(description = "过期时间", required = true)
    private Date expireTime;
    
    @Column(name = "tenant_id", nullable = false)
    @Schema(description = "租户ID")
    private Long tenantId;

    @Column(name = "is_admin", nullable = false, columnDefinition = "boolean default false")
    @Schema(description = "是否为租户管理员")
    private Boolean isAdmin;

    @Column(name = "status", nullable = false, columnDefinition = "varchar(20) default 'active'")
    @Schema(description = "用户状态")
    private String status;

    @Column(name = "last_login_time")
    @Schema(description = "最后登录时间")
    private Date lastLoginTime;

    @Column(name = "last_login_ip")
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}