package com.pmportfolio.hub.common;

import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ThreadLocalUtil.UserInfo userInfo = (ThreadLocalUtil.UserInfo) request.getSession().getAttribute("userInfo");
        if (userInfo == null) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "未登录");
            result.put("data", null);
            out.write(new ObjectMapper().writeValueAsString(result));
            out.flush();
            out.close();
            return false;
        }
        // 将session中的用户信息存入ThreadLocal，供后续业务逻辑使用
        ThreadLocalUtil.setUserInfo(userInfo);
        return true;
    }
}