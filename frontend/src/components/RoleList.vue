<template>
  <div class="role-list">
    <div class="list-header">
      <h3>角色列表</h3>
      <el-button type="primary" @click="handleCreateRole">创建角色</el-button>
    </div>
    <el-table :data="roles" style="width: 100%">
      <el-table-column prop="roleName" label="角色名称" width="180" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column prop="isEnabled" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row?.isEnabled ? 'success' : 'danger'">
            {{ scope.row?.isEnabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row?.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import apiClient from '../utils/api'

// 定义事件
const emit = defineEmits(['edit-role', 'delete-role'])

// 角色列表
const roles = ref([])

// 加载角色列表
const loadRoles = async () => {
  try {
    const response = await apiClient.get('/api/permission/roles')
    roles.value = response.data
  } catch (error) {
    console.error('加载角色列表失败:', error)
  }
}

// 处理创建角色
const handleCreateRole = () => {
  emit('edit-role', { id: null, roleName: '', description: '', isEnabled: true })
}

// 处理编辑角色
const handleEdit = (role) => {
  emit('edit-role', role)
}

// 处理删除角色
const handleDelete = (id) => {
  emit('delete-role', id)
}

// 组件挂载时加载角色列表
onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-list {
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