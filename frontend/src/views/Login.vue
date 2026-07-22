<template>
  <div class="login-container">
    <div class="login-bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">
          <img src="/logo.svg" class="logo-img" alt="AI呼叫中心" />
        </div>
        <h1>AI 呼叫中心</h1>
        <p>智能话务管理平台</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="passwd">
          <el-input
            v-model="loginForm.passwd"
            type="password"
            placeholder="密码"
            size="large"
            prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <span>默认账号：admin / 12345678</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  passwd: '12345678'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  passwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return
    loading.value = true
    const success = await userStore.userLogin(loginForm)
    if (success) {
      router.push('/')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + error.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #312e81 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.login-bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}

.shape-1 {
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, #818cf8, transparent 70%);
  top: -200px;
  right: -150px;
}

.shape-2 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #6366f1, transparent 70%);
  bottom: -150px;
  left: -100px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl), 0 0 0 1px rgba(255, 255, 255, 0.1);
  padding: 48px 40px;
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.5s var(--transition-smooth);
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.logo-img {
  width: 64px;
  height: 64px;
  display: block;
  margin: 0 auto;
}

.login-logo {
  margin-bottom: 16px;
}

/* kept for backward compatibility */
.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  font-size: 26px;
  color: #fff;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-dark));
  border-radius: 14px;
  box-shadow: 0 8px 20px rgba(99, 102, 241, 0.35);
}

.login-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
  letter-spacing: -0.01em;
}

.login-header p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
}

.login-form {
  margin-bottom: 0;
}

.login-form :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border-color);
  padding: 2px 14px;
  border-radius: var(--radius-md);
  transition: all 0.2s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--color-primary-light);
}

.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px var(--color-primary), 0 0 0 4px var(--color-primary-bg);
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  height: 46px;
  font-size: var(--font-size-lg);
  font-weight: 600;
  letter-spacing: 0.05em;
  border-radius: var(--radius-md);
  margin-top: 4px;
}

.login-footer {
  text-align: center;
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid var(--border-color-light);
}

.login-footer span {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

@media (max-width: 480px) {
  .login-card {
    padding: 36px 24px;
  }
}
</style>


