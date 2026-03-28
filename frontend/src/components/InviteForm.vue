<template>
  <div class="invite-form">
    <el-form :model="invite" :rules="rules" ref="inviteForm" label-width="120px">
      <el-form-item label="邀请方式" prop="type">
        <el-select v-model="invite.type" placeholder="请选择邀请方式" @change="handleInviteTypeChange">
          <el-option label="链接" value="link" />
          <el-option label="邮箱" value="email" />
          <el-option label="手机号" value="phone" />
        </el-select>
      </el-form-item>
      <el-form-item label="邀请目标" prop="target">
        <el-input 
          v-model="invite.target" 
          :placeholder="getTargetPlaceholder()" 
        />
      </el-form-item>
      <el-form-item label="分配角色" prop="roleIds">
        <el-select 
          v-model="invite.roleIds" 
          multiple 
          placeholder="请选择角色"
          style="width: 100%"
        >
          <el-option 
            v-for="role in roles" 
            :key="role.id" 
            :label="role.roleName" 
            :value="role.id" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="邀请有效期" prop="expireTime">
        <el-date-picker
          v-model="invite.expireTime"
          type="datetime"
          placeholder="请选择过期时间"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" @click="submitForm">发送邀请</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 接收props
const props = defineProps({
  roles: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 邀请数据
const invite = reactive({
  type: 'email',
  target: '',
  roleIds: [],
  expireTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // 默认7天有效期
})

// 表单验证规则
const rules = {
  type: [
    { required: true, message: '请选择邀请方式', trigger: 'change' }
  ],
  target: [
    { required: true, message: '请输入邀请目标', trigger: 'blur' }
  ],
  roleIds: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  expireTime: [
    { required: true, message: '请选择过期时间', trigger: 'change' }
  ]
}

// 表单引用
const inviteForm = ref(null)

// 处理邀请方式变更
const handleInviteTypeChange = (type) => {
  invite.target = ''
}

// 获取邀请目标占位符
const getTargetPlaceholder = () => {
  switch (invite.type) {
    case 'email': return '请输入邮箱地址'
    case 'phone': return '请输入手机号'
    case 'link': return '链接将自动生成'
    default: return '请输入邀请目标'
  }
}

// 提交表单
const submitForm = async () => {
  await inviteForm.value.validate((valid, fields) => {
    if (valid) {
      // 验证手机号或邮箱格式
      if (invite.type === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(invite.target)) {
        alert('请输入正确的邮箱格式')
        return
      }
      if (invite.type === 'phone' && !/^1[3-9]\d{9}$/.test(invite.target)) {
        alert('请输入正确的手机号格式')
        return
      }
      emit('save', { ...invite })
    } else {
      console.log('验证失败:', fields)
    }
  })
}
</script>

<style scoped>
.invite-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>