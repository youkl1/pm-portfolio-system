<template>
  <div class="package-form">
    <el-form :model="package" label-width="120px">
      <el-form-item label="套餐名称" required>
        <el-input v-model="package.packageName" placeholder="请输入套餐名称" />
      </el-form-item>
      <el-form-item label="用户上限" required>
        <el-input-number v-model="package.userLimit" :min="1" :step="1" placeholder="请输入用户上限" />
      </el-form-item>
      <el-form-item label="价格" required>
        <el-input-number v-model="package.price" :min="0" :step="0.01" placeholder="请输入价格" />
      </el-form-item>
      <el-form-item label="功能限制">
        <el-input
          v-model="package.featureLimit"
          type="textarea"
          placeholder="请输入功能限制（JSON格式）"
          :rows="4"
        />
      </el-form-item>
      <el-form-item>
        <div class="form-actions">
          <el-button @click="$emit('cancel')">取消</el-button>
          <el-button type="primary" @click="handleSave">保存</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 接收props
const props = defineProps({
  package: {
    type: Object,
    default: () => ({
      id: null,
      packageName: '',
      userLimit: 1,
      featureLimit: '{}',
      price: 0
    })
  }
})

// 定义事件
const emit = defineEmits(['save', 'cancel'])

// 处理保存
const handleSave = () => {
  emit('save', { ...props.package })
}
</script>

<style scoped>
.package-form {
  padding: 20px 0;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>