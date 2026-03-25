<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
            </div>
          </template>
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo?.avatar">
              <el-icon :size="40"><UserFilled /></el-icon>
            </el-avatar>
            <h3 class="username">{{ userInfo?.nickname || userInfo?.username }}</h3>
            <el-tag :type="userInfo?.role === 1 ? 'danger' : 'info'" size="small">
              {{ userInfo?.role === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </div>
          <div class="info-list">
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userInfo?.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号</span>
              <span class="value">{{ userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ userInfo?.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间</span>
              <span class="value">{{ formatTime(userInfo?.createTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="edit-card">
          <template #header>
            <div class="card-header">
              <span>修改信息</span>
            </div>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="80px" class="form">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="infoForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" class="form">
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="pwdLoading" @click="handleUpdatePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { updateUserInfo, updatePassword } from '@/api/user'
import { formatTime } from '@/utils/time'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const activeTab = ref('info')
const infoFormRef = ref(null)
const pwdFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)

const infoForm = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const infoRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  if (userInfo.value) {
    infoForm.nickname = userInfo.value.nickname || ''
    infoForm.phone = userInfo.value.phone || ''
    infoForm.email = userInfo.value.email || ''
  }
})

const handleUpdateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  infoLoading.value = true
  try {
    await updateUserInfo({
      id: userInfo.value.id,
      nickname: infoForm.nickname,
      phone: infoForm.phone,
      email: infoForm.email
    })
    userInfo.value.nickname = infoForm.nickname
    userInfo.value.phone = infoForm.phone
    userInfo.value.email = infoForm.email
    userStore.userInfo = userInfo.value
    localStorage.setItem('user', JSON.stringify(userInfo.value))
    ElMessage.success('修改成功')
  } catch (error) {
    console.error(error)
  } finally {
    infoLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  pwdLoading.value = true
  try {
    await updatePassword({
      userId: userInfo.value.id,
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
  } catch (error) {
    console.error(error)
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style scoped>
.profile-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

.username {
  margin: 16px 0 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.info-list {
  text-align: left;
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-item .label {
  color: #999;
}

.info-item .value {
  color: #333;
  font-weight: 500;
}

.edit-card {
  min-height: 400px;
}

.form {
  max-width: 400px;
  margin-top: 20px;
}
</style>
