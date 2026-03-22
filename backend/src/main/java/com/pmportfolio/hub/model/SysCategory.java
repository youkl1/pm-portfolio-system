package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "sys_category")
@Schema(name = "系统分类", description = "系统分类信息")
public class SysCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "分类ID")
    private Long id;
    
    @Column(name = "name", nullable = false)
    @Schema(description = "分类名称", required = true)
    private String name;
    
    @Column(name = "description")
    @Schema(description = "分类描述")
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
