<template>
  <div class="mentor-dashboard">
    <h2 class="page-title">导师工作台</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#409eff"><ChatDotRound /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingConsult }}</div>
              <div class="stat-label">待回复咨询</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#67c23a"><Reading /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.trainingCount }}</div>
              <div class="stat-label">发布培训</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#e6a23c"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount }}</div>
              <div class="stat-label">服务学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon :size="36" color="#f56c6c"><Star /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rating }}</div>
              <div class="stat-label">评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待处理咨询 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">待处理咨询</span>
          <el-button type="primary" link @click="$router.push('/mentor/consultations')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="pendingConsultations" stripe>
        <el-table-column prop="studentName" label="学生" width="120" />
        <el-table-column prop="content" label="咨询内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="咨询时间" width="160" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openReply(row)">回复</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="pendingConsultations.length === 0" description="暂无待处理咨询" />
    </el-card>

    <!-- 我的培训活动 -->
    <el-card class="section-card">
      <template #header>
        <div class="section-header">
          <span class="section-title">我的培训活动</span>
          <el-button type="primary" link @click="$router.push('/mentor/trainings')">管理培训</el-button>
        </div>
      </template>
      <el-table :data="myTrainings" stripe>
        <el-table-column prop="title" label="活动名称" min-width="200" />
        <el-table-column prop="time" label="活动时间" width="180" />
        <el-table-column prop="location" label="地点" width="180" />
        <el-table-column label="报名" width="80">
          <template #default="{ row }">
            {{ row.enrolled }}/{{ row.capacity }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="myTrainings.length === 0" description="暂无培训活动" />
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyVisible" title="回复咨询" width="500px">
      <el-form :model="replyForm">
        <el-form-item label="咨询内容">
          <p>{{ currentConsult.content }}</p>
        </el-form-item>
        <el-form-item label="回复">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Reading, User, Star } from '@element-plus/icons-vue'
import { getMentorConsultations, updateConsultationStatus } from '@/api/mentor'
import { getMentorTrainings } from '@/api/mentor'

const stats = reactive({
  pendingConsult: 0,
  trainingCount: 0,
  studentCount: 0,
  rating: 0
})

const pendingConsultations = ref([])
const myTrainings = ref([])
const replyVisible = ref(false)
const currentConsult = ref({})

const replyForm = reactive({
  reply: ''
})

function openReply(row) {
  currentConsult.value = row
  replyForm.reply = ''
  replyVisible.value = true
}

async function submitReply() {
  if (!replyForm.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await updateConsultationStatus(currentConsult.value.id, {
      reply: replyForm.reply,
      status: 'replied'
    })
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchData()
  } catch {
    // error handled
  }
}

async function fetchData() {
  try {
    const [consultRes, trainingRes] = await Promise.all([
      getMentorConsultations({ status: 'pending' }),
      getMentorTrainings()
    ])
    pendingConsultations.value = consultRes.data?.list || consultRes.data || []
    myTrainings.value = trainingRes.data?.list || trainingRes.data || []
    stats.pendingConsult = consultRes.data?.total || pendingConsultations.value.length
    stats.trainingCount = trainingRes.data?.total || myTrainings.value.length
    stats.studentCount = pendingConsultations.value.length
  } catch {
    pendingConsultations.value = []
    myTrainings.value = []
    stats.pendingConsult = 0
    stats.trainingCount = 0
    stats.studentCount = 0
    stats.rating = 0
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.mentor-dashboard {
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
</style>
