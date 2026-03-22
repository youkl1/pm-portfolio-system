package com.pmportfolio.hub.service;

import com.pmportfolio.hub.common.ThreadLocalUtil;
import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    
    public SysUser login(String username, String password) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getExpireTime().before(new Date())) {
            throw new RuntimeException("账号已过期");
        }
        
        // 登录成功后将用户信息存入ThreadLocal
        ThreadLocalUtil.setUserInfo(new ThreadLocalUtil.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRole()
        ));
        
        return user;
    }
}