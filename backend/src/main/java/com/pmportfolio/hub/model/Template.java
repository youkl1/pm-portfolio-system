package com.pmportfolio.hub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Entity
@Table(name = "template")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "format", nullable = false)
    private String format;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_hash")
    private String contentHash;

    @Column(name = "is_encrypted", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isEncrypted = true;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private SysUser createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private SysUser updatedBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Date updatedAt;

    @ManyToMany
    @JoinTable(
        name = "template_category_relation",
        joinColumns = @JoinColumn(name = "template_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<TemplateCategory> categories;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<TemplateVersion> versions;

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
