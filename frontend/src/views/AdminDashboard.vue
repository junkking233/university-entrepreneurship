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
  totalUsers: 1250,
  totalProjects: 480,
  pendingAudit: 15,
  pendingFeedback: 8
})

const pieChartRef = ref(null)
const userPieChartRef = ref(null)
const barChartRef = ref(null)

let pieChart = null
let userPieChart = null
let barChart = null

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
      data: [
        { value: 120, name: '人工智能' },
        { value: 95, name: '环保科技' },
        { value: 80, name: '教育科技' },
        { value: 65, name: '医疗健康' },
        { value: 55, name: '电子商务' },
        { value: 45, name: '金融科技' },
        { value: 20, name: '其他' }
      ]
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
      data: [
        { value: 850, name: '创业者' },
        { value: 180, name: '导师' },
        { value: 120, name: '投资者' },
        { value: 100, name: '管理员' }
      ]
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
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: {
      type: 'value',
      name: '项目数量'
    },
    series: [
      {
        name: '新增项目',
        type: 'bar',
        data: [15, 22, 30, 28, 35, 42, 38, 45, 50, 48, 52, 55],
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
        data: [10, 18, 25, 22, 30, 38, 32, 40, 45, 42, 48, 50],
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

function handleResize() {
  pieChart?.resize()
  userPieChart?.resize()
  barChart?.resize()
}

onMounted(() => {
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
