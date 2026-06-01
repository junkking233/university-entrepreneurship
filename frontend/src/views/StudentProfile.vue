<template>
  <div class="student-profile-page">
    <el-card shadow="never" class="profile-card">
      <template #header>
        <div class="card-header">
          <div>
            <h2>个人中心</h2>
            <p>维护你的创业者基础资料，便于导师和平台联系你。</p>
          </div>
          <el-avatar :size="48" :icon="UserFilled" />
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="96px"
        size="large"
        class="profile-form"
      >
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名或昵称" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">保存资料</el-button>
          <el-button @click="$router.push('/student/dashboard')">返回工作台</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo } from '@/api/auth'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  name: '',
  email: '',
  phone: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名或昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

function cleanText(value) {
  return value ? String(value).replace(/[\u0000-\u0008\u000b\u000c\u000e-\u001f\u007f-\u009f]/g, '').trim() : ''
}

async function loadProfile() {
  try {
    const res = await getUserInfo()
    const user = res.data || {}
    Object.assign(form, {
      username: user.username || '',
      name: cleanText(user.name || user.username || ''),
      email: user.email || '',
      phone: user.phone || ''
    })
  } catch {
    // error handled by interceptor
  }
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const payload = {
      name: cleanText(form.name),
      email: form.email,
      phone: form.phone
    }
    const res = await updateUserInfo(payload)
    const updated = res.data || payload
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    localStorage.setItem('userInfo', JSON.stringify({
      ...userInfo,
      ...updated,
      name: payload.name,
      email: payload.email,
      phone: payload.phone
    }))
    ElMessage.success('保存成功')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.student-profile-page {
  max-width: 760px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
}

.card-header p {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.profile-form {
  margin-top: 8px;
}
</style>
