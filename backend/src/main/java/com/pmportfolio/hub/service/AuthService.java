package com.pmportfolio.hub.service;

import com.pmportfolio.hub.common.ThreadLocalUtil;
import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.model.LoginLog;
import com.pmportfolio.hub.repository.UserRepository;
import com.pmportfolio.hub.repository.LoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginLogRepository loginLogRepository;
    
    public SysUser login(String username, String password, String ip, String device) {
        LoginLog loginLog = new LoginLog();
        loginLog.setLoginTime(new Date());
        loginLog.setLoginIp(ip);
        loginLog.setLoginDevice(device);
        loginLog.setUsername(username);
        
        try {
            SysUser user = userRepository.findByUsername(username);
            if (user == null) {
                loginLog.setLoginResult("fail");
                loginLog.setFailReason("用户名或密码错误");
                loginLogRepository.save(loginLog);
                throw new RuntimeException("用户名或密码错误");
            }
            
            if (!user.getPassword().equals(password)) {
                loginLog.setLoginResult("fail");
                loginLog.setFailReason("用户名或密码错误");
                loginLogRepository.save(loginLog);
                throw new RuntimeException("用户名或密码错误");
            }
            
            if (user.getExpireTime().before(new Date())) {
                loginLog.setLoginResult("fail");
                loginLog.setFailReason("账号已过期");
                loginLogRepository.save(loginLog);
                throw new RuntimeException("账号已过期");
            }
            
            // 登录成功
            loginLog.setLoginResult("success");
            loginLog.setUserId(user.getId());
            loginLogRepository.save(loginLog);
            
            // 登录成功后将用户信息存入ThreadLocal
            ThreadLocalUtil.setUserInfo(new ThreadLocalUtil.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            ));
            
            return user;
        } catch (Exception e) {
            if (loginLog.getLoginResult() == null) {
                loginLog.setLoginResult("fail");
                loginLog.setFailReason(e.getMessage());
                loginLogRepository.save(loginLog);
            }
            throw e;
        }
    }
}