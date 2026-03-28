package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Data
@Entity
@Table(name = "sys_user_role", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "role_id"})
})
@Schema(name = "用户角色关联", description = "用户与角色的关联关系")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "关联ID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "用户ID", required = true)
    private SysUser user;
    
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @Schema(description = "角色ID", required = true)
    private Role role;

    @Column(name = "effective_time")
    @Schema(description = "生效时间")
    private Date effectiveTime;

    @Column(name = "expire_time")
    @Schema(description = "失效时间")
    private Date expireTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "创建时间")
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}