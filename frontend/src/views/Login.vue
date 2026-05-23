<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <el-icon :size="40" color="#2563eb"><School /></el-icon>
          <h2>用户登录</h2>
          <p>欢迎来到大学生创业平台</p>
        </div>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名/邮箱"
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
            native-type="submit"
            :loading="loading"
            class="login-btn"
          >
            登 录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="primary" link @click="$router.push('/register')">立即注册</el-button>
        </div>
      </el-form>

      <!-- 快速登录（演示用） -->
      <el-divider>快速体验</el-divider>
      <div class="quick-login">
        <el-button
          v-for="demo in demos"
          :key="demo.role"
          :type="demo.type"
          size="small"
          @click="quickLogin(demo)"
        >
          {{ demo.label }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, School } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const demos = [
  { role: 'student', label: '学生演示', username: 'student', password: '123456', type: 'primary' },
  { role: 'mentor', label: '导师演示', username: 'mentor', password: '123456', type: 'success' },
  { role: 'investor', label: '投资者演示', username: 'investor', password: '123456', type: 'warning' },
  { role: 'admin', label: '管理员演示', username: 'admin', password: 'admin123', type: 'danger' }
]

const roleRedirectMap = {
  student: '/student/dashboard',
  mentor: '/mentor/dashboard',
  investor: '/investor/dashboard',
  admin: '/admin/dashboard'
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login({
      username: form.username,
      password: form.password
    })
    const { token, user } = res.data
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(user))
    ElMessage.success('登录成功')

    const redirect = route.query.redirect || roleRedirectMap[user.role] || '/'
    router.push(redirect)
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

function quickLogin(demo) {
  form.username = demo.username
  form.password = demo.password
  handleLogin()
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 160px);
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.login-card {
  width: 420px;
  border-radius: 12px;
  box-shadow: var(--shadow-card-hover);
}

.login-header {
  text-align: center;
  padding: 10px 0;
}

.login-header h2 {
  margin: 12px 0 8px;
  color: #303133;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.quick-login {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 8px;
}
</style>
