<template>
  <div class="role-permission">
    <h3>角色权限配置</h3>
    <div class="role-selector">
      <el-form-item label="选择角色">
        <el-select v-model="selectedRoleId" placeholder="请选择角色" @change="handleRoleChange">
          <el-option
            v-for="role in roles"
            :key="role.id"
            :label="role.roleName"
            :value="role.id"
          />
        </el-select>
      </el-form-item>
    </div>
    
    <div v-if="selectedRoleId" class="permission-assignment">
      <h4>{{ selectedRoleName }} - 权限分配</h4>
      <el-checkbox-group v-model="selectedPermissionIds" @change="handlePermissionChange">
        <el-checkbox
          v-for="permission in permissions"
          :key="permission.id"
          :value="permission.id"
        >
          {{ permission.permissionName }} ({{ permission.permissionCode }})
        </el-checkbox>
      </el-checkbox-group>
      <div class="action-buttons">
        <el-button type="primary" @click="savePermissions">保存权限分配</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import apiClient from '../utils/api'
import { ElMessage } from 'element-plus'

// 状态管理
const roles = ref([])
const permissions = ref([])
const selectedRoleId = ref(null)
const selectedPermissionIds = ref([])

// 计算属性：当前选择的角色名称
const selectedRoleName = computed(() => {
  const role = roles.value.find(r => r.id === selectedRoleId.value)
  return role ? role.roleName : ''
})

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await apiClient.get('/api/permission/roles')
    roles.value = response.data
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 加载权限列表
const loadPermissions = async () => {
  try {
    const response = await apiClient.get('/api/permission/permissions')
    permissions.value = response.data
  } catch (error) {
    console.error('加载权限列表失败:', error)
  }
}

// 加载角色权限
const loadRolePermissions = async (roleId) => {
  try {
    const response = await apiClient.get(`/api/permission/roles/${roleId}/permissions`)
    selectedPermissionIds.value = response.data.map(p => p.id)
  } catch (error) {
    console.error('加载角色权限失败:', error)
  }
}

// 处理角色选择变化
const handleRoleChange = (roleId) => {
  if (roleId) {
    loadRolePermissions(roleId)
  } else {
    selectedPermissionIds.value = []
  }
}

// 处理权限选择变化
const handlePermissionChange = () => {
  // 可以在这里添加实时验证逻辑
}

// 保存权限分配
const savePermissions = async () => {
  if (!selectedRoleId.value) {
    return
  }
  
  try {
    await apiClient.post(`/api/permission/roles/${selectedRoleId.value}/permissions`, selectedPermissionIds.value)
    // 显示成功消息
    ElMessage.success('权限分配保存成功')
  } catch (error) {
    console.error('保存权限分配失败:', error)
    ElMessage.error('保存权限分配失败')
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadRoles()
  loadPermissions()
})
</script>

<style scoped>
.role-permission {
  margin-top: 20px;
}

.role-selector {
  margin-bottom: 20px;
  max-width: 300px;
}

.permission-assignment {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 20px;
  background-color: #f9f9f9;
}

.permission-assignment h4 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.el-checkbox-group {
  margin-bottom: 20px;
}

.action-buttons {
  margin-top: 20px;
  text-align: right;
}
</style>