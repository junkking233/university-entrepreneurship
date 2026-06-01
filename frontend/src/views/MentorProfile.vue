<template>
  <div class="mentor-profile-page">
    <el-card>
      <template #header>
        <h2>导师个人资料</h2>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="large"
        class="profile-form"
      >
        <el-form-item label="头像">
          <el-avatar :size="80" :icon="UserFilled" />
          <el-button type="primary" link style="margin-left: 16px;">更换头像</el-button>
        </el-form-item>

        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>

        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="如：教授、副教授、高级工程师" />
        </el-form-item>

        <el-form-item label="专业领域" prop="expertise">
          <el-select
            v-model="form.expertise"
            multiple
            placeholder="请选择专业领域"
            style="width: 100%"
          >
            <el-option label="人工智能" value="人工智能" />
            <el-option label="企业管理" value="企业管理" />
            <el-option label="市场营销" value="市场营销" />
            <el-option label="财务管理" value="财务管理" />
            <el-option label="技术开发" value="技术开发" />
            <el-option label="法律合规" value="法律合规" />
          </el-select>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="6"
            placeholder="请介绍您的专业背景、经验和成就"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import { updateMentorProfile, getMentorProfile } from '@/api/mentor'
import { getUserInfo, updateUserInfo } from '@/api/auth'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  name: '',
  title: '',
  expertise: [],
  email: '',
  phone: '',
  bio: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请输入职称', trigger: 'blur' }],
  expertise: [{ required: true, message: '请选择专业领域', trigger: 'change' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

const expertiseOptions = ['人工智能', '企业管理', '市场营销', '财务管理', '技术开发', '法律合规']

function cleanFormText(value) {
  return value ? String(value).replace(/[\u0000-\u0008\u000b\u000c\u000e-\u001f\u007f-\u009f]/g, '') : ''
}

function normalizeExpertise(value) {
  const list = Array.isArray(value)
    ? value
    : String(value || '').split(/[,，]/).map((item) => item.trim()).filter(Boolean)
  const validList = list.filter((item) => expertiseOptions.includes(item))
  return validList.length > 0 ? validList : ['人工智能']
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await Promise.all([
      updateMentorProfile({
        expertise: form.expertise.join(','),
        introduction: cleanFormText(form.bio),
        availability: cleanFormText(form.title)
      }),
      updateUserInfo({
        name: cleanFormText(form.name),
        email: form.email,
        phone: form.phone
      })
    ])
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    localStorage.setItem('userInfo', JSON.stringify({
      ...userInfo,
      name: form.name,
      email: form.email,
      phone: form.phone
    }))
    ElMessage.success('保存成功')
  } catch {
    // error handled
  } finally {
    loading.value = false
  }
}

async function loadProfile() {
  try {
    const [mentorRes, userRes] = await Promise.all([getMentorProfile(), getUserInfo()])
    const mentor = mentorRes?.data || {}
    const user = userRes?.data || {}
    Object.assign(form, {
      name: cleanFormText(user.name || user.username || ''),
      title: cleanFormText(mentor.availability || ''),
      expertise: normalizeExpertise(mentor.expertise),
      email: user.email || '',
      phone: user.phone || '',
      bio: cleanFormText(mentor.introduction || '')
    })
  } catch {
    Object.assign(form, {
      name: '',
      title: '',
      expertise: [],
      email: '',
      phone: '',
      bio: ''
    })
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.mentor-profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.mentor-profile-page h2 {
  margin: 0;
  font-size: 20px;
}

.profile-form {
  margin-top: 20px;
}
</style>
