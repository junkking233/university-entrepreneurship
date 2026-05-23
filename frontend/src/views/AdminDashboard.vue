<template>
  <div class="admin-dashboard">
    <h2 class="page-title">管理后台</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#409eff"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#67c23a"><FolderOpened /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalProjects }}</div>
              <div class="stat-label">总项目数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#e6a23c"><Clock /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingAudit }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#f56c6c"><ChatLineSquare /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingFeedback }}</div>
              <div class="stat-label">待处理反馈</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 饼图：项目分类分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">项目分类分布</span>
          </template>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 饼图：用户角色分布 -->
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span class="chart-title">用户角色分布</span>
          </template>
          <div ref="userPieChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 直方图：月度项目趋势 -->
    <el-card class="chart-card" style="margin-bottom: 24px;">
      <template #header>
        <span class="chart-title">月度项目趋势</span>
      </template>
      <div ref="barChartRef" class="chart-container" style="height: 350px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { User, FolderOpened, Clock, ChatLineSquare } from '@element-plus/icons-vue'
import { getStatistics, getProjectStats, getUserStats, getMonthlyTrend } from '@/api/statistics'

const stats = reactive({
  totalUsers: 0,
  totalProjects: 0,
  pendingAudit: 0,
  pendingFeedback: 0
})

const pieChartRef = ref(null)
const userPieChartRef = ref(null)
const barChartRef = ref(null)

let pieChart = null
let userPieChart = null
let barChart = null
const projectCategoryData = ref([])
const userRoleData = ref([])
const monthlyTrendData = ref({
  months: [],
  created: [],
  approved: []
})

function initPieChart() {
  if (!pieChartRef.value) return
  pieChart = echarts.init(pieChartRef.value)
  pieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    series: [{
      name: '项目分类',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      data: projectCategoryData.value
    }]
  })
}

function initUserPieChart() {
  if (!userPieChartRef.value) return
  userPieChart = echarts.init(userPieChartRef.value)
  userPieChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%' },
    color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c'],
    series: [{
      name: '用户角色',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '45%'],
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}: {c}'
      },
      data: userRoleData.value
    }]
  })
}

function initBarChart() {
  if (!barChartRef.value) return
  barChart = echarts.init(barChartRef.value)
  barChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: monthlyTrendData.value.months
    },
    yAxis: {
      type: 'value',
      name: '项目数量'
    },
    series: [
      {
        name: '新增项目',
        type: 'bar',
        data: monthlyTrendData.value.created,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#79bbff' }
          ])
        },
        barWidth: '50%'
      },
      {
        name: '通过审核',
        type: 'bar',
        data: monthlyTrendData.value.approved,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#95d475' }
          ])
        },
        barWidth: '50%'
      }
    ]
  })
}

function normalizeChartData(res, nameKeys = ['name', 'label', 'category', 'role'], valueKeys = ['value', 'count', 'total']) {
  const list = res.data?.list || res.data?.records || res.data || []
  return list.map((item) => ({
    name: nameKeys.map((key) => item[key]).find((value) => value !== undefined && value !== null) || '-',
    value: valueKeys.map((key) => item[key]).find((value) => value !== undefined && value !== null) || 0
  }))
}

function normalizeTrendData(res) {
  const list = res.data?.list || res.data?.records || res.data || []
  monthlyTrendData.value = {
    months: list.map((item) => item.month || item.name || item.label || ''),
    created: list.map((item) => item.created || item.newProjects || item.count || 0),
    approved: list.map((item) => item.approved || item.approvedProjects || 0)
  }
}

async function fetchDashboardData() {
  const [statsRes, projectStatsRes, userStatsRes, trendRes] = await Promise.allSettled([
    getStatistics(),
    getProjectStats(),
    getUserStats(),
    getMonthlyTrend()
  ])

  if (statsRes.status === 'fulfilled') {
    const data = statsRes.value.data || {}
    stats.totalUsers = data.totalUsers || data.userCount || 0
    stats.totalProjects = data.totalProjects || data.projectCount || 0
    stats.pendingAudit = data.pendingAudit || data.pendingProjects || 0
    stats.pendingFeedback = data.pendingFeedback || data.feedbackCount || 0
  }

  projectCategoryData.value = projectStatsRes.status === 'fulfilled' ? normalizeChartData(projectStatsRes.value) : []
  userRoleData.value = userStatsRes.status === 'fulfilled' ? normalizeChartData(userStatsRes.value) : []
  if (trendRes.status === 'fulfilled') {
    normalizeTrendData(trendRes.value)
  } else {
    monthlyTrendData.value = { months: [], created: [], approved: [] }
  }
}

function handleResize() {
  pieChart?.resize()
  userPieChart?.resize()
  barChart?.resize()
}

onMounted(async () => {
  await fetchDashboardData()
  initPieChart()
  initUserPieChart()
  initBarChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  userPieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.admin-dashboard {
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

.chart-row {
  margin-bottom: 24px;
}

.chart-card {
  margin-bottom: 0;
}

.chart-title {
  font-size: 16px;
  font-weight: bold;
}

.chart-container {
  width: 100%;
  height: 320px;
}
</style>
