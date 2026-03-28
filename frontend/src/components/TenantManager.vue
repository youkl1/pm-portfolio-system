<template>
  <div class="tenant-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item>系统管理</el-breadcrumb-item>
            <el-breadcrumb-item>租户管理</el-breadcrumb-item>
          </el-breadcrumb>
          <h1 class="page-title">租户管理</h1>
          <p class="page-description">管理系统租户、套餐及组织架构</p>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <el-card class="main-card" shadow="hover">
        <el-tabs v-model="activeTab" class="tenant-tabs" type="border-card">
          <el-tab-pane label="租户列表" name="tenants">
            <div class="tab-content">
              <!-- 搜索和筛选 -->
              <div class="search-filter">
                <el-input
                  v-model="searchKeyword"
                  placeholder="搜索租户名称"
                  prefix-icon="Search"
                  class="search-input"
                />
                <el-select v-model="tenantTypeFilter" placeholder="租户类型" class="filter-select">
                  <el-option label="全部" value="" />
                  <el-option label="个人" value="personal" />
                  <el-option label="团队" value="team" />
                  <el-option label="企业" value="enterprise" />
                </el-select>
                <el-button type="primary" @click="handleCreateTenant" class="action-button">
                  <el-icon><Plus /></el-icon>
                  创建租户
                </el-button>
              </div>
              
              <!-- 租户表格 -->
              <div class="table-container">
                <el-table :data="filteredTenants" class="tenant-table" stripe style="width: 100%">
                  <el-table-column type="index" label="序号" width="80" />
                  <el-table-column prop="tenantName" label="租户名称" width="180" sortable />
                  <el-table-column prop="tenantType" label="租户类型" width="120">
                    <template #default="scope">
                      <el-tag :type="getTenantTypeTagType(scope.row?.tenantType)" effect="light">
                        {{ getTenantTypeName(scope.row?.tenantType) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="scope.row?.status === 'active' ? 'success' : 'danger'" effect="light">
                        {{ scope.row?.status === 'active' ? '启用' : '禁用' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column prop="packageId.packageName" label="套餐" width="150" />
                  <el-table-column prop="expireTime" label="到期时间" width="180" sortable />
                  <el-table-column label="操作" width="200" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" @click="handleEditTenant(scope.row)" type="primary" link>
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-button>
                        <el-button size="small" @click="handleToggleStatus(scope.row)" :type="scope.row?.status === 'active' ? 'warning' : 'success'" link>
                          <el-icon><Switch /></el-icon>
                          {{ scope.row?.status === 'active' ? '禁用' : '启用' }}
                        </el-button>
                        <el-button size="small" type="danger" link @click="handleDeleteTenant(scope.row?.id)">
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
          
          <el-tab-pane label="套餐管理" name="packages">
            <div class="tab-content">
              <!-- 搜索和筛选 -->
              <div class="search-filter">
                <el-input
                  v-model="packageSearchKeyword"
                  placeholder="搜索套餐名称"
                  prefix-icon="Search"
                  class="search-input"
                />
                <el-button type="primary" @click="handleCreatePackage" class="action-button">
                  <el-icon><Plus /></el-icon>
                  创建套餐
                </el-button>
              </div>
              
              <!-- 套餐表格 -->
              <div class="table-container">
                <el-table :data="filteredPackages" class="tenant-table" stripe style="width: 100%">
                  <el-table-column type="index" label="序号" width="80" />
                  <el-table-column prop="packageName" label="套餐名称" width="180" sortable />
                  <el-table-column prop="userLimit" label="用户上限" width="100" sortable />
                  <el-table-column prop="price" label="价格" width="100" sortable />
                  <el-table-column prop="featureLimit" label="功能限制" />
                  <el-table-column label="操作" width="180" fixed="right">
                    <template #default="scope">
                      <div class="table-actions">
                        <el-button size="small" @click="handleEditPackage(scope.row)" type="primary" link>
                          <el-icon><Edit /></el-icon>
                          编辑
                        </el-button>
                        <el-button size="small" type="danger" link @click="handleDeletePackage(scope.row?.id)">
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
          
          <el-tab-pane label="组织架构" name="organization">
            <OrganizationManager />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <!-- 租户编辑对话框 -->
    <el-dialog
      v-model="tenantDialogVisible"
      :title="isEditTenant ? '编辑租户' : '创建租户'"
      width="600px"
      class="tenant-dialog"
      center
    >
      <TenantForm
        :tenant="currentTenant"
        :packages="packages"
        @save="saveTenant"
        @cancel="tenantDialogVisible = false"
      />
    </el-dialog>

    <!-- 套餐编辑对话框 -->
    <el-dialog
      v-model="packageDialogVisible"
      :title="isEditPackage ? '编辑套餐' : '创建套餐'"
      width="500px"
      class="tenant-dialog"
      center
    >
      <PackageForm
        :package="currentPackage"
        @save="savePackage"
        @cancel="packageDialogVisible = false"
      />
    </el-dialog>

    <!-- 删除确认对话框 -->
    <DeleteModal
      v-model="deleteDialogVisible"
      :title="deleteType === 'tenant' ? '删除租户' : '删除套餐'"
      :content="deleteType === 'tenant' ? '确定要删除该租户吗？' : '确定要删除该套餐吗？'"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Edit, Delete, Switch, Search } from '@element-plus/icons-vue'
import apiClient from '../utils/api'
import DeleteModal from './DeleteModal.vue'

// 导入子组件
import OrganizationManager from './OrganizationManager.vue'
import TenantForm from './TenantForm.vue'
import PackageForm from './PackageForm.vue'

// 状态管理
const activeTab = ref('tenants')
const tenantDialogVisible = ref(false)
const packageDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEditTenant = ref(false)
const isEditPackage = ref(false)
const deleteType = ref('')
const deleteId = ref(null)

// 搜索和筛选
const searchKeyword = ref('')
const tenantTypeFilter = ref('')
const packageSearchKeyword = ref('')

// 数据
const tenants = ref([])
const packages = ref([])

// 当前编辑的租户
const currentTenant = reactive({
  id: null,
  tenantName: '',
  tenantType: 'personal',
  status: 'active',
  packageId: null,
  expireTime: '',
  companyName: '',
  industryType: '',
  companySize: '',
  companyAddress: '',
  adminUsername: '',
  adminPassword: '',
  adminEmail: '',
  adminPhone: ''
})

// 当前编辑的套餐
const currentPackage = reactive({
  id: null,
  packageName: '',
  userLimit: 1,
  featureLimit: '{}',
  price: 0
})

// 过滤后的租户列表
const filteredTenants = computed(() => {
  return tenants.value.filter(tenant => {
    const matchesKeyword = searchKeyword.value === '' || 
      tenant.tenantName.toLowerCase().includes(searchKeyword.value.toLowerCase())
    const matchesType = tenantTypeFilter.value === '' || 
      tenant.tenantType === tenantTypeFilter.value
    return matchesKeyword && matchesType
  })
})

// 过滤后的套餐列表
const filteredPackages = computed(() => {
  return packages.value.filter(pkg => {
    return packageSearchKeyword.value === '' || 
      pkg.packageName.toLowerCase().includes(packageSearchKeyword.value.toLowerCase())
  })
})

// 加载租户列表
const loadTenants = async () => {
  try {
    const response = await apiClient.get('/api/tenant/list')
    tenants.value = response.data
  } catch (error) {
    console.error('加载租户列表失败:', error)
  }
}

// 加载套餐列表
const loadPackages = async () => {
  try {
    const response = await apiClient.get('/api/tenant/package/list')
    packages.value = response.data
  } catch (error) {
    console.error('加载套餐列表失败:', error)
  }
}

// 处理创建租户
const handleCreateTenant = () => {
  Object.assign(currentTenant, {
    id: null,
    tenantName: '',
    tenantType: 'personal',
    status: 'active',
    packageId: null,
    expireTime: '',
    companyName: '',
    industryType: '',
    companySize: '',
    companyAddress: '',
    adminUsername: '',
    adminPassword: '',
    adminEmail: '',
    adminPhone: ''
  })
  isEditTenant.value = false
  tenantDialogVisible.value = true
}

// 处理编辑租户
const handleEditTenant = (tenant) => {
  Object.assign(currentTenant, tenant)
  isEditTenant.value = true
  tenantDialogVisible.value = true
}

// 处理删除租户
const handleDeleteTenant = (id) => {
  deleteType.value = 'tenant'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 处理切换租户状态
const handleToggleStatus = async (tenant) => {
  try {
    const updatedTenant = {
      ...tenant,
      status: tenant.status === 'active' ? 'inactive' : 'active'
    }
    await apiClient.put('/api/tenant/update', updatedTenant)
    loadTenants()
  } catch (error) {
    console.error('切换租户状态失败:', error)
  }
}

// 处理创建套餐
const handleCreatePackage = () => {
  Object.assign(currentPackage, {
    id: null,
    packageName: '',
    userLimit: 1,
    featureLimit: '{}',
    price: 0
  })
  isEditPackage.value = false
  packageDialogVisible.value = true
}

// 处理编辑套餐
const handleEditPackage = (pkg) => {
  Object.assign(currentPackage, pkg)
  isEditPackage.value = true
  packageDialogVisible.value = true
}

// 处理删除套餐
const handleDeletePackage = (id) => {
  deleteType.value = 'package'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 保存租户
const saveTenant = async (tenant) => {
  try {
    let response
    if (isEditTenant.value) {
      response = await apiClient.put(`/api/tenant/update`, tenant)
    } else {
      response = await apiClient.post('/api/tenant/create', tenant)
    }
    tenantDialogVisible.value = false
    loadTenants()
  } catch (error) {
    console.error('保存租户失败:', error)
  }
}

// 保存套餐
const savePackage = async (pkg) => {
  try {
    let response
    if (isEditPackage.value) {
      response = await apiClient.put(`/api/tenant/package/update`, pkg)
    } else {
      response = await apiClient.post('/api/tenant/package/create', pkg)
    }
    packageDialogVisible.value = false
    loadPackages()
  } catch (error) {
    console.error('保存套餐失败:', error)
  }
}

// 确认删除
const confirmDelete = async () => {
  try {
    if (deleteType.value === 'tenant') {
      await apiClient.delete(`/api/tenant/delete/${deleteId.value}`)
    } else if (deleteType.value === 'package') {
      await apiClient.delete(`/api/tenant/package/delete/${deleteId.value}`)
    }
    deleteDialogVisible.value = false
    if (deleteType.value === 'tenant') {
      loadTenants()
    } else {
      loadPackages()
    }
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 获取租户类型标签类型
const getTenantTypeTagType = (type) => {
  switch (type) {
    case 'personal': return 'info'
    case 'team': return 'warning'
    case 'enterprise': return 'primary'
    default: return 'info'
  }
}

// 获取租户类型名称
const getTenantTypeName = (type) => {
  switch (type) {
    case 'personal': return '个人'
    case 'team': return '团队'
    case 'enterprise': return '企业'
    default: return type
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadTenants()
  loadPackages()
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

.tenant-manager {
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
.tenant-tabs {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
}

.tenant-tabs .el-tabs__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
}

.tenant-tabs .el-tabs__tab {
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  transition: all 0.2s ease;
  margin-right: var(--spacing-md);
}

.tenant-tabs .el-tabs__tab.is-active {
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
}

.tenant-tabs .el-tabs__content {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
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

.filter-select {
  width: 150px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-light);
  transition: all 0.2s ease;
}

.filter-select:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(22, 93, 255, 0.1);
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

/* 表格样式 */
.table-container {
  margin-top: var(--spacing-md);
}

.tenant-table {
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
}

.tenant-table th {
  background-color: var(--bg-light);
  color: var(--text-secondary);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-bottom: 2px solid var(--border-light);
  text-align: left;
}

.tenant-table td {
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-light);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  vertical-align: middle;
}

.tenant-table tr:hover {
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

.el-tag--primary {
  background-color: rgba(22, 93, 255, 0.1);
  color: var(--primary-color);
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
.tenant-dialog {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  max-width: 600px;
}

.tenant-dialog .el-dialog__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-md);
}

.tenant-dialog .el-dialog__title {
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.tenant-dialog .el-dialog__body {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

.tenant-dialog .el-dialog__footer {
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
  
  .search-filter {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-sm);
  }
  
  .search-input,
  .filter-select {
    width: 100%;
  }
  
  .action-button {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .tenant-manager {
    padding: var(--spacing-md);
  }
  
  .header-content {
    padding: var(--spacing-md);
  }
  
  .page-title {
    font-size: var(--font-size-xl);
  }
  
  .tenant-tabs .el-tabs__content {
    padding: var(--spacing-sm);
  }
  
  .tenant-table th,
  .tenant-table td {
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
  
  .tenant-dialog {
    width: 95% !important;
    margin: 10px auto !important;
  }
  
  .tenant-dialog .el-dialog__body {
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