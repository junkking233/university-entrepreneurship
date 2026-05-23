<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <el-carousel v-if="banners.length > 0" :interval="5000" arrow="always" height="400px" class="home-carousel">
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
    <el-empty v-else class="home-empty" description="暂无轮播数据" />

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
              <el-icon :size="60" color="#409eff"><FolderOpened /></el-icon>
            </div>
            <h3>{{ item.title }}</h3>
            <p class="project-desc">{{ item.description }}</p>
            <div class="project-meta">
              <el-tag size="small" type="primary">{{ item.category }}</el-tag>
              <span class="project-funding">融资: {{ item.funding }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="hotProjects.length === 0" description="暂无热门项目" />
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
      <el-empty v-if="trainings.length === 0" description="暂无培训活动" />
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
      <el-timeline v-if="roadshows.length > 0">
        <el-timeline-item
          v-for="item in roadshows"
          :key="item.id"
          :timestamp="item.time"
          placement="top"
          :color="item.status === 'upcoming' ? '#409eff' : '#67c23a'"
        >
          <el-card shadow="hover">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
            <div class="roadshow-meta">
              <el-tag :type="item.status === 'upcoming' ? 'primary' : 'success'" size="small">
                {{ item.status === 'upcoming' ? '即将开始' : '进行中' }}
              </el-tag>
              <span>{{ item.location }}</span>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无路演预告" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  TrendCharts, User, DataAnalysis, TrophyBase,
  FolderOpened, ArrowRight, Location
} from '@element-plus/icons-vue'
import { getPublicProjectList } from '@/api/project'
import { getTrainingList } from '@/api/training'
import { getRoadshowList } from '@/api/roadshow'

const banners = ref([])
const hotProjects = ref([])
const trainings = ref([])
const roadshows = ref([])

const stats = ref([
  { label: '创业项目', value: 0, icon: 'TrendCharts', color: '#409eff' },
  { label: '培训活动', value: 0, icon: 'User', color: '#67c23a' },
  { label: '路演活动', value: 0, icon: 'DataAnalysis', color: '#e6a23c' },
  { label: '展示项目', value: 0, icon: 'TrophyBase', color: '#f56c6c' }
])

function getListData(res) {
  return res.data?.list || res.data?.records || res.data || []
}

function getTotal(res, list) {
  return res.data?.total || res.data?.count || list.length
}

function formatFunding(item) {
  const value = item.funding || item.fundingTarget || item.targetAmount || 0
  return `${value}万`
}

function parseDate(value) {
  const date = value ? new Date(value) : null
  return date && !Number.isNaN(date.getTime()) ? date : null
}

function mapTraining(item) {
  const date = parseDate(item.time || item.startTime || item.createdAt)
  return {
    ...item,
    day: date ? String(date.getDate()).padStart(2, '0') : '--',
    month: date ? `${date.getMonth() + 1}月` : '--',
    speaker: item.speaker || item.mentorName || item.teacherName || '-',
    location: item.location || '-'
  }
}

function mapRoadshow(item) {
  return {
    ...item,
    time: item.time || item.startTime || item.createdAt || '',
    description: item.description || item.summary || '',
    location: item.location || '-'
  }
}

async function fetchHomeData() {
  const [projectRes, trainingRes, roadshowRes] = await Promise.allSettled([
    getPublicProjectList({ page: 1, pageSize: 3 }),
    getTrainingList({ page: 1, pageSize: 3 }),
    getRoadshowList({ page: 1, pageSize: 3 })
  ])

  if (projectRes.status === 'fulfilled') {
    const list = getListData(projectRes.value)
    hotProjects.value = list.map((item) => ({ ...item, funding: formatFunding(item) }))
    banners.value = hotProjects.value.map((item, index) => ({
      id: item.id || index,
      title: item.title || item.name || '项目',
      description: item.description || item.summary || '',
      bgColor: ['#409eff', '#337ecc', '#2d6cbf'][index % 3]
    }))
    stats.value[0].value = getTotal(projectRes.value, list)
    stats.value[3].value = list.length
  } else {
    hotProjects.value = []
    banners.value = []
  }

  if (trainingRes.status === 'fulfilled') {
    const list = getListData(trainingRes.value)
    trainings.value = list.map(mapTraining)
    stats.value[1].value = getTotal(trainingRes.value, list)
  } else {
    trainings.value = []
  }

  if (roadshowRes.status === 'fulfilled') {
    const list = getListData(roadshowRes.value)
    roadshows.value = list.map(mapRoadshow)
    stats.value[2].value = getTotal(roadshowRes.value, list)
  } else {
    roadshows.value = []
  }
}

onMounted(() => {
  fetchHomeData()
})
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

.home-empty {
  margin-bottom: 30px;
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
