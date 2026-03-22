package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "pm_project")
@Schema(name = "作品", description = "产品经理个人作品信息")
public class PmProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "作品ID")
    private Long id;
    
    @Column(name = "title", nullable = false)
    @Schema(description = "作品标题", required = true)
    private String title;
    
    @Column(name = "description")
    @Schema(description = "作品描述")
    private String description;
    
    @Column(name = "cover_image")
    @Schema(description = "封面图片URL")
    private String coverImage;
    
    @Column(name = "detail_link")
    @Schema(description = "详情链接")
    private String detailLink;
    
    @Column(name = "github_link")
    @Schema(description = "GitHub地址")
    private String githubLink;
    
    @Column(name = "sort", nullable = false)
    @Schema(description = "排序值", required = true)
    private Integer sort;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private Date createdAt;
    
    @Column(name = "updated_at", nullable = false)
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