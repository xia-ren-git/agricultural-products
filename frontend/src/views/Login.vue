<template>
  <div class="login-container">
    <div class="flowing-bg">
      <div class="wave wave1"></div>
      <div class="wave wave2"></div>
      <div class="wave wave3"></div>
      <div class="floating-shapes">
        <div class="shape shape1"></div>
        <div class="shape shape2"></div>
        <div class="shape shape3"></div>
        <div class="shape shape4"></div>
        <div class="shape shape5"></div>
      </div>
    </div>
    <div class="login-card">
      <div class="card-header">
        <div class="brand-icon">
          <el-icon :size="28"><Shop /></el-icon>
        </div>
        <h1>农产品销售管理系统</h1>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Shop } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.loginAction(form)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
  position: relative;
  overflow: hidden;
}

.flowing-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
}

.wave {
  position: absolute;
  width: 200%;
  height: 200%;
  top: -50%;
  left: -50%;
  opacity: 0.3;
  border-radius: 45%;
  animation: wave-rotate linear infinite;
}

.wave1 {
  background: linear-gradient(135deg, rgba(45, 90, 135, 0.8) 0%, rgba(58, 124, 165, 0.4) 100%);
  animation-duration: 25s;
}

.wave2 {
  background: linear-gradient(135deg, rgba(30, 58, 95, 0.6) 0%, rgba(45, 90, 135, 0.3) 100%);
  animation-duration: 35s;
  animation-direction: reverse;
}

.wave3 {
  background: linear-gradient(135deg, rgba(58, 124, 165, 0.5) 0%, rgba(30, 58, 95, 0.2) 100%);
  animation-duration: 45s;
}

@keyframes wave-rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0.05) 100%);
  backdrop-filter: blur(5px);
  animation: float ease-in-out infinite;
}

.shape1 {
  width: 120px;
  height: 120px;
  top: 10%;
  left: 10%;
  animation-duration: 8s;
}

.shape2 {
  width: 80px;
  height: 80px;
  top: 60%;
  left: 5%;
  animation-duration: 10s;
  animation-delay: -2s;
}

.shape3 {
  width: 150px;
  height: 150px;
  top: 20%;
  right: 15%;
  animation-duration: 12s;
  animation-delay: -4s;
}

.shape4 {
  width: 60px;
  height: 60px;
  bottom: 15%;
  right: 20%;
  animation-duration: 9s;
  animation-delay: -1s;
}

.shape5 {
  width: 100px;
  height: 100px;
  bottom: 25%;
  left: 20%;
  animation-duration: 11s;
  animation-delay: -3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(5deg);
  }
  50% {
    transform: translateY(0) rotate(0deg);
  }
  75% {
    transform: translateY(20px) rotate(-5deg);
  }
}

.login-card {
  width: 340px;
  padding: 32px 28px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(20px);
  z-index: 1;
}

.card-header {
  text-align: center;
  margin-bottom: 28px;
}

.brand-icon {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #0f2027 0%, #2c5364 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  color: #fff;
  box-shadow: 0 8px 20px rgba(15, 32, 39, 0.3);
}

.card-header h1 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 2px;
}

.login-form {
  margin-bottom: 16px;
}

.login-btn {
  width: 100%;
  height: 40px;
  font-size: 14px;
  background: linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%);
  border: none;
  letter-spacing: 4px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(15, 32, 39, 0.3);
}

:deep(.el-input__wrapper) {
  padding: 0 12px;
  height: 38px;
  box-shadow: 0 0 0 1px #e0e0e0 inset;
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #2c5364 inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(44, 83, 100, 0.2) inset, 0 0 0 1px #2c5364 inset;
}

:deep(.el-input__inner) {
  font-size: 13px;
}

:deep(.el-form-item) {
  margin-bottom: 18px;
}

:deep(.el-divider__text) {
  color: #999;
  font-size: 11px;
  background: rgba(255, 255, 255, 0.95);
}
</style>
