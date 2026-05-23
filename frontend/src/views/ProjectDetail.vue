<template>
  <div class="project-detail-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header" v-if="project.id">
          <el-button type="primary" link @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <h2>{{ project.title }}</h2>
        </div>
      </template>

      <el-descriptions v-if="project.id" :column="2" border>
        <el-descriptions-item label="项目分类">
          <el-tag size="small">{{ project.category }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目状态">
          <el-tag :type="statusType(project.status)" size="small">
            {{ statusLabel(project.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创始人">{{ project.founder }}</el-descriptions-item>
        <el-descriptions-item label="融资金额">
          <span style="color:#e6a23c;font-weight:bold;">{{ project.fundingTarget }}万</span>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ project.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="团队规模">{{ project.teamSize || '-' }}</el-descriptions-item>
      </el-descriptions>

      <template v-if="project.id">
      <el-divider />

      <div class="detail-section">
        <h3>项目简介</h3>
        <p>{{ project.description }}</p>
      </div>

      <div class="detail-section">
        <h3>商业计划</h3>
        <p>{{ project.businessPlan || '-' }}</p>
      </div>

      <div class="detail-section">
        <h3>团队介绍</h3>
        <p>{{ project.teamIntro || '-' }}</p>
      </div>

      <div class="detail-actions">
        <el-button type="primary" size="large" @click="handleInvest">
          <el-icon><Money /></el-icon> 我要投资
        </el-button>
        <el-button type="success" size="large" @click="handleConsult">
          <el-icon><ChatDotRound /></el-icon> 咨询导师
        </el-button>
      </div>
      </template>
      <el-empty v-else-if="!loading" description="暂无项目详情" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Money, ChatDotRound } from '@element-plus/icons-vue'
import { getProjectDetail } from '@/api/project'

const route = useRoute()
const loading = ref(false)

const project = ref({
  id: '',
  title: '',
  category: '',
  status: '',
  founder: '',
  fundingTarget: 0,
  createdAt: '',
  description: '',
  businessPlan: '',
  teamIntro: '',
  teamSize: ''
})

function statusType(status) {
  const map = { funding: 'primary', funded: 'success', incubating: 'warning' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { funding: '融资中', funded: '已融资', incubating: '孵化中' }
  return map[status] || status
}

function handleInvest() {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    return
  }
  ElMessage.info('投资功能开发中，敬请期待')
}

function handleConsult() {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    return
  }
  ElMessage.info('咨询功能开发中，敬请期待')
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getProjectDetail(route.params.id)
    project.value = res.data || project.value
  } catch {
    project.value = {
      id: '',
      title: '',
      category: '',
      status: '',
      founder: '',
      fundingTarget: 0,
      createdAt: '',
      description: '',
      businessPlan: '',
      teamIntro: '',
      teamSize: ''
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>

<style scoped>
.project-detail-page {
  max-width: 1000px;
  margin: 0 auto;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-header h2 {
  margin: 0;
  font-size: 22px;
  color: #303133;
}

.detail-section {
  margin: 20px 0;
}

.detail-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid #409eff;
}

.detail-section p {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.detail-actions {
  margin-top: 30px;
  display: flex;
  gap: 16px;
  justify-content: center;
}
</style>
