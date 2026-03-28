package com.pmportfolio.hub.service;

import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.model.Role;

import java.util.List;

public interface UserRoleService {
    void assignRoles(Long userId, List<Long> roleIds);
    List<Role> getUserRoles(Long userId);
    List<SysUser> getUsersByRole(Long roleId);
}