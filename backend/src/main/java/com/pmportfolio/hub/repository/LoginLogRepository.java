package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    List<LoginLog> findByUserId(Long userId);
    List<LoginLog> findByTenantId(Long tenantId);
    List<LoginLog> findByLoginResult(String loginResult);
}