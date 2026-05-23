<template>
  <div class="project-detail-page">
    <el-card v-loading="loading">
      <template #header>
        <div class="detail-header">
          <el-button type="primary" link @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
          <h2>{{ project.title }}</h2>
        </div>
      </template>

      <el-descriptions :column="2" border>
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
          <span style="color:#f59e0b;font-weight:bold;">{{ project.fundingTarget }}万</span>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ project.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="团队规模">{{ project.teamSize || '3-5人' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider />

      <div class="detail-section">
        <h3>项目简介</h3>
        <p>{{ project.description }}</p>
      </div>

      <div class="detail-section">
        <h3>商业计划</h3>
        <p>{{ project.businessPlan || '该项目旨在通过创新技术解决行业痛点，具有广阔的市场前景和发展潜力。' }}</p>
      </div>

      <div class="detail-section">
        <h3>团队介绍</h3>
        <p>{{ project.teamIntro || '团队由来自多个专业领域的优秀大学生组成，具备扎实的技术功底和丰富的实践经验。' }}</p>
      </div>

      <div class="detail-actions">
        <el-button type="primary" size="large" @click="handleInvest">
          <el-icon><Money /></el-icon> 我要投资
        </el-button>
        <el-button type="success" size="large" @click="handleConsult">
          <el-icon><ChatDotRound /></el-icon> 咨询导师
        </el-button>
      </div>
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
  id: 1,
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
    // 使用模拟数据
    project.value = {
      id: route.params.id,
      title: '智能校园助手',
      category: '人工智能',
      status: 'funding',
      founder: '张三',
      fundingTarget: 50,
      createdAt: '2024-05-20',
      teamSize: '5人',
      description: '基于AI的校园服务一体化平台，提供智能问答、课表管理、校园导航等功能，致力于打造智慧校园生态系统。',
      businessPlan: '目标市场为全国高校，预计第一年覆盖50所高校，第二年实现盈利。',
      teamIntro: '核心团队由来自计算机学院、管理学院和经济学院的5名优秀学生组成，具备AI开发、产品设计和市场推广能力。'
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
  color: var(--text-primary);
}

.detail-section {
  margin: 20px 0;
}

.detail-section h3 {
  font-size: 16px;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-left: 10px;
  border-left: 3px solid var(--primary-color);
}

.detail-section p {
  color: var(--text-regular);
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
