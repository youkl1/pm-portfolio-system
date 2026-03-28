<template>
  <div class="permission-list">
    <div class="list-header">
      <h3>权限列表</h3>
      <el-button type="primary" @click="handleCreatePermission">创建权限</el-button>
    </div>
    <el-table :data="permissions" style="width: 100%">
      <el-table-column prop="permissionName" label="权限名称" width="180" />
      <el-table-column prop="permissionCode" label="权限编码" width="180" />
      <el-table-column prop="description" label="权限描述" />
      <el-table-column prop="isEnabled" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isEnabled ? 'success' : 'danger'">
            {{ row.isEnabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import apiClient from '../utils/api'

// 定义事件
const emit = defineEmits(['edit-permission', 'delete-permission'])

// 权限列表
const permissions = ref([])

// 加载权限列表
const loadPermissions = async () => {
  try {
    const response = await apiClient.get('/api/permission/permissions')
    permissions.value = response.data
  } catch (error) {
    console.error('加载权限列表失败:', error)
  }
}

// 处理创建权限
const handleCreatePermission = () => {
  emit('edit-permission', { id: null, permissionName: '', permissionCode: '', description: '', isEnabled: true })
}

// 处理编辑权限
const handleEdit = (permission) => {
  emit('edit-permission', permission)
}

// 处理删除权限
const handleDelete = (id) => {
  emit('delete-permission', id)
}

// 组件挂载时加载权限列表
onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
.permission-list {
  margin-top: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.list-header h3 {
  margin: 0;
  color: #333;
}
</style>