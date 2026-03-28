package com.pmportfolio.hub.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "login_log")
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "login_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date loginTime;

    @Column(name = "login_ip", nullable = false)
    private String loginIp;

    @Column(name = "login_device", length = 512)
    private String loginDevice;

    @Column(name = "login_result", nullable = false)
    private String loginResult;

    @Column(name = "fail_reason")
    private String failReason;

    @Column(name = "username")
    private String username;

    @PrePersist
    protected void onCreate() {
        loginTime = new Date();
    }
}