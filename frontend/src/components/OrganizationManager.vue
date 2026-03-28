<template>
  <div class="organization-manager">
    <div class="page-header">
      <div class="header-content">
        <div class="header-info">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item>系统管理</el-breadcrumb-item>
            <el-breadcrumb-item>租户管理</el-breadcrumb-item>
            <el-breadcrumb-item>组织架构</el-breadcrumb-item>
          </el-breadcrumb>
          <h1 class="page-title">组织架构管理</h1>
          <p class="page-description">管理企业组织架构，支持多级组织树结构</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="handleCreateOrganization" class="action-button">
            <el-icon><Plus /></el-icon>
            创建组织
          </el-button>
        </div>
      </div>
    </div>

    <div class="main-content">
      <el-card class="main-card" shadow="hover">
        <div class="org-tree-container">
          <el-tree
            :data="organizationTree"
            :props="defaultProps"
            node-key="id"
            @node-click="handleNodeClick"
            default-expand-all
            class="org-tree"
          >
            <template #default="{ node, data }">
              <div class="org-node">
                <span class="org-node-name">{{ data.orgName }}</span>
                <div class="org-node-actions">
                  <el-button size="small" @click.stop="handleEditOrganization(data)" type="primary" link>
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button size="small" @click.stop="handleCreateSubOrganization(data.id)" type="success" link>
                    <el-icon><Plus /></el-icon>
                    添加子组织
                  </el-button>
                  <el-button size="small" @click.stop="handleDeleteOrganization(data.id)" type="danger" link>
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </template>
          </el-tree>
          
          <!-- 空状态 -->
          <div v-if="organizationTree.length === 0" class="empty-state">
            <el-icon class="empty-state-icon"><Folder /></el-icon>
            <div class="empty-state-text">暂无组织数据</div>
            <div class="empty-state-subtext">点击上方按钮创建第一个组织</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 组织编辑对话框 -->
    <el-dialog
      v-model="organizationDialogVisible"
      :title="isEditOrganization ? '编辑组织' : '创建组织'"
      width="500px"
      class="organization-dialog"
      center
    >
      <OrganizationForm
        :organization="currentOrganization"
        :organizations="organizations"
        @save="saveOrganization"
        @cancel="organizationDialogVisible = false"
      />
    </el-dialog>

    <!-- 删除确认对话框 -->
    <DeleteModal
      v-model="deleteDialogVisible"
      title="删除组织"
      content="确定要删除该组织吗？删除后该组织下的子组织也会被删除。"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import apiClient from '../utils/api'
import DeleteModal from './DeleteModal.vue'
import OrganizationForm from './OrganizationForm.vue'

// 状态管理
const organizationDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEditOrganization = ref(false)
const deleteId = ref(null)
const selectedOrganization = ref(null)

// 数据
const organizations = ref([])

// 当前编辑的组织
const currentOrganization = reactive({
  id: null,
  tenantId: 1, // 默认租户ID
  parentId: null,
  orgName: '',
  orgType: 'department'
})

// 组织树配置
const defaultProps = {
  children: 'children',
  label: 'orgName'
}

// 构建组织树
const organizationTree = computed(() => {
  const tree = []
  const map = new Map()

  // 先将所有组织放入Map
  organizations.value.forEach(org => {
    map.set(org.id, { ...org, children: [] })
  })

  // 构建树结构
  organizations.value.forEach(org => {
    if (org.parentId === null) {
      // 根组织
      tree.push(map.get(org.id))
    } else {
      // 子组织
      const parent = map.get(org.parentId)
      if (parent) {
        parent.children.push(map.get(org.id))
      }
    }
  })

  return tree
})

// 加载组织列表
const loadOrganizations = async () => {
  try {
    const response = await apiClient.get('/api/organization/list/tenant/1') // 默认租户ID为1
    organizations.value = response.data
  } catch (error) {
    console.error('加载组织列表失败:', error)
  }
}

