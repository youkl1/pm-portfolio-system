package com.pmportfolio.hub.repository;

import com.pmportfolio.hub.model.UserRole;
import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(SysUser user);
    List<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(SysUser user, Role role);
    void deleteByUser(SysUser user);
}