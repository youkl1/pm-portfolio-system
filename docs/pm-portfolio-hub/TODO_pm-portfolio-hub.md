# 待办事项文档 - 产品经理个人作品展示系统

## 上线前准备

### 1. 数据库准备
- [ ] 在目标服务器上创建pm_portfolio_hub数据库
- [ ] 执行schema.sql脚本初始化表结构和默认数据
- [ ] 验证数据库连接配置是否正确

### 2. 后端部署
- [ ] 编译Spring Boot项目生成jar包
  ```bash
  cd backend
  mvn clean package
  ```
- [ ] 部署jar包到目标服务器
- [ ] 配置application.yml中的数据库连接信息
- [ ] 启动后端服务
  ```bash
  java -jar hub-1.0.0.jar
  ```

### 3. 前端部署
- [ ] 构建前端项目
  ```bash
  cd frontend
  npm install
  npm run build
  ```
- [ ] 将构建后的dist目录部署到Web服务器
- [ ] 配置反向代理，将/api请求转发到后端服务
  - Nginx配置示例：
  ```nginx
  server {
    listen 80;
    server_name example.com;
    
    location / {
      root /path/to/dist;
      index index.html;
      try_files $uri $uri/ /index.html;
    }
    
    location /api {
      proxy_pass http://localhost:8080;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
  }
  ```

### 4. 环境检查
- [ ] 确保JDK 17+已安装
- [ ] 确保MySQL 8.0+已安装并运行
- [ ] 确保Node.js 16+已安装
- [ ] 确保端口8080（后端）和80（前端）已开放

## 运维指引

### 1. 日常维护
- [ ] 定期备份数据库
  ```bash
  mysqldump -u root -p pm_portfolio_hub > backup.sql
  ```
- [ ] 监控系统运行状态
- [ ] 检查日志文件，及时发现并解决问题

### 2. 常见问题处理
- **数据库连接失败**：检查数据库服务是否运行，连接配置是否正确
- **前端页面无法访问**：检查Web服务器是否运行，反向代理配置是否正确
- **后端服务无法启动**：检查端口是否被占用，数据库连接是否正常
- **图片上传失败**：检查文件上传大小限制，确保服务器有足够的磁盘空间

### 3. 系统更新
- **后端更新**：
  1. 停止现有服务
  2. 部署新的jar包
  3. 重启服务

- **前端更新**：
  1. 构建新的前端代码
  2. 替换Web服务器上的dist目录
  3. 清除浏览器缓存

## 安全注意事项
- [ ] 生产环境中实现密码加密存储
- [ ] 配置HTTPS
- [ ] 限制API访问频率，防止暴力攻击
- [ ] 定期更新依赖库，修复安全漏洞

## 功能扩展
- [ ] 实现用户注册和密码重置功能
- [ ] 添加作品详情页
- [ ] 实现图片存储的持久化方案
- [ ] 添加作品分类和标签功能
- [ ] 实现作品搜索功能
- [ ] 添加用户评论功能

## 技术债务
- [ ] 密码存储为明文，需要实现加密
- [ ] 图片上传返回mock URL，需要实现真实的图片存储
- [ ] 缺少单元测试和集成测试
- [ ] 代码中存在硬编码的配置信息