// 处理节点点击
const handleNodeClick = (data) => {
  selectedOrganization.value = data
}

// 处理创建组织
const handleCreateOrganization = () => {
  Object.assign(currentOrganization, {
    id: null,
    tenantId: 1,
    parentId: null,
    orgName: '',
    orgType: 'department'
  })
  isEditOrganization.value = false
  organizationDialogVisible.value = true
}

// 处理创建子组织
const handleCreateSubOrganization = (parentId) => {
  Object.assign(currentOrganization, {
    id: null,
    tenantId: 1,
    parentId: parentId,
    orgName: '',
    orgType: 'department'
  })
  isEditOrganization.value = false
  organizationDialogVisible.value = true
}

// 处理编辑组织
const handleEditOrganization = (organization) => {
  Object.assign(currentOrganization, organization)
  isEditOrganization.value = true
  organizationDialogVisible.value = true
}

// 处理删除组织
const handleDeleteOrganization = (id) => {
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 保存组织
const saveOrganization = async (organization) => {
  try {
    let response
    if (isEditOrganization.value) {
      response = await apiClient.put(`/api/organization/update`, organization)
    } else {
      response = await apiClient.post('/api/organization/create', organization)
    }
    organizationDialogVisible.value = false
    loadOrganizations()
  } catch (error) {
    console.error('保存组织失败:', error)
  }
}

// 确认删除
const confirmDelete = async () => {
  try {
    await apiClient.delete(`/api/organization/delete/${deleteId.value}`)
    deleteDialogVisible.value = false
    loadOrganizations()
  } catch (error) {
    console.error('删除组织失败:', error)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadOrganizations()
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

.organization-manager {
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

/* 组织树容器 */
.org-tree-container {
  padding: var(--spacing-md);
  min-height: 500px;
  position: relative;
}

/* 组织树 */
.org-tree {
  width: 100%;
}

/* 组织节点 */
.org-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: var(--spacing-xs) 0;
}

.org-node-name {
  font-size: var(--font-size-md);
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
}

.org-node-actions {
  display: flex;
  gap: var(--spacing-xs);
  align-items: center;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.org-node:hover .org-node-actions {
  opacity: 1;
}

.org-node-actions .el-button {
  padding: 4px 12px;
  font-size: var(--font-size-xs);
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 弹窗样式 */
.organization-dialog {
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  max-width: 500px;
}

.organization-dialog .el-dialog__header {
  background-color: var(--bg-white);
  border-bottom: 1px solid var(--border-light);
  padding: var(--spacing-md);
}

.organization-dialog .el-dialog__title {
  color: var(--text-primary);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.organization-dialog .el-dialog__body {
  padding: var(--spacing-md);
  background-color: var(--bg-white);
}

.organization-dialog .el-dialog__footer {
  background-color: var(--bg-white);
  border-top: 1px solid var(--border-light);
  padding: var(--spacing-md);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
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
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
}

.empty-state-icon {
  font-size: 48px;
  margin-bottom: var(--spacing-sm);
  opacity: 0.5;
}

.empty-state-text {
  font-size: var(--font-size-md);
  margin-bottom: var(--spacing-xs);
  color: var(--text-secondary);
}

.empty-state-subtext {
  font-size: var(--font-size-sm);
  color: var(--text-quaternary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .organization-manager {
    padding: var(--spacing-md);
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
    padding: var(--spacing-md);
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .action-button {
    width: 100%;
    justify-content: center;
  }
  
  .org-tree-container {
    padding: var(--spacing-sm);
    min-height: 400px;
  }
  
  .org-node {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-xs);
  }
  
  .org-node-actions {
    opacity: 1;
    width: 100%;
    justify-content: flex-start;
  }
  
  .organization-dialog {
    width: 95% !important;
    margin: 10px auto !important;
  }
  
  .organization-dialog .el-dialog__body {
    padding: var(--spacing-sm);
  }
}
</style>