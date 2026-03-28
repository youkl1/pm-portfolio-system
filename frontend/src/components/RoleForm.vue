<template>
  <div class="role-form">
    <el-form :model="localRole" :rules="rules" ref="roleForm" label-width="100px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="localRole.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色描述" prop="description">
        <el-input v-model="localRole.description" placeholder="请输入角色描述" type="textarea" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="localRole.isEnabled" />
      </el-form-item>
      <el-form-item label="权限设置">
        <div class="permission-selector">
          <el-checkbox-group v-model="selectedPermissionIds">
            <el-checkbox 
              v-for="permission in permissions" 
              :key="permission.id" 
              :label="permission.id"
              class="permission-item"
            >
              {{ permission.permissionName }} ({{ permission.permissionCode }})
            </el-checkbox>
          </el-checkbox-group>
          <div v-if="permissions.length === 0" class="empty-permissions">
            暂无权限数据
          </div>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="resetForm">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import apiClient from '../utils/api'

// 定义props
const props = defineProps({
  role: {
    type: Object,
    default: () => ({})
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 本地角色数据
const localRole = reactive({
  id: null,
  roleName: '',
  description: '',
  isEnabled: true
})

// 权限数据
const permissions = ref([])
const selectedPermissionIds = ref([])

// 表单验证规则
const rules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 50, message: '角色名称长度在 2 到 50 个字符', trigger: 'blur' }
  ]
}

// 表单引用
const roleForm = ref(null)

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
  if (!roleId) {
    selectedPermissionIds.value = []
    return
  }
  try {
    const response = await apiClient.get(`/api/permission/roles/${roleId}/permissions`)
    selectedPermissionIds.value = response.data.map(p => p.id)
  } catch (error) {
    console.error('加载角色权限失败:', error)
    selectedPermissionIds.value = []
  }
}

// 监听角色数据变化
watch(() => props.role, (newRole) => {
  Object.assign(localRole, newRole)
  loadRolePermissions(newRole.id)
}, { deep: true, immediate: true })

// 提交表单
const submitForm = async () => {
  await roleForm.value.validate((valid, fields) => {
    if (valid) {
      emit('save', { 
        ...localRole,
        permissionIds: selectedPermissionIds.value
      })
    } else {
      console.log('验证失败:', fields)
    }
  })
}

// 重置表单
const resetForm = () => {
  roleForm.value.resetFields()
  selectedPermissionIds.value = []
  emit('cancel')
}

// 组件挂载时加载权限数据
onMounted(() => {
  loadPermissions()
  loadRolePermissions(props.role.id)
})
</script>

<style scoped>
.role-form {
  padding: 20px 0;
}

.permission-selector {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  background-color: #f9f9f9;
}

.permission-item {
  display: block;
  margin-bottom: 8px;
  width: 100%;
}

.empty-permissions {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>