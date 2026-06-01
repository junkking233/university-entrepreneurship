<template>
  <div class="investor-dashboard">
    <h2 class="page-title">投资者工作台</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#409eff"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.investCount }}</div>
              <div class="stat-label">投资项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#67c23a"><Coin /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalAmount }}万</div>
              <div class="stat-label">投资总额</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#e6a23c"><Collection /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.interestedCount }}</div>
              <div class="stat-label">关注项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#f56c6c"><TrendCharts /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.successRate }}%</div>
              <div class="stat-label">成功率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新投资项目 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">最新投资项目</span>
          <el-button type="primary" link @click="$router.push('/investor/investments')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentInvestments" stripe>
        <el-table-column prop="projectTitle" label="项目名称" min-width="200" />
        <el-table-column prop="amount" label="投资金额" width="150">
          <template #default="{ row }">
            <span class="amount-text">{{ row.amount }}万</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="investDate" label="投资时间" width="160" />
      </el-table>
      <el-empty v-if="recentInvestments.length === 0" description="暂无投资记录" />
    </el-card>

    <!-- 推荐项目 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">推荐项目</span>
          <el-button type="primary" link @click="$router.push('/projects')">浏览更多</el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="item in recommendedProjects" :key="item.id">
          <el-card shadow="hover" class="project-card" @click="$router.push(`/projects/${item.id}`)">
            <h3>{{ item.title }}</h3>
            <p class="project-desc">{{ item.description }}</p>
            <div class="project-meta">
              <el-tag size="small">{{ item.category }}</el-tag>
              <span class="funding">{{ item.fundingTarget ? `${item.fundingTarget}万` : '-' }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="recommendedProjects.length === 0" description="暂无推荐项目" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Money, Coin, Collection, TrendCharts } from '@element-plus/icons-vue'
import { getMyInvestments } from '@/api/investment'
import { getPublicProjectList } from '@/api/project'

const stats = reactive({
  investCount: 0,
  totalAmount: 0,
  interestedCount: 0,
  successRate: 0
})

const recentInvestments = ref([])

const recommendedProjects = ref([])

function statusType(status) {
  const map = { pending: 'warning', confirmed: 'success', completed: 'info', cancelled: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待确认', confirmed: '已确认', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

function getListData(res) {
  return res.data?.list || res.data?.records || res.data || []
}

async function fetchDashboard() {
  const [investmentRes, projectRes] = await Promise.allSettled([
    getMyInvestments({ page: 1, pageSize: 5 }),
    getPublicProjectList({ page: 1, pageSize: 3 })
  ])

  if (investmentRes.status === 'fulfilled') {
    const list = getListData(investmentRes.value)
    recentInvestments.value = list
    stats.investCount = investmentRes.value.data?.total || list.length
    stats.totalAmount = list.reduce((sum, item) => sum + Number(item.amount || 0), 0)
    const completed = list.filter((item) => item.status === 'completed').length
    stats.successRate = list.length ? Math.round((completed / list.length) * 100) : 0
  } else {
    recentInvestments.value = []
    stats.investCount = 0
    stats.totalAmount = 0
    stats.successRate = 0
  }

  if (projectRes.status === 'fulfilled') {
    recommendedProjects.value = getListData(projectRes.value)
    stats.interestedCount = projectRes.value.data?.total || recommendedProjects.value.length
  } else {
    recommendedProjects.value = []
    stats.interestedCount = 0
  }
}

onMounted(() => {
  fetchDashboard()
})
</script>

<style scoped>
.investor-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 24px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  cursor: default;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.section-card {
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
}

.amount-text {
  color: #e6a23c;
  font-weight: bold;
}

.project-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.project-card:hover {
  transform: translateY(-4px);
}

.project-card h3 {
  font-size: 16px;
  margin-bottom: 8px;
}

.project-desc {
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.funding {
  color: #e6a23c;
  font-weight: bold;
}
</style>
