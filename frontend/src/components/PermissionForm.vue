<template>
  <div class="permission-form">
    <el-form :model="localPermission" :rules="rules" ref="permissionForm" label-width="100px">
      <el-form-item label="权限名称" prop="permissionName">
        <el-input v-model="localPermission.permissionName" placeholder="请输入权限名称" />
      </el-form-item>
      <el-form-item label="权限编码" prop="permissionCode">
        <el-input v-model="localPermission.permissionCode" placeholder="请输入权限编码" />
      </el-form-item>
      <el-form-item label="权限类型" prop="permissionType">
        <el-select v-model="localPermission.permissionType" placeholder="请选择权限类型">
          <el-option label="功能权限" value="function" />
          <el-option label="数据权限" value="data" />
          <el-option label="操作权限" value="operation" />
          <el-option label="资源权限" value="resource" />
        </el-select>
      </el-form-item>
      <el-form-item label="权限描述" prop="description">
        <el-input v-model="localPermission.description" placeholder="请输入权限描述" type="textarea" />
      </el-form-item>
      <el-form-item label="状态">
        <el-switch v-model="localPermission.isEnabled" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">保存</el-button>
        <el-button @click="resetForm">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

// 定义props
const props = defineProps({
  permission: {
    type: Object,
    default: () => ({})
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 本地权限数据
const localPermission = reactive({
  id: null,
  permissionName: '',
  permissionCode: '',
  permissionType: 'function',
  description: '',
  isEnabled: true
})

// 表单验证规则
const rules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '权限名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 2, max: 50, message: '权限编码长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  permissionType: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ]
}

// 表单引用
const permissionForm = ref(null)

// 监听权限数据变化
watch(() => props.permission, (newPermission) => {
  Object.assign(localPermission, newPermission)
}, { deep: true, immediate: true })

// 提交表单
const submitForm = async () => {
  await permissionForm.value.validate((valid, fields) => {
    if (valid) {
      emit('save', { ...localPermission })
    } else {
      console.log('验证失败:', fields)
    }
  })
}

// 重置表单
const resetForm = () => {
  permissionForm.value.resetFields()
  emit('cancel')
}
</script>

<style scoped>
.permission-form {
  padding: 20px 0;
}
</style>