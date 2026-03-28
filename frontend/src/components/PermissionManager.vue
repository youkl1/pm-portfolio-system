<template>
  <div class="permission-manager">
    <h2>权限管理</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="角色管理" name="roles">
        <RoleList @edit-role="handleEditRole" @delete-role="handleDeleteRole" />
      </el-tab-pane>
      <el-tab-pane label="权限管理" name="permissions">
        <PermissionList @edit-permission="handleEditPermission" @delete-permission="handleDeletePermission" />
      </el-tab-pane>
      <el-tab-pane label="角色权限配置" name="role-permissions">
        <RolePermission />
      </el-tab-pane>
      <el-tab-pane label="角色模板库" name="role-templates">
        <RoleTemplate />
      </el-tab-pane>
    </el-tabs>

    <!-- 角色编辑对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="isEditRole ? '编辑角色' : '创建角色'"
      width="500px"
    >
      <RoleForm
        :role="currentRole"
        @save="saveRole"
        @cancel="roleDialogVisible = false"
      />
    </el-dialog>

    <!-- 权限编辑对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="isEditPermission ? '编辑权限' : '创建权限'"
      width="500px"
    >
      <PermissionForm
        :permission="currentPermission"
        @save="savePermission"
        @cancel="permissionDialogVisible = false"
      />
    </el-dialog>

    <!-- 删除确认对话框 -->
    <DeleteModal
      v-model="deleteDialogVisible"
      :title="deleteType === 'role' ? '删除角色' : '删除权限'"
      :content="deleteType === 'role' ? '确定要删除该角色吗？' : '确定要删除该权限吗？'"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import RoleList from './RoleList.vue'
import RoleForm from './RoleForm.vue'
import PermissionList from './PermissionList.vue'
import PermissionForm from './PermissionForm.vue'
import RolePermission from './RolePermission.vue'
import RoleTemplate from './RoleTemplate.vue'
import DeleteModal from './DeleteModal.vue'
import apiClient from '../utils/api'

// 状态管理
const activeTab = ref('roles')
const roleDialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const deleteDialogVisible = ref(false)
const isEditRole = ref(false)
const isEditPermission = ref(false)
const deleteType = ref('')
const deleteId = ref(null)

const currentRole = reactive({
  id: null,
  roleName: '',
  description: '',
  isEnabled: true
})

const currentPermission = reactive({
  id: null,
  permissionName: '',
  permissionCode: '',
  permissionType: 'function',
  description: '',
  isEnabled: true
})

// 处理编辑角色
const handleEditRole = (role) => {
  Object.assign(currentRole, role)
  isEditRole.value = true
  roleDialogVisible.value = true
}

// 处理删除角色
const handleDeleteRole = (id) => {
  deleteType.value = 'role'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 处理编辑权限
const handleEditPermission = (permission) => {
  Object.assign(currentPermission, permission)
  isEditPermission.value = true
  permissionDialogVisible.value = true
}

// 处理删除权限
const handleDeletePermission = (id) => {
  deleteType.value = 'permission'
  deleteId.value = id
  deleteDialogVisible.value = true
}

// 保存角色
const saveRole = async (role) => {
  try {
    let response
    if (isEditRole.value) {
      response = await apiClient.put(`/api/permission/roles/${role.id}`, role)
    } else {
      response = await apiClient.post('/api/permission/roles', role)
    }
    roleDialogVisible.value = false
    // 刷新角色列表
    window.location.reload()
  } catch (error) {
    console.error('保存角色失败:', error)
  }
}

// 保存权限
const savePermission = async (permission) => {
  try {
    let response
    if (isEditPermission.value) {
      response = await apiClient.put(`/api/permission/permissions/${permission.id}`, permission)
    } else {
      response = await apiClient.post('/api/permission/permissions', permission)
    }
    permissionDialogVisible.value = false
    // 刷新权限列表
    window.location.reload()
  } catch (error) {
    console.error('保存权限失败:', error)
  }
}

// 确认删除
const confirmDelete = async () => {
  try {
    if (deleteType.value === 'role') {
      await apiClient.delete(`/api/permission/roles/${deleteId.value}`)
    } else if (deleteType.value === 'permission') {
      await apiClient.delete(`/api/permission/permissions/${deleteId.value}`)
    }
    deleteDialogVisible.value = false
    // 刷新列表
    window.location.reload()
  } catch (error) {
    console.error('删除失败:', error)
  }
}
</script>

<style scoped>
.permission-manager {
  padding: 20px;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}
</style>