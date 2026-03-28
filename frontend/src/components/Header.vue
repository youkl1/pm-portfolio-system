<template>
  <header class="header">
    <h1>产品经理个人作品展示系统</h1>
    <div class="header-actions">
      <span>欢迎，{{ userInfo.username }} ({{ userInfo.role }})</span>
      <nav class="nav-menu">
        <button @click="switchTab('dashboard')" :class="{ active: activeTab === 'dashboard' }" class="nav-btn">仪表盘</button>
        <button @click="switchTab('projects')" :class="{ active: activeTab === 'projects' }" class="nav-btn">作品管理</button>
        <button @click="switchTab('resumes')" :class="{ active: activeTab === 'resumes' }" class="nav-btn">个人简历</button>
        <button v-if="userInfo.role === 'admin'" @click="switchTab('permission')" :class="{ active: activeTab === 'permission' }" class="nav-btn">权限管理</button>
        <button v-if="userInfo.role === 'admin'" @click="switchTab('tenant')" :class="{ active: activeTab === 'tenant' }" class="nav-btn">租户管理</button>
        <button v-if="userInfo.role === 'admin'" @click="switchTab('user')" :class="{ active: activeTab === 'user' }" class="nav-btn">用户管理</button>
        <button v-if="userInfo.role === 'admin' || userInfo.role === 'test'" @click="switchTab('template')" :class="{ active: activeTab === 'template' }" class="nav-btn">模板库</button>
      </nav>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>
  </header>
</template>

<script>
export default {
  name: 'Header',
  props: {
    userInfo: {
      type: Object,
      required: true
    },
    activeTab: {
      type: String,
      default: 'projects'
    }
  },
  methods: {
    switchTab(tab) {
      this.$emit('switch-tab', tab)
    },
    logout() {
      this.$emit('logout')
    }
  }
}
</script>

<style scoped>
/* 头部样式 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header h1 {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-actions span {
  font-size: 14px;
  color: #666;
}

.nav-menu {
  display: flex;
  gap: 12px;
}

.nav-btn {
  padding: 8px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.nav-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.nav-btn.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.logout-btn {
  padding: 8px 16px;
  border: 1px solid #ff4d4f;
  background: white;
  color: #ff4d4f;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff4d4f;
  color: white;
}
</style>