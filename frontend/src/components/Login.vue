<template>
  <div class="login-container">
    <h1>产品经理个人作品展示系统</h1>
    <form @submit.prevent="login" class="login-form">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" id="username" v-model="loginForm.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="loginForm.password" required>
      </div>
      <div class="form-group">
        <label for="captcha">验证码</label>
        <div class="captcha-container">
          <input type="text" id="captcha" v-model="loginForm.captcha" required placeholder="请输入验证码">
          <img :src="captchaImage" @click="getCaptcha" alt="验证码" class="captcha-image">
        </div>
      </div>
      <button type="submit" class="login-btn" :disabled="loginLoading">
        <span v-if="!loginLoading">登录</span>
        <span v-else class="loading-spinner">登录中...</span>
      </button>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </form>
  </div>
</template>

<script>
export default {
  name: 'Login',
  props: {
    loginForm: {
      type: Object,
      required: true
    },
    captchaImage: {
      type: String,
      required: true
    },
    errorMessage: {
      type: String,
      default: ''
    },
    loginLoading: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    login() {
      this.$emit('login')
    },
    getCaptcha() {
      this.$emit('get-captcha')
    }
  }
}
</script>

<style scoped>
/* 登录页面 */
.login-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container h1 {
  color: white;
  margin-bottom: 32px;
  font-size: 28px;
  font-weight: 600;
  text-align: center;
}

.login-form {
  background: white;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.captcha-container {
  display: flex;
  gap: 12px;
}

.captcha-container input {
  flex: 1;
}

.captcha-image {
  width: 100px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #40a9ff;
}

.login-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  position: relative;
  width: 16px;
  height: 16px;
  margin-right: 8px;
}

.loading-spinner::after {
  content: '';
  display: block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid currentColor;
  border-color: transparent currentColor transparent currentColor;
  animation: spin 1s linear infinite;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@keyframes spin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

.error-message {
  margin-top: 16px;
  color: #f5222d;
  font-size: 14px;
  text-align: center;
}
</style>