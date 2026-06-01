<template>
  <div class="create-project-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>{{ isEdit ? '编辑项目' : '发布项目' }}</h2>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        size="large"
        class="project-form"
      >
        <el-form-item label="项目名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入项目名称" maxlength="50" show-word-limit />
        </el-form-item>

        <el-form-item label="项目分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择项目分类" style="width: 100%">
            <el-option label="人工智能" value="人工智能" />
            <el-option label="环保科技" value="环保科技" />
            <el-option label="教育科技" value="教育科技" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="电子商务" value="电子商务" />
            <el-option label="金融科技" value="金融科技" />
            <el-option label="文化创意" value="文化创意" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="融资金额" prop="fundingTarget">
          <el-input-number
            v-model="form.fundingTarget"
            :min="1"
            :max="10000"
            :step="10"
            controls-position="right"
            style="width: 200px"
          />
          <span class="unit-text">万元</span>
        </el-form-item>

        <el-form-item label="团队规模" prop="teamSize">
          <el-input-number
            v-model="form.teamSize"
            :min="1"
            :max="100"
            style="width: 200px"
            controls-position="right"
          />
          <span class="unit-text">人</span>
        </el-form-item>

        <el-form-item label="项目简介" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请简要描述项目内容（200字以内）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="商业计划" prop="businessPlan">
          <el-input
            v-model="form.businessPlan"
            type="textarea"
            :rows="6"
            placeholder="请详细描述商业计划，包括市场分析、盈利模式、发展规划等"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="团队介绍" prop="teamInfo">
          <el-input
            v-model="form.teamInfo"
            type="textarea"
            :rows="4"
            placeholder="请介绍团队成员及各自的优势和分工"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="loading">
            {{ isEdit ? '保存修改' : '发布项目' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
          <el-button @click="handleSaveDraft">保存草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createProject, updateProject, getProjectDetail } from '@/api/project'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = reactive({
  title: '',
  category: '',
  fundingTarget: 10,
  teamSize: 3,
  description: '',
  businessPlan: '',
  teamInfo: ''
})

const rules = {
  title: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择项目分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入项目简介', trigger: 'blur' }],
  businessPlan: [{ required: true, message: '请输入商业计划', trigger: 'blur' }]
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isEdit.value) {
      await updateProject(route.params.id, form)
      ElMessage.success('项目更新成功')
    } else {
      await createProject(form)
      ElMessage.success('项目发布成功')
    }
    router.push('/student/projects')
  } catch {
    // error handled
  } finally {
    loading.value = false
  }
}

async function handleSaveDraft() {
  loading.value = true
  try {
    const draftData = { ...form, status: 'draft' }
    if (isEdit.value) {
      await updateProject(route.params.id, draftData)
    } else {
      await createProject(draftData)
    }
    ElMessage.success('草稿保存成功')
    router.push('/student/projects')
  } catch {
    // error handled
  } finally {
    loading.value = false
  }
}

async function loadProject() {
  if (!isEdit.value) return
  try {
    const res = await getProjectDetail(route.params.id)
    Object.assign(form, res.data)
    if (res.data?.teamInfo) {
      form.teamInfo = res.data.teamInfo
    }
  } catch {
    ElMessage.error('加载项目信息失败')
  }
}

onMounted(() => {
  loadProject()
})
</script>

<style scoped>
.create-project-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.project-form {
  margin-top: 20px;
}

.unit-text {
  margin-left: 8px;
  color: #909399;
  font-size: 14px;
}
</style>
