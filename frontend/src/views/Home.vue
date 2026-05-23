<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <el-carousel :interval="5000" arrow="always" height="400px" class="home-carousel">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="carousel-item" :style="{ backgroundColor: item.bgColor }">
          <div class="carousel-text">
            <h2>{{ item.title }}</h2>
            <p>{{ item.description }}</p>
            <el-button type="primary" size="large" round>了解更多</el-button>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 统计数据 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6" v-for="stat in stats" :key="stat.label">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <el-icon :size="36" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 创业项目展示 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">热门创业项目</span>
          <el-button type="primary" link @click="$router.push('/projects')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="item in hotProjects" :key="item.id">
          <el-card shadow="hover" class="project-card" @click="$router.push(`/projects/${item.id}`)">
            <div class="project-image">
              <el-icon :size="60" color="#2563eb"><FolderOpened /></el-icon>
            </div>
            <h3>{{ item.title }}</h3>
            <p class="project-desc">{{ item.description }}</p>
            <div class="project-meta">
              <el-tag size="small" effect="light" type="primary">{{ item.category }}</el-tag>
              <span class="project-funding">融资: {{ item.funding }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 培训活动 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">最新培训活动</span>
          <el-button type="primary" link @click="$router.push('/trainings')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="item in trainings" :key="item.id">
          <el-card shadow="hover" class="training-card">
            <div class="training-date">
              <div class="date-day">{{ item.day }}</div>
              <div class="date-month">{{ item.month }}</div>
            </div>
            <div class="training-info">
              <h3>{{ item.title }}</h3>
              <p><el-icon><User /></el-icon> {{ item.speaker }}</p>
              <p><el-icon><Location /></el-icon> {{ item.location }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 路演预告 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">路演预告</span>
          <el-button type="primary" link @click="$router.push('/roadshows')">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="item in roadshows"
          :key="item.id"
          :timestamp="item.time"
          placement="top"
          :color="item.status === 'upcoming' ? '#2563eb' : '#10b981'"
        >
          <el-card shadow="hover">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <div class="roadshow-meta">
              <el-tag :type="item.status === 'upcoming' ? 'primary' : 'success'" size="small" effect="light">
                {{ item.status === 'upcoming' ? '即将开始' : '进行中' }}
              </el-tag>
              <span>{{ item.location }}</span>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  TrendCharts, User, DataAnalysis, TrophyBase,
  FolderOpened, ArrowRight, Location
} from '@element-plus/icons-vue'

const banners = ref([
  { id: 1, title: '点燃创业梦想', description: '大学生创业孵化平台，为你的创意插上翅膀', bgColor: '#2563eb' },
  { id: 2, title: '汇聚创新力量', description: '与顶尖导师、投资者面对面交流', bgColor: '#1d4ed8' },
  { id: 3, title: '成就未来之星', description: '优质项目路演、投资对接一站式服务', bgColor: '#1e40af' }
])

const stats = ref([
  { label: '创业项目', value: '1,280+', icon: 'TrendCharts', color: '#2563eb' },
  { label: '导师团队', value: '350+', icon: 'User', color: '#10b981' },
  { label: '投资机构', value: '180+', icon: 'DataAnalysis', color: '#f59e0b' },
  { label: '成功孵化', value: '650+', icon: 'TrophyBase', color: '#f43f5e' }
])

const hotProjects = ref([
  {
    id: 1,
    title: '智能校园助手',
    description: '基于AI的校园服务一体化平台，提供智能问答、课表管理等功能',
    category: '人工智能',
    funding: '50万'
  },
  {
    id: 2,
    title: '绿色循环快递盒',
    description: '可重复使用的环保快递包装解决方案，减少快递垃圾',
    category: '环保科技',
    funding: '100万'
  },
  {
    id: 3,
    title: 'VR虚拟实验室',
    description: '面向高校的虚拟仿真实验教学平台，降低实验成本',
    category: '教育科技',
    funding: '80万'
  }
])

const trainings = ref([
  {
    id: 1,
    day: '15',
    month: '6月',
    title: '创业计划书撰写技巧',
    speaker: '张教授 - 创业导师',
    location: '大学生活动中心301'
  },
  {
    id: 2,
    day: '20',
    month: '6月',
    title: '商业模式创新工作坊',
    speaker: '李总 - 天使投资人',
    location: '创新创业学院报告厅'
  },
  {
    id: 3,
    day: '28',
    month: '6月',
    title: '融资路演实战演练',
    speaker: '王导师 - 资深投资人',
    location: '科技园路演大厅'
  }
])

const roadshows = ref([
  {
    id: 1,
    title: '第六届大学生创业路演大赛',
    description: '汇聚全国高校优秀创业项目，现场对接投资人',
    time: '2024-07-15 14:00',
    status: 'upcoming',
    location: '学校大礼堂'
  },
  {
    id: 2,
    title: '科技创新项目专场路演',
    description: '聚焦人工智能、新能源、生物医药等前沿领域',
    time: '2024-08-01 09:30',
    status: 'upcoming',
    location: '科技园国际会议中心'
  }
])
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

.home-carousel {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.carousel-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.carousel-text {
  text-align: center;
}

.carousel-text h2 {
  font-size: 36px;
  margin-bottom: 16px;
}

.carousel-text p {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  cursor: default;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-info {
  flex: 1;
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
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.project-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.project-card:hover {
  transform: translateY(-4px);
}

.project-image {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f5ff;
  border-radius: 8px;
  margin-bottom: 12px;
}

.project-card h3 {
  font-size: 16px;
  margin-bottom: 8px;
}

.project-desc {
  color: #909399;
  font-size: 13px;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.project-funding {
  color: #e6a23c;
  font-size: 13px;
}

.training-card {
  display: flex;
  gap: 16px;
}

.training-date {
  text-align: center;
  min-width: 60px;
  background: #ecf5ff;
  border-radius: 8px;
  padding: 10px;
}

.date-day {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}

.date-month {
  font-size: 13px;
  color: #409eff;
}

.training-info h3 {
  font-size: 16px;
  margin-bottom: 8px;
}

.training-info p {
  color: #909399;
  font-size: 13px;
  margin: 4px 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.roadshow-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  color: #909399;
  font-size: 13px;
}
</style>
