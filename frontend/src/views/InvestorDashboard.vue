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
              <span class="funding">{{ item.fundingTarget }}万</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Money, Coin, Collection, TrendCharts } from '@element-plus/icons-vue'

const stats = reactive({
  investCount: 5,
  totalAmount: 350,
  interestedCount: 12,
  successRate: 80
})

const recentInvestments = ref([
  { id: 1, projectTitle: '智能校园助手', amount: 50, status: 'active', investDate: '2024-05-20' },
  { id: 2, projectTitle: '绿色循环快递盒', amount: 100, status: 'pending', investDate: '2024-05-18' }
])

const recommendedProjects = ref([
  { id: 1, title: 'VR虚拟实验室', category: '教育科技', fundingTarget: 80, description: '面向高校的虚拟仿真实验教学平台' },
  { id: 2, title: '智慧医疗助手', category: '医疗健康', fundingTarget: 200, description: 'AI辅助诊疗系统' },
  { id: 3, title: '碳中和解决方案', category: '环保科技', fundingTarget: 150, description: '企业碳排放管理平台' }
])

function statusType(status) {
  const map = { active: 'success', pending: 'warning', completed: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { active: '进行中', pending: '待确认', completed: '已完成' }
  return map[status] || status
}
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
