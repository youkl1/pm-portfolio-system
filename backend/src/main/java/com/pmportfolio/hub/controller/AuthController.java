package com.pmportfolio.hub.controller;

import com.pmportfolio.hub.common.CaptchaUtil;
import com.pmportfolio.hub.common.ThreadLocalUtil;
import com.pmportfolio.hub.model.SysUser;
import com.pmportfolio.hub.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    
    // 获取验证码
    @GetMapping("/captcha")
    public Map<String, Object> getCaptcha(HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 生成验证码
            String captcha = CaptchaUtil.generateCaptcha();
            // 生成验证码图片
            String captchaImage = CaptchaUtil.generateCaptchaImage(captcha);
            // 将验证码存入session
            httpRequest.getSession().setAttribute("captcha", captcha);
            
            Map<String, String> data = new HashMap<>();
            data.put("image", captchaImage);
            
            response.put("code", 200);
            response.put("message", "成功");
            response.put("data", data);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "生成验证码失败");
            response.put("data", null);
        }
        return response;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = request.get("username");
            String password = request.get("password");
            String captcha = request.get("captcha");
            
            // 验证验证码
            String sessionCaptcha = (String) httpRequest.getSession().getAttribute("captcha");
            if (!CaptchaUtil.validateCaptcha(captcha, sessionCaptcha)) {
                response.put("code", 400);
                response.put("message", "验证码错误");
                response.put("data", null);
                return response;
            }
            
            // 获取IP和设备信息
            String ip = httpRequest.getRemoteAddr();
            String device = httpRequest.getHeader("User-Agent");
            
            SysUser user = authService.login(username, password, ip, device);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("role", user.getRole());
            
            // 将用户信息存入session
            ThreadLocalUtil.UserInfo sessionUserInfo = new ThreadLocalUtil.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            httpRequest.getSession().setAttribute("userInfo", sessionUserInfo);
            
            response.put("code", 200);
            response.put("message", "登录成功");
            response.put("data", userInfo);
        } catch (Exception e) {
            response.put("code", 400);
            response.put("message", e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}