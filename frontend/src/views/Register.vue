<template>
  <div class="register-page">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <el-icon :size="40" color="#409eff"><School /></el-icon>
          <h2>用户注册</h2>
          <p>加入大学生创业平台，开启创业之旅</p>
        </div>
      </template>

      <!-- 角色选择 -->
      <div class="role-select">
        <span class="role-label">选择角色：</span>
        <el-radio-group v-model="form.role" size="large">
          <el-radio-button value="student">
            <el-icon><User /></el-icon> 创业者
          </el-radio-button>
          <el-radio-button value="mentor">
            <el-icon><Avatar /></el-icon> 导师
          </el-radio-button>
          <el-radio-button value="investor">
            <el-icon><Wallet /></el-icon> 投资者
          </el-radio-button>
        </el-radio-group>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        size="large"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称"
            :prefix-icon="Edit"
          />
        </el-form-item>

        <el-form-item prop="email">
          <el-input
            v-model="form.email"
            placeholder="请输入邮箱"
            :prefix-icon="Message"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>

        <!-- 导师特有字段 -->
        <template v-if="form.role === 'mentor'">
          <el-form-item prop="expertise">
            <el-input
              v-model="form.expertise"
              placeholder="请输入专业领域（如：人工智能、企业管理）"
              :prefix-icon="CollectionTag"
            />
          </el-form-item>
          <el-form-item prop="bio">
            <el-input
              v-model="form.bio"
              type="textarea"
              :rows="3"
              placeholder="请输入个人简介"
            />
          </el-form-item>
        </template>

        <!-- 投资者特有字段 -->
        <template v-if="form.role === 'investor'">
          <el-form-item prop="company">
            <el-input
              v-model="form.company"
              placeholder="请输入投资机构/公司名称"
              :prefix-icon="OfficeBuilding"
            />
          </el-form-item>
        </template>

        <el-form-item>
          <el-button
            type="primary"
            native-type="submit"
            :loading="loading"
            class="register-btn"
          >
            注 册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-button type="primary" link @click="$router.push('/login')">立即登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  User, Lock, Message, Edit, Wallet,
  CollectionTag, OfficeBuilding, Avatar, School
} from '@element-plus/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  role: 'student',
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: '',
  expertise: '',
  bio: '',
  company: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const roleRedirectMap = {
  student: '/student/dashboard',
  mentor: '/mentor/dashboard',
  investor: '/investor/dashboard'
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const registerData = {
      role: form.role,
      username: form.username,
      name: form.nickname,
      nickname: form.nickname,
      email: form.email,
      password: form.password
    }

    if (form.role === 'mentor') {
      registerData.expertise = form.expertise
      registerData.bio = form.bio
    } else if (form.role === 'investor') {
      registerData.company = form.company
    }

    const res = await register(registerData)
    const { token } = res.data
    const userInfo = res.data.userInfo || res.data.user
    localStorage.setItem('token', token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    ElMessage.success('注册成功')

    router.push(roleRedirectMap[userInfo.role] || '/')
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 160px);
  background: linear-gradient(135deg, #e8f4ff 0%, #d0e8ff 100%);
  padding: 40px 0;
}

.register-card {
  width: 480px;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(64, 158, 255, 0.15);
}

.register-header {
  text-align: center;
  padding: 10px 0;
}

.register-header h2 {
  margin: 12px 0 8px;
  color: #303133;
}

.register-header p {
  color: #909399;
  font-size: 14px;
}

.role-select {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 24px;
}

.role-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>
