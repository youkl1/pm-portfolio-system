package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SysUser, Long> {
    SysUser findByUsername(String username);
}