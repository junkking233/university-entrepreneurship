<template>
  <div class="investor-profile-page">
    <el-card>
      <template #header>
        <h2>投资者资料</h2>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" size="large">
        <el-form-item label="头像">
          <el-avatar :size="80" :icon="UserFilled" />
          <el-button type="primary" link style="margin-left: 16px;">更换头像</el-button>
        </el-form-item>

        <el-form-item label="姓名/企业名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名或企业名称" />
        </el-form-item>

        <el-form-item label="投资机构" prop="company">
          <el-input v-model="form.company" placeholder="请输入投资机构名称" />
        </el-form-item>

        <el-form-item label="投资领域" prop="fields">
          <el-select
            v-model="form.fields"
            multiple
            placeholder="请选择投资领域"
            style="width: 100%"
          >
            <el-option label="人工智能" value="人工智能" />
            <el-option label="环保科技" value="环保科技" />
            <el-option label="教育科技" value="教育科技" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="电子商务" value="电子商务" />
            <el-option label="金融科技" value="金融科技" />
          </el-select>
        </el-form-item>

        <el-form-item label="投资阶段" prop="stages">
          <el-checkbox-group v-model="form.stages">
            <el-checkbox label="种子轮" value="种子轮" />
            <el-checkbox label="天使轮" value="天使轮" />
            <el-checkbox label="Pre-A" value="Pre-A" />
            <el-checkbox label="A轮" value="A轮" />
            <el-checkbox label="B轮及以上" value="B轮及以上" />
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="投资额度范围" prop="investRange">
          <el-row :gutter="10">
            <el-col :span="11">
              <el-input-number
                v-model="form.investMin"
                :min="0"
                :step="10"
                controls-position="right"
                placeholder="最低"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="2" style="text-align: center; line-height: 40px;">-</el-col>
            <el-col :span="11">
              <el-input-number
                v-model="form.investMax"
                :min="0"
                :step="10"
                controls-position="right"
                placeholder="最高"
                style="width: 100%"
              />
            </el-col>
          </el-row>
          <span class="unit-text">万元</span>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="个人/机构简介" prop="bio">
          <el-input
            v-model="form.bio"
            type="textarea"
            :rows="6"
            placeholder="请介绍您的投资背景、成功案例等"
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
import { updateUserInfo } from '@/api/auth'

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  name: '',
  company: '',
  fields: [],
  stages: [],
  investMin: 10,
  investMax: 500,
  email: '',
  phone: '',
  bio: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名或企业名称', trigger: 'blur' }],
  company: [{ required: true, message: '请输入投资机构', trigger: 'blur' }],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await updateUserInfo(form)
    ElMessage.success('保存成功')
  } catch {
    // error handled
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  Object.assign(form, {
    name: userInfo.nickname || '某投资机构',
    company: '晨星创投',
    fields: ['人工智能', '教育科技'],
    stages: ['天使轮', 'Pre-A'],
    investMin: 10,
    investMax: 500,
    email: userInfo.email || 'investor@example.com',
    phone: '',
    bio: '专注于早期科技创新项目投资，已成功投资超过50个创业项目。'
  })
})
</script>

<style scoped>
.investor-profile-page {
  max-width: 800px;
  margin: 0 auto;
}

.investor-profile-page h2 {
  margin: 0;
  font-size: 20px;
}

.unit-text {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
