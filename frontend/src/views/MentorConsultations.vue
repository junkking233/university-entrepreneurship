<template>
  <div class="mentor-consultations-page">
    <el-card>
      <template #header>
        <h2>咨询管理</h2>
      </template>

      <!-- 筛选 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterStatus" @change="fetchList">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待回复</el-radio-button>
          <el-radio-button value="completed">已完成</el-radio-button>
          <el-radio-button value="cancelled">已取消</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="consultationList" v-loading="loading" stripe>
        <el-table-column label="学生" width="120">
          <template #default="{ row }">
            {{ row.studentName || (row.studentId ? `学生${row.studentId}` : '-') }}
          </template>
        </el-table-column>
        <el-table-column label="关联项目" min-width="180">
          <template #default="{ row }">
            {{ row.projectTitle || (row.projectId ? `项目${row.projectId}` : '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="咨询内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="咨询时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="primary"
              size="small"
              @click="openReply(row)"
            >
              回复
            </el-button>
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && consultationList.length === 0" description="暂无咨询记录" />

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          background
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyVisible" title="回复咨询" width="500px">
      <el-descriptions :column="1" border style="margin-bottom: 16px;">
        <el-descriptions-item label="咨询内容">{{ currentItem.content }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="replyForm">
        <el-form-item label="回复内容">
          <el-input
            v-model="replyForm.reply"
            type="textarea"
            :rows="5"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply">提交回复</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="咨询详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="学生">{{ currentItem.studentName || (currentItem.studentId ? `学生${currentItem.studentId}` : '-') }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentItem.status)" size="small">
            {{ statusLabel(currentItem.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="咨询内容">{{ currentItem.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentItem.notes" label="我的回复">
          {{ currentItem.notes }}
        </el-descriptions-item>
        <el-descriptions-item label="咨询时间">{{ currentItem.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMentorConsultations, updateConsultationStatus } from '@/api/mentor'

const loading = ref(false)
const consultationList = ref([])
const filterStatus = ref('')
const replyVisible = ref(false)
const detailVisible = ref(false)
const currentItem = ref({})

const replyForm = reactive({
  reply: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { pending: 'warning', accepted: 'primary', completed: 'success', cancelled: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待回复', accepted: '已受理', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

function openReply(row) {
  currentItem.value = row
  replyForm.reply = ''
  replyVisible.value = true
}

function showDetail(row) {
  currentItem.value = row
  detailVisible.value = true
}

async function submitReply() {
  if (!replyForm.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }
  try {
    await updateConsultationStatus(currentItem.value.id, {
      reply: replyForm.reply,
      status: 'completed'
    })
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchList()
  } catch {
    // error handled
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMentorConsultations({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value
    })
    const list = res.data?.records || res.data?.list || res.data || []
    consultationList.value = filterStatus.value ? list.filter((item) => item.status === filterStatus.value) : list
    pagination.total = res.data?.total || 0
  } catch {
    consultationList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.mentor-consultations-page {
  max-width: 1200px;
  margin: 0 auto;
}

.mentor-consultations-page h2 {
  margin: 0;
  font-size: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
