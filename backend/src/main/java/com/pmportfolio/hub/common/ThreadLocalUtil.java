package com.pmportfolio.hub.common;

public class ThreadLocalUtil {
    private static final ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();
    
    public static void setUserInfo(UserInfo userInfo) {
        userInfoThreadLocal.set(userInfo);
    }
    
    public static UserInfo getUserInfo() {
        return userInfoThreadLocal.get();
    }
    
    public static void removeUserInfo() {
        userInfoThreadLocal.remove();
    }
    
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
        
        public UserInfo(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getRole() {
            return role;
        }
    }
}