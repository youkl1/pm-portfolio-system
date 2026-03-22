<template>
  <div class="app">
    <!-- 登录页面 -->
    <div v-if="!userInfo" class="login-container">
      <h1>产品经理个人作品展示系统</h1>
      <form @submit.prevent="login" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="loginForm.username" required>
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="loginForm.password" required>
        </div>
        <div class="form-group">
          <label for="captcha">验证码</label>
          <div class="captcha-container">
            <input type="text" id="captcha" v-model="loginForm.captcha" required placeholder="请输入验证码">
            <img :src="captchaImage" @click="getCaptcha" alt="验证码" class="captcha-image">
          </div>
        </div>
        <button type="submit" class="login-btn">登录</button>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
      </form>
    </div>
    
    <!-- 作品展示页面 -->
    <div v-else class="project-container">
      <header class="header">
        <h1>产品经理个人作品展示系统</h1>
        <div class="header-actions">
          <span>欢迎，{{ userInfo.username }} ({{ userInfo.role }})</span>
          <button @click="logout" class="logout-btn">退出登录</button>
        </div>
      </header>
      
      <main class="main-content">
        <div class="toolbar">
          <h2>作品列表</h2>
          <button v-if="userInfo.role === 'admin'" @click="showAddModal = true" class="add-btn">新增作品</button>
        </div>
        
        <div class="project-grid">
          <div v-for="project in projects" :key="project.id" class="project-card">
            <img :src="project.coverImage" alt="封面" class="project-cover">
            <div class="project-info">
              <h3>{{ project.title }}</h3>
              <p>{{ project.description }}</p>
              <div class="link-container">
                <a :href="project.detailLink" target="_blank" class="detail-link">查看详情</a>
              </div>
              <div v-if="project.githubLink" class="github-container">
                <span class="github-label">GitHub:</span>
                <div class="github-url-container">
                  <a :href="project.githubLink" target="_blank" class="github-url">{{ project.githubLink }}</a>
                  <button @click="copyGithubLink(project.githubLink)" class="copy-btn">
                    <span class="copy-icon">📋</span>
                  </button>
                </div>
              </div>
            </div>
            <div v-if="userInfo.role === 'admin'" class="project-actions">
              <button @click="editProject(project)" class="edit-btn">编辑</button>
              <button @click="showDeleteConfirm(project.id)" class="delete-btn">删除</button>
            </div>
          </div>
        </div>
      </main>
      
      <!-- 新增/编辑弹窗 -->
      <div v-if="showAddModal || showEditModal" class="modal-overlay">
        <div class="modal">
          <div class="modal-header">
            <h3>{{ showEditModal ? '编辑作品' : '新增作品' }}</h3>
            <button @click="closeModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveProject" class="project-form">
              <div class="form-group">
                <label for="title">标题</label>
                <input type="text" id="title" v-model="projectForm.title" required>
              </div>
              <div class="form-group">
                <label for="description">描述</label>
                <textarea id="description" v-model="projectForm.description"></textarea>
              </div>
              <div class="form-group">
                <label>封面图片</label>
                <div class="upload-area" @click="triggerFileInput">
                  <input type="file" ref="fileInput" accept="image/jpeg,image/png" @change="handleFileChange" style="display: none;">
                  <img v-if="projectForm.coverImage" :src="projectForm.coverImage" alt="预览" class="preview-image">
                  <div v-else class="upload-placeholder">点击上传图片</div>
                </div>
              </div>
              <div class="form-group">
                <label for="detailLink">详情链接</label>
                <input type="url" id="detailLink" v-model="projectForm.detailLink">
              </div>
              <div class="form-group">
                <label for="githubLink">GitHub地址</label>
                <input type="url" id="githubLink" v-model="projectForm.githubLink">
              </div>
              <div class="form-group">
                <label for="sort">排序</label>
                <input type="number" id="sort" v-model.number="projectForm.sort" min="0">
              </div>
              <div class="form-actions">
                <button type="button" @click="closeModal" class="cancel-btn">取消</button>
                <button type="submit" class="save-btn">保存</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      
      <!-- 删除确认弹窗 -->
      <div v-if="showDeleteModal" class="modal-overlay">
        <div class="modal delete-modal">
          <div class="modal-header">
            <h3>确认删除</h3>
            <button @click="closeDeleteModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <p class="delete-message">确定要删除这个作品吗？此操作不可撤销。</p>
            <div class="form-actions">
              <button type="button" @click="closeDeleteModal" class="cancel-btn">取消</button>
              <button type="button" @click="confirmDelete" class="delete-confirm-btn">确认删除</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 成功提示弹窗 -->
      <div v-if="showSuccessModal" class="modal-overlay">
        <div class="modal success-modal">
          <div class="modal-header">
            <h3>操作成功</h3>
            <button @click="closeSuccessModal" class="close-btn">&times;</button>
          </div>
          <div class="modal-body">
            <div class="success-content">
              <div class="success-icon">✓</div>
              <p class="success-message">{{ successMessage }}</p>
            </div>
            <div class="form-actions">
              <button type="button" @click="closeSuccessModal" class="save-btn">确定</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import config from './config'

// 创建axios实例
const apiClient = axios.create({
  baseURL: config.apiBaseUrl,
  withCredentials: true
})

export default {
  name: 'App',
  data() {
    return {
      userInfo: null,
      loginForm: {
        username: 'test',
        password: 'test123',
        captcha: ''
      },
      captchaImage: '',
      errorMessage: '',
      projects: [],
      showAddModal: false,
      showEditModal: false,
      showDeleteModal: false,
      showSuccessModal: false,
      successMessage: '',
      currentProjectId: null,
      projectForm: {
        title: '',
        description: '',
        coverImage: '',
        detailLink: '',
        githubLink: '',
        sort: 0
      }
    }
  },
  mounted() {
    // 检查本地存储中的用户信息
    const storedUser = localStorage.getItem('userInfo')
    if (storedUser) {
      this.userInfo = JSON.parse(storedUser)
      this.getProjects()
    } else {
      // 未登录时获取验证码
      this.getCaptcha()
    }
  },
  methods: {
    // 获取验证码
    async getCaptcha() {
      try {
        const response = await apiClient.get('/api/auth/captcha')
        if (response.data.code === 200) {
          this.captchaImage = response.data.data.image
        } else {
          this.errorMessage = '获取验证码失败'
        }
      } catch (error) {
        this.errorMessage = '获取验证码失败，请重试'
      }
    },
    
    // 登录
    async login() {
      try {
        const response = await apiClient.post('/api/auth/login', this.loginForm)
        if (response.data.code === 200) {
          this.userInfo = response.data.data
          localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
          this.errorMessage = ''
          this.getProjects()
        } else {
          this.errorMessage = response.data.message
          // 登录失败时重新获取验证码
          this.getCaptcha()
        }
      } catch (error) {
        this.errorMessage = '登录失败，请重试'
        // 登录失败时重新获取验证码
        this.getCaptcha()
      }
    },
    
    // 退出登录
    logout() {
      this.userInfo = null
      localStorage.removeItem('userInfo')
      this.getCaptcha()
    },
    
    // 获取作品列表
    async getProjects() {
      try {
        const response = await apiClient.get('/api/projects')
        console.log('获取到的作品列表:', response.data.data)
        if (response.data.code === 200) {
          this.projects = response.data.data
        }
      } catch (error) {
        console.error('获取作品列表失败:', error)
      }
    },
    
    // 触发文件选择
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    
    // 处理文件选择
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        const formData = new FormData()
        formData.append('file', file)
        try {
          const response = await apiClient.post('/api/upload/cover', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
          if (response.data.code === 200) {
            this.projectForm.coverImage = response.data.data.url
          }
        } catch (error) {
          console.error('上传图片失败:', error)
        }
      }
    },
    
    // 打开编辑弹窗
    editProject(project) {
      this.currentProjectId = project.id
      this.projectForm = {
        title: project.title,
        description: project.description,
        coverImage: project.coverImage,
        detailLink: project.detailLink,
        githubLink: project.githubLink || '',
        sort: project.sort
      }
      this.showEditModal = true
    },
    
    // 保存作品
    async saveProject() {
      try {
        console.log('发送的projectForm:', this.projectForm)
        let response
        if (this.showEditModal) {
          response = await apiClient.put(`/api/projects/${this.currentProjectId}`, this.projectForm)
        } else {
          response = await apiClient.post('/api/projects', this.projectForm)
        }
        console.log('接收到的响应:', response.data)
        if (response.data.code === 200) {
          this.closeModal()
          this.getProjects()
        }
      } catch (error) {
        console.error('保存作品失败:', error)
      }
    },
    
    // 显示删除确认弹窗
    showDeleteConfirm(id) {
      this.currentProjectId = id
      this.showDeleteModal = true
    },
    
    // 关闭删除确认弹窗
    closeDeleteModal() {
      this.showDeleteModal = false
      this.currentProjectId = null
    },
    
    // 确认删除
    async confirmDelete() {
      try {
        const response = await apiClient.delete(`/api/projects/${this.currentProjectId}`)
        if (response.data.code === 200) {
          this.getProjects()
          this.closeDeleteModal()
          this.successMessage = '删除成功！'
          this.showSuccessModal = true
        }
      } catch (error) {
        console.error('删除作品失败:', error)
        this.successMessage = '删除失败，请重试'
        this.showSuccessModal = true
      }
    },
    
    // 关闭弹窗
    closeModal() {
      this.showAddModal = false
      this.showEditModal = false
      this.currentProjectId = null
      this.projectForm = {
        title: '',
        description: '',
        coverImage: '',
        detailLink: '',
        githubLink: '',
        sort: 0
      }
    },
    
    // 关闭成功提示弹窗
    closeSuccessModal() {
      this.showSuccessModal = false
      this.successMessage = ''
    },
    
    // 复制GitHub链接
    copyGithubLink(url) {
      navigator.clipboard.writeText(url).then(() => {
        this.successMessage = 'GitHub链接已复制到剪贴板！'
        this.showSuccessModal = true
      }).catch(err => {
        console.error('复制失败:', err)
        this.successMessage = '复制失败，请手动复制'
        this.showSuccessModal = true
      })
    }
  }
}
</script>

