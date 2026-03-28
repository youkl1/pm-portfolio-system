<template>
  <div class="user-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item>系统管理</el-breadcrumb-item>
            <el-breadcrumb-item>用户管理</el-breadcrumb-item>
          </el-breadcrumb>
          <h1 class="page-title">用户管理</h1>
          <p class="page-description">管理系统用户账号、角色权限及安全设置</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" size="default" @click="handleCreateUser" class="action-button">
            <el-icon><Plus /></el-icon>
            创建用户
          </el-button>
          <el-button type="success" size="default" @click="handleInviteUser" class="action-button">
            <el-icon><Message /></el-icon>
            邀请成员
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <el-card class="main-card" shadow="hover">
        <el-tabs v-model="activeTab" class="user-tabs" type="border-card">
          <el-tab-pane label="用户列表" name="users">
            <div class="tab-content">
              <!-- 搜索和筛选 -->
              <div class="search-filter">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索账号、姓名或邮箱"
                  prefix-icon="Search"
                  class="search-input"
                />
              </div>
              
              <!-- 用户表格 -->
              <div class="table-container">
                <el-table :data="users" class="user-table" stripe style="width: 100%">
                  <el-table-column type="index" label="序号" width="80" />
                  <el-table-column prop="username" label="账号" width="180" sortable />
                  <el-table-column prop="name" label="姓名" width="120" sortable />
                  <el-table-column prop="email" label="邮箱" width="200" />
                  <el-table-column prop="phone" label="手机号" width="150" />
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="getUserStatusTagType(scope.row?.status)" effect="light">
                        {{ getUserStatusName(scope.row?.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="tenantId.tenantName" label="所属租户" width="150" />
                  <el-table-column label="角色" width="180">
                    <template #default="scope">
                      <div v-if="scope.row?.roles && scope.row.roles.length > 0" class="role-tags">
                        <el-tag 
                          v-for="role in scope.row.roles" 
                          :key="role.id" 
                          type="info" 
                          effect="light" 
                          size="small" 
                          class="role-tag"
                        >
                          {{ role.roleName }}
                        </el-tag>
                      </div>
                      <span v-else class="text-gray">无角色</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="220" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" @click="handleEditUser(scope.row)" type="primary" link>
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-button>
                        <el-button size="small" @click="handleAssignRole(scope.row)" type="success" link>
                          <el-icon><Setting /></el-icon>
                          分配角色
                        </el-button>
                        <el-button size="small" type="danger" link @click="handleDeleteUser(scope.row?.id)">
                          <el-icon><Delete /></el-icon>
                          删除
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="成员邀请" name="invites">
            <div class="tab-content">
              <div class="table-container">
                <el-table :data="invites" class="user-table" stripe style="width: 100%">
                  <el-table-column prop="target" label="邀请目标" width="200" />
                  <el-table-column prop="type" label="邀请方式" width="120">
                    <template #default="scope">
                      {{ getInviteTypeName(scope.row?.type) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="getInviteStatusTagType(scope.row?.status)" effect="light">
                        {{ getInviteStatusName(scope.row?.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="expireTime" label="过期时间" width="180" />
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" @click="handleResendInvite(scope.row)" type="primary" link>
                          <el-icon><Refresh /></el-icon>
                          重发
                        </el-button>
                        <el-button size="small" type="danger" link @click="handleRevokeInvite(scope.row?.id)">
                          <el-icon><Close /></el-icon>
                          撤回
                        </el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="账号安全" name="security">
            <div class="tab-content">
              <div class="security-section">
                <div class="section-header">
                  <h3 class="section-title">登录日志</h3>
                  <div class="section-actions">
                    <el-date-picker
                      v-model="dateRange"
                      type="daterange"
                      range-separator="至"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      size="small"
                      class="date-picker"
                    />
                  </div>
                </div>
                
                <div class="table-container">
                  <el-table :data="loginLogs" class="user-table" stripe style="width: 100%">
                    <el-table-column prop="loginTime" label="登录时间" width="180" sortable />
                    <el-table-column prop="loginIp" label="登录IP" width="150" />
                    <el-table-column prop="loginDevice" label="登录设备" width="150" />
                    <el-table-column prop="loginResult" label="登录结果" width="100">
                      <template #default="scope">
                        <el-tag :type="scope.row?.loginResult === 'success' ? 'success' : 'danger'" effect="light">
                          {{ scope.row?.loginResult === 'success' ? '成功' : '失败' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="failReason" label="失败原因" />
                  </el-table>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 用户编辑对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="isEditUser ? '编辑用户' : '创建用户'"
      width="600px"
      class="user-dialog"
      center
    >
      <UserForm
        :user="currentUser"
        :tenants="tenants"
        @save="saveUser"
        @cancel="userDialogVisible = false"
      />
    </el-dialog>

    <!-- 邀请成员对话框 -->
    <el-dialog
      v-model="inviteDialogVisible"
      title="邀请成员"
      width="500px"
      class="user-dialog"
      center
    >
      <InviteForm
        :roles="roles"
        @save="saveInvite"
        @cancel="inviteDialogVisible = false"
      />
    </el-dialog>

    <!-- 角色分配对话框 -->
    <el-dialog
      v-model="roleAssignDialogVisible"
      :title="`为 ${currentUser.username} 分配角色`"
      width="500px"
      class="user-dialog"
      center
    >
      <RoleAssignForm
        :userId="currentUser.id"
        :roles="roles"
        :selectedRoles="selectedRoleIds"
        @save="saveRoleAssignment"
        @cancel="roleAssignDialogVisible = false"
      />
    </el-dialog>

    <!-- 删除确认对话框 -->
    <DeleteModal
      v-model="deleteDialogVisible"
      :title="deleteType === 'user' ? '删除用户' : '撤回邀请'"
      :content="deleteType === 'user' ? '确定要删除该用户吗？' : '确定要撤回该邀请吗？'"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import apiClient from '../utils/api'
import DeleteModal from './DeleteModal.vue'

// 导入子组件
import UserForm from './UserForm.vue'
import InviteForm from './InviteForm.vue'
import RoleAssignForm from './RoleAssignForm.vue'

// 状态管理
const activeTab = ref('users')
const userDialogVisible = ref(false)
const inviteDialogVisible = ref(false)
const roleAssignDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEditUser = ref(false)
const deleteType = ref('')
const deleteId = ref(null)

// 搜索和筛选
const searchKeyword = ref('')
const dateRange = ref([])

// 数据
const users = ref([])
const invites = ref([])
const loginLogs = ref([])
const tenants = ref([])
const roles = ref([])

// 当前编辑的用户
const currentUser = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  status: 'active',
  tenantId: null
})

// 选中的角色ID
const selectedRoleIds = ref([])

// 加载用户列表
const loadUsers = async () => {
  try {
    const response = await apiClient.get('/api/user/list')
    users.value = response.data.data
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

// 加载邀请列表
const loadInvites = async () => {
  try {
    const response = await apiClient.get('/api/user/invites')
    invites.value = response.data.data
  } catch (error) {
    console.error('加载邀请列表失败:', error)
  }
}

// 加载登录日志
const loadLoginLogs = async () => {
  try {
    const response = await apiClient.get('/api/user/login-logs')
    loginLogs.value = response.data.data
  } catch (error) {
    console.error('加载登录日志失败:', error)
  }
}

// 加载租户列表
const loadTenants = async () => {
  try {
    const response = await apiClient.get('/api/tenant/list')
    tenants.value = response.data
  } catch (error) {
    console.error('加载租户列表失败:', error)
  }
}

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await apiClient.get('/api/permission/roles')
    roles.value = response.data
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 加载用户角色
const loadUserRoles = async (userId) => {
  try {
    const response = await apiClient.get(`/api/user/${userId}/roles`)
    selectedRoleIds.value = response.data.data.map(r => r.id)
  } catch (error) {
    console.error('加载用户角色失败:', error)
  }
}

// 处理创建用户
const handleCreateUser = () => {
  Object.assign(currentUser, {
    id: null,
    username: '',
    password: '',
    name: '',
    email: '',
    phone: '',
    status: 'active',
    tenantId: null
  })
  isEditUser.value = false
  userDialogVisible.value = true
}

// 处理编辑用户
const handleEditUser = (user) => {
  Object.assign(currentUser, user)
  isEditUser.value = true
  userDialogVisible.value = true
}

// 处理删除用户
const handleDeleteUser = (id) => {
  deleteType.value = 'user'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 处理邀请成员
const handleInviteUser = () => {
  inviteDialogVisible.value = true
}

// 处理分配角色
const handleAssignRole = (user) => {
  Object.assign(currentUser, user)
  loadUserRoles(user.id)
  roleAssignDialogVisible.value = true
}

// 处理重发邀请
const handleResendInvite = async (invite) => {
  try {
    await apiClient.post(`/api/user/invite/resend/${invite.id}`)
    loadInvites()
  } catch (error) {
    console.error('重发邀请失败:', error)
  }
}

// 处理撤回邀请
const handleRevokeInvite = (id) => {
  deleteType.value = 'invite'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 保存用户
const saveUser = async (user) => {
  try {
    let response
    if (isEditUser.value) {
      response = await apiClient.put(`/api/user/update`, user)
    } else {
      response = await apiClient.post('/api/user/create', user)
    }
    userDialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

// 保存邀请
const saveInvite = async (invite) => {
  try {
    const response = await apiClient.post('/api/user/invite', invite)
    inviteDialogVisible.value = false
    loadInvites()
  } catch (error) {
    console.error('保存邀请失败:', error)
  }
}

// 保存角色分配
const saveRoleAssignment = async (roleData) => {
  try {
    await apiClient.post(`/api/user/${currentUser.id}/roles`, {
      roleIds: roleData.roleIds
    })
    roleAssignDialogVisible.value = false
    loadUsers()
  } catch (error) {
    console.error('保存角色分配失败:', error)
  }
}

// 确认删除
const confirmDelete = async () => {
  try {
    if (deleteType.value === 'user') {
      await apiClient.delete(`/api/user/delete/${deleteId.value}`)
      loadUsers()
    } else if (deleteType.value === 'invite') {
      await apiClient.delete(`/api/user/invite/revoke/${deleteId.value}`)
      loadInvites()
    }
    deleteDialogVisible.value = false
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 获取用户状态标签类型
const getUserStatusTagType = (status) => {
  switch (status) {
    case 'active': return 'success'
    case 'inactive': return 'danger'
    case 'locked': return 'warning'
    default: return 'default'
  }
}

// 获取用户状态名称
const getUserStatusName = (status) => {
  switch (status) {
    case 'active': return '启用'
    case 'inactive': return '禁用'
    case 'locked': return '锁定'
    default: return status
  }
}

// 获取邀请方式名称
const getInviteTypeName = (type) => {
  switch (type) {
    case 'link': return '链接'
    case 'email': return '邮箱'
    case 'phone': return '手机号'
    default: return type
  }
}

// 获取邀请状态标签类型
const getInviteStatusTagType = (status) => {
  switch (status) {
    case 'sent': return 'info'
    case 'accepted': return 'success'
    case 'expired': return 'danger'
    case 'revoked': return 'warning'
    default: return 'default'
  }
}

// 获取邀请状态名称
const getInviteStatusName = (status) => {
  switch (status) {
    case 'sent': return '已发送'
    case 'accepted': return '已接受'
    case 'expired': return '已过期'
    case 'revoked': return '已撤回'
    default: return status
  }
}

// 监听TAB切换
watch(activeTab, (newTab) => {
  switch (newTab) {
    case 'users':
      loadUsers()
      break
    case 'invites':
      loadInvites()
      break
    case 'security':
      loadLoginLogs()
      break
  }
})

// 组件挂载时加载数据
onMounted(() => {
  loadUsers()
  loadTenants()
  loadRoles()
})
</script>

<style scoped>
/* 全局样式变量 */
:root {
  /* 色彩系统 */
  --primary-color: #165DFF;
  --success-color: #00B42A;
  --warning-color: #FF7D00;
  --danger-color: #F53F3F;
  --info-color: #86909C;
  
  /* 中性色 */
  --text-primary: #1D2129;
  --text-secondary: #4E5969;
  --text-tertiary: #86909C;
  --text-quaternary: #C9CDD4;
  
  --border-light: #E5E6EB;
  --border-medium: #C9CDD4;
  
  --bg-white: #FFFFFF;
  --bg-light: #F2F3F5;
  --bg-medium: #E5E6EB;
  
  /* 间距系统 */
  --spacing-xs: 8px;
  --spacing-sm: 16px;
  --spacing-md: 24px;
  --spacing-lg: 32px;
  --spacing-xl: 48px;
  
  /* 圆角系统 */
  --border-radius-sm: 4px;
  --border-radius-md: 8px;
  --border-radius-lg: 12px;
  --border-radius-xl: 16px;
  
  /* 阴影系统 */
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.08);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.12);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.16);
  
  /* 字体系统 */
  --font-size-xs: 12px;
  --font-size-sm: 14px;
  --font-size-md: 16px;
  --font-size-lg: 18px;
  --font-size-xl: 20px;
  --font-size-xxl: 24px;
  
  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
}

.user-manager {
  padding: var(--spacing-lg);
  background-color: var(--bg-light);
  min-height: calc(100vh - 80px);
}

/* 页面头部样式 */
.page-header {
  margin-bottom: var(--spacing-lg);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background-color: var(--bg-white);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
}

.header-info {
  flex: 1;
}

.breadcrumb {
  margin-bottom: var(--spacing-sm);
  font-size: var(--font-size-sm);
}

.breadcrumb .el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
}

.page-title {
  margin: 0 0 var(--spacing-xs) 0;
  color: var(--text-primary);
  font-size: var(--font-size-xxl);
  font-weight: var(--font-weight-semibold);
  line-height: 1.2;
}

.page-description {
  margin: 0;
  color: var(--text-tertiary);
  font-size: var(--font-size-md);
  line-height: 1.5;
}

.header-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.action-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding: 10px 20px;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  border-radius: var(--border-radius-md);
  transition: all 0.2s ease;
}

.action-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 主内容区域 */
.main-content {
  width: 100%;
}

.main-card {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
}

/* 标签页样式 */
.user-tabs {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.user-tabs .el-tabs__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
}

.user-tabs .el-tabs__tab {
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  transition: all 0.2s ease;
  margin-right: var(--spacing-md);
}

.user-tabs .el-tabs__tab.is-active {
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
}

.user-tabs .el-tabs__content {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  align-items: center;
}

.search-input {
  width: 300px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.search-input:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
}

/* 区域头部 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-light);
}

.section-title {
  margin: 0;
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.section-actions {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.date-picker {
  width: 240px;
}

/* 表格样式 */
.table-container {
  margin-top: var(--spacing-md);
}

.user-table {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.user-table th {
  background-color: var(--bg-light);
  color: var(--text-secondary);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 2px solid var(--border-light);
  text-align: left;
}

.user-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  vertical-align: middle;
}

.user-table tr:hover {
  background-color: rgba(22, 93, 255, 0.02);
}

/* 标签样式 */
.el-tag {
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-xs);
  padding: 4px 12px;
  font-weight: var(--font-weight-medium);
}

.el-tag--success {
  background-color: rgba(0, 180, 42, 0.1);
  color: var(--success-color);
}

.el-tag--warning {
  background-color: rgba(255, 125, 0, 0.1);
  color: var(--warning-color);
}

.el-tag--danger {
  background-color: rgba(245, 63, 63, 0.1);
  color: var(--danger-color);
}

.el-tag--info {
  background-color: rgba(134, 144, 156, 0.1);
  color: var(--info-color);
}

/* 角色标签样式 */
.role-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.role-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.text-gray {
  color: var(--text-tertiary);
  font-size: var(--font-size-sm);
}

/* 表格操作按钮组 */
.table-actions {
  display: flex;
  gap: var(--spacing-xs);
  align-items: center;
}

.table-actions .el-button {
  padding: 4px 12px;
  font-size: var(--font-size-xs);
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 弹窗样式 */
.user-dialog {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  max-width: 600px;
}

.user-dialog .el-dialog__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-md);
}

.user-dialog .el-dialog__title {
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.user-dialog .el-dialog__body {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

.user-dialog .el-dialog__footer {
  background-color: var(--bg-white);
  border-top: 1px solid var(--border-light);
  padding: var(--spacing-md);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .search-input {
    width: 100%;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
  
  .section-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .date-picker {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .user-manager {
    padding: var(--spacing-md);
  }
  
  .header-content {
    padding: var(--spacing-md);
  }
  
  .page-title {
    font-size: var(--font-size-xl);
  }
  
  .action-button {
    padding: 8px 16px;
    font-size: var(--font-size-sm);
  }
  
  .user-tabs .el-tabs__content {
    padding: var(--spacing-sm);
  }
  
  .user-table th,
  .user-table td {
    padding: var(--spacing-sm);
    font-size: var(--font-size-xs);
  }
  
  .table-actions {
    flex-wrap: wrap;
  }
  
  .table-actions .el-button {
    padding: 2px 8px;
    font-size: var(--font-size-xs);
  }
  
  .user-dialog {
    width: 95% !important;
    margin: 10px auto !important;
  }
  
  .user-dialog .el-dialog__body {
    padding: var(--spacing-sm);
  }
}

/* 加载状态 */
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-xl);
  color: var(--text-tertiary);
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-xl);
  color: var(--text-tertiary);
  text-align: center;
}

.empty-state-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
  opacity: 0.5;
}

.empty-state-text {
  font-size: var(--font-size-md);
  margin-bottom: var(--spacing-xs);
}

.empty-state-subtext {
  font-size: var(--font-size-sm);
  color: var(--text-quaternary);
}
</style>