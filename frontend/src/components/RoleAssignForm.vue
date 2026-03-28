<template>
  <div class="role-assign-form">
    <el-form :model="form" label-width="120px">
      <el-form-item label="选择角色">
        <el-checkbox-group v-model="selectedRoles" @change="handleRoleChange">
          <el-checkbox
            v-for="role in roles"
            :key="role.id"
            :value="role.id"
          >
            {{ role.roleName }} - {{ role.description || '无描述' }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="生效时间">
        <el-date-picker
          v-model="form.effectiveTime"
          type="datetime"
          placeholder="请选择生效时间"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="失效时间">
        <el-date-picker
          v-model="form.expireTime"
          type="datetime"
          placeholder="请选择失效时间"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" @click="submitForm">保存分配</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

// 接收props
const props = defineProps({
  userId: {
    type: Number,
    default: null
  },
  roles: {
    type: Array,
    default: () => []
  },
  selectedRoles: {
    type: Array,
    default: () => []
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 表单数据
const form = reactive({
  effectiveTime: new Date(), // 默认立即生效
  expireTime: null // 默认永不过期
})

// 选中的角色
const selectedRoles = ref([])

// 监听选中角色变化
watch(() => props.selectedRoles, (newRoles) => {
  selectedRoles.value = [...newRoles]
}, { deep: true, immediate: true })

// 处理角色选择变化
const handleRoleChange = (roles) => {
  selectedRoles.value = roles
}

// 提交表单
const submitForm = () => {
  if (selectedRoles.value.length === 0) {
    alert('请至少选择一个角色')
    return
  }
  
  emit('save', {
    roleIds: selectedRoles.value,
    effectiveTime: form.effectiveTime,
    expireTime: form.expireTime
  })
}
</script>

<style scoped>
.role-assign-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-checkbox-group {
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.el-checkbox {
  display: block;
  margin-bottom: 10px;
}
</style>