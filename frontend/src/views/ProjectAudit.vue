<template>
  <div class="project-audit-page">
    <el-card>
      <template #header>
        <h2>项目审核</h2>
      </template>

      <div class="filter-bar">
        <el-radio-group v-model="filterStatus" @change="fetchList">
          <el-radio-button value="pending">待审核</el-radio-button>
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="approved">已通过</el-radio-button>
          <el-radio-button value="rejected">已驳回</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="projectList" v-loading="loading" stripe>
        <el-table-column prop="title" label="项目名称" min-width="200" />
        <el-table-column label="创建者" width="120">
          <template #default="{ row }">{{ formatOwner(row) }}</template>
        </el-table-column>
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
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'pending'">
              <el-button type="success" size="small" @click="handleAudit(row, 'approved')">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="openReject(row)">
                驳回
              </el-button>
            </template>
            <el-button type="primary" link size="small" @click="showDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && projectList.length === 0" description="暂无项目数据" />

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

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectVisible" title="驳回原因" width="500px">
      <el-form :model="rejectForm">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="项目详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">{{ currentItem.title }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{ formatOwner(currentItem) }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ currentItem.category }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentItem.status)" size="small">
            {{ statusLabel(currentItem.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目评分">{{ currentItem.rating ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentItem.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目简介" :span="2">{{ currentItem.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingProjects, auditProject, getAllProjects } from '@/api/project'

const loading = ref(false)
const projectList = ref([])
const filterStatus = ref('pending')
const rejectVisible = ref(false)
const detailVisible = ref(false)
const currentItem = ref({})

const rejectForm = reactive({
  reason: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待审核', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

function formatOwner(row) {
  return row?.founder || row?.studentName || (row?.studentId ? `用户#${row.studentId}` : '-')
}

function showDetail(row) {
  currentItem.value = row
  detailVisible.value = true
}

function openReject(row) {
  currentItem.value = row
  rejectForm.reason = ''
  rejectVisible.value = true
}

async function handleAudit(row, status, reason = '') {
  try {
    await auditProject(row.id, { status, reason })
    ElMessage.success(status === 'approved' ? '审核通过' : '已驳回')
    fetchList()
  } catch {
    // error handled
  }
}

async function submitReject() {
  if (!rejectForm.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  await handleAudit(currentItem.value, 'rejected', rejectForm.reason)
  rejectVisible.value = false
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getAllProjects({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value
    })
    projectList.value = res.data?.records || res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    projectList.value = []
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
.project-audit-page {
  max-width: 1200px;
  margin: 0 auto;
}

.project-audit-page h2 {
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