<style scoped>
.app {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 登录页面 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
}

.login-container h1 {
  color: #007bff;
  margin-bottom: 30px;
  font-size: 24px;
}

.login-form {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #212529;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 16px;
}

.captcha-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-container input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border: 1px solid #ced4da;
  border-radius: 4px;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #0069d9;
}

.error-message {
  margin-top: 15px;
  color: #dc3545;
  font-size: 14px;
  text-align: center;
}

/* 作品展示页面 */
.project-container {
  min-height: 100vh;
}

.header {
  background-color: #007bff;
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  margin: 0;
  font-size: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logout-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.main-content {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar h2 {
  margin: 0;
  color: #212529;
  font-size: 18px;
}

.add-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background-color: #218838;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.project-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.project-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.project-info {
  padding: 15px;
}

.project-info h3 {
  margin: 0 0 10px 0;
  color: #212529;
  font-size: 16px;
}

.project-info p {
  margin: 0 0 15px 0;
  color: #6c757d;
  font-size: 14px;
  line-height: 1.4;
}

.link-container {
  margin: 12px 0;
}

.detail-link {
  color: #165DFF;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 4px;
}

.detail-link:hover {
  color: #0A46D4;
  text-decoration: none;
  background-color: #F0F5FF;
}

.github-container {
  margin-top: 12px;
  padding: 12px;
  background-color: #FAFAFA;
  border-radius: 8px;
  border: 1px solid #F0F2F5;
}

.github-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #86909C;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.github-url-container {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #FFFFFF;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #E5E6EB;
}

.github-url {
  flex: 1;
  color: #24292E;
  text-decoration: none;
  font-size: 13px;
  font-family: 'Courier New', monospace;
  word-break: break-all;
  white-space: normal;
  line-height: 1.4;
}

.github-url:hover {
  color: #0366d6;
  text-decoration: underline;
}

.copy-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.copy-btn:hover {
  background-color: #F0F2F5;
  transform: translateY(-1px);
}

.copy-icon {
  font-size: 14px;
}

.project-actions {
  padding: 0 15px 15px;
  display: flex;
  gap: 10px;
}

.edit-btn {
  flex: 1;
  background-color: #ffc107;
  color: #212529;
  border: none;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-btn:hover {
  background-color: #e0a800;
}

.delete-btn {
  flex: 1;
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-btn:hover {
  background-color: #c82333;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-in-out;
}

.modal {
  background-color: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.3s ease-in-out;
  outline: none;
}

.delete-modal {
  max-width: 400px;
}

.delete-message {
  text-align: center;
  padding: 32px 0;
  font-size: 16px;
  color: #212529;
  margin: 0;
  line-height: 1.5;
}

.success-modal {
  max-width: 400px;
}

.success-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 0;
}

.success-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background-color: #00B42A;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 24px;
}

.success-message {
  text-align: center;
  font-size: 16px;
  color: #212529;
  margin: 0;
  line-height: 1.5;
}

.delete-confirm-btn {
  padding: 12px 24px;
  background-color: #F53F3F;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.delete-confirm-btn:hover {
  background-color: #E53935;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 63, 63, 0.2);
}

.delete-confirm-btn:active {
  transform: translateY(0);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-24px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #F0F2F5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #212529;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #86909C;
  transition: all 0.2s ease;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.close-btn:hover {
  color: #212529;
  background-color: #F0F2F5;
}

.modal-body {
  padding: 24px;
}

.project-form {
  width: 100%;
}

.upload-area {
  border: 2px dashed #DCDFE6;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #FAFAFA;
}

.upload-area:hover {
  border-color: #165DFF;
  background-color: #F0F5FF;
}

.preview-image {
  max-width: 100%;
  max-height: 200px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.upload-placeholder {
  color: #86909C;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #F0F2F5;
}

.cancel-btn {
  padding: 12px 24px;
  background-color: #FFFFFF;
  color: #4E5969;
  border: 1px solid #DCDFE6;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.cancel-btn:hover {
  background-color: #F5F7FA;
  border-color: #C0C4CC;
}

.save-btn {
  padding: 12px 24px;
  background-color: #165DFF;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  font-weight: 500;
}

.save-btn:hover {
  background-color: #0A46D4;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(22, 93, 255, 0.2);
}

.save-btn:active {
  transform: translateY(0);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .project-grid {
    grid-template-columns: 1fr;
  }
  
  .modal {
    margin: 20px;
  }
}
</style>