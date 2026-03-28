<template>
  <div class="user-form">
    <el-form :model="localUser" :rules="rules" ref="userForm" label-width="120px">
      <el-form-item label="账号" prop="username">
        <el-input v-model="localUser.username" placeholder="请输入账号" />
      </el-form-item>
      <el-form-item label="密码" :required="!localUser.id">
        <el-input v-model="localUser.password" type="password" placeholder="请输入密码" />
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="localUser.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="localUser.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="localUser.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="localUser.status" placeholder="请选择状态">
          <el-option label="启用" value="active" />
          <el-option label="禁用" value="inactive" />
          <el-option label="锁定" value="locked" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属租户" prop="tenantId">
        <el-select v-model="localUser.tenantId" placeholder="请选择租户">
          <el-option 
            v-for="tenant in tenants" 
            :key="tenant.id" 
            :label="tenant.tenantName" 
            :value="tenant.id" 
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" @click="submitForm">保存</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

// 接收props
const props = defineProps({
  user: {
    type: Object,
    default: () => ({
      id: null,
      username: '',
      password: '',
      name: '',
      email: '',
      phone: '',
      status: 'active',
      tenantId: null
    })
  },
  tenants: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 本地用户数据
const localUser = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  status: 'active',
  tenantId: null
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  tenantId: [
    { required: true, message: '请选择租户', trigger: 'change' }
  ]
}

// 表单引用
const userForm = ref(null)

// 监听用户数据变化
watch(() => props.user, (newUser) => {
  Object.assign(localUser, newUser)
}, { deep: true, immediate: true })

// 提交表单
const submitForm = async () => {
  await userForm.value.validate((valid, fields) => {
    if (valid) {
      emit('save', { ...localUser })
    } else {
      console.log('验证失败:', fields)
    }
  })
}
</script>

<style scoped>
.user-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>