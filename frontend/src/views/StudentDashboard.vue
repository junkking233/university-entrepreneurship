<template>
  <div class="student-dashboard">
    <h2 class="page-title">创业者工作台</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#2563eb"><FolderOpened /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.projectCount }}</div>
              <div class="stat-label">我的项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#10b981"><ChatDotRound /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.consultCount }}</div>
              <div class="stat-label">咨询记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#f59e0b"><Reading /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.trainingCount }}</div>
              <div class="stat-label">参加培训</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#f43f5e"><Star /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.favoriteCount }}</div>
              <div class="stat-label">收藏项目</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-card class="section-card">
      <template #header>
        <span class="section-title">快捷操作</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="8" v-for="action in quickActions" :key="action.label">
          <el-button class="quick-action-btn" @click="$router.push(action.path)">
            <el-icon :size="24"><component :is="action.icon" /></el-icon>
            <span>{{ action.label }}</span>
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 我的项目列表 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">我的项目</span>
          <el-button type="primary" size="small" @click="$router.push('/student/projects/create')">
            发布新项目
          </el-button>
        </div>
      </template>
      <el-table :data="myProjects" stripe>
        <el-table-column prop="title" label="项目名称" min-width="200" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/student/projects/create?id=${row.id}`)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="myProjects.length === 0" description="暂无项目，快去发布吧" />
    </el-card>

    <!-- 最近咨询 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">最近咨询</span>
          <el-button type="primary" link @click="$router.push('/student/consultations')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentConsultations" stripe>
        <el-table-column prop="mentorName" label="导师" width="120" />
        <el-table-column prop="projectTitle" label="关联项目" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'replied' ? 'success' : 'warning'" size="small">
              {{ row.status === 'replied' ? '已回复' : '待回复' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="咨询时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { FolderOpened, ChatDotRound, Reading, Star, Plus, Search, EditPen } from '@element-plus/icons-vue'
import { getMyProjects, deleteProject } from '@/api/project'

const stats = reactive({
  projectCount: 3,
  consultCount: 5,
  trainingCount: 2,
  favoriteCount: 8
})

const quickActions = [
  { label: '发布项目', path: '/student/projects/create', icon: 'Plus' },
  { label: '浏览项目', path: '/projects', icon: 'Search' },
  { label: '我的咨询', path: '/student/consultations', icon: 'EditPen' }
]

const myProjects = ref([])
const recentConsultations = ref([])

function statusType(status) {
  const map = { draft: 'info', pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { draft: '草稿', pending: '审核中', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该项目吗？', '提示', { type: 'warning' })
    await deleteProject(row.id)
    ElMessage.success('删除成功')
    fetchProjects()
  } catch {
    // cancelled or error
  }
}

async function fetchProjects() {
  try {
    const res = await getMyProjects()
    myProjects.value = res.data?.list || res.data || []
  } catch {
    myProjects.value = [
      { id: 1, title: '智能校园助手', category: '人工智能', status: 'approved', createdAt: '2024-05-20' },
      { id: 2, title: '在线教育平台', category: '教育科技', status: 'pending', createdAt: '2024-05-22' }
    ]
  }
}

onMounted(() => {
  fetchProjects()
  recentConsultations.value = [
    { mentorName: '张教授', projectTitle: '智能校园助手', status: 'replied', createdAt: '2024-05-21' }
  ]
})
</script>

<style scoped>
.student-dashboard {
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

.quick-action-btn {
  width: 100%;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
}
</style>
