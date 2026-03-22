package com.pmportfolio.hub.model;

import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Entity
@Table(name = "pm_resume")
@Schema(name = "个人简历", description = "产品经理个人简历信息")
public class PmResume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "简历ID")
    private Long id;
    
    @Column(name = "name", nullable = false)
    @Schema(description = "姓名", required = true)
    private String name;
    
    @Column(name = "email")
    @Schema(description = "邮箱")
    private String email;
    
    @Column(name = "phone")
    @Schema(description = "电话")
    private String phone;
    
    @Column(name = "education")
    @Schema(description = "教育背景")
    private String education;
    
    @Column(name = "work_experience")
    @Schema(description = "工作经验")
    private String workExperience;
    
    @Column(name = "skills")
    @Schema(description = "技能")
    private String skills;
    
    @Column(name = "projects")
    @Schema(description = "项目经验")
    private String projects;
    
    @Column(name = "self_introduction")
    @Schema(description = "自我介绍")
    private String selfIntroduction;
    
    @Column(name = "resume_file")
    @Schema(description = "简历文件URL")
    private String resumeFile;
    
    @Column(name = "resume_file_name")
    @Schema(description = "简历文件名")
    private String resumeFileName;
    
    @Column(name = "gender")
    @Schema(description = "性别")
    private String gender;
    
    @Column(name = "birth_date")
    @Schema(description = "出生年月")
    private String birthDate;
    
    @Column(name = "work_start_date")
    @Schema(description = "参加工作时间")
    private String workStartDate;
    
    @Column(name = "job_status")
    @Schema(description = "求职状态")
    private String jobStatus;
    
    @Column(name = "user_type")
    @Schema(description = "牛人身份")
    private String userType;
    
    @Column(name = "wechat")
    @Schema(description = "微信号")
    private String wechat;
    
    @Column(name = "personal_advantage", columnDefinition = "TEXT")
    @Schema(description = "个人优势")
    private String personalAdvantage;
    
    @Column(name = "expected_position", columnDefinition = "TEXT")
    @Schema(description = "期望职位")
    private String expectedPosition;
    
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