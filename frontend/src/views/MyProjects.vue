<template>
  <div class="my-projects-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>我的项目</h2>
          <el-button type="primary" @click="$router.push('/student/projects/create')">
            <el-icon><Plus /></el-icon> 发布新项目
          </el-button>
        </div>
      </template>

      <el-table :data="projectList" v-loading="loading" stripe>
        <el-table-column prop="title" label="项目名称" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/projects/${row.id}`)">
              {{ row.title }}
            </el-link>
          </template>
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
        <el-table-column prop="fundingTarget" label="融资金额" width="120">
          <template #default="{ row }">
            {{ row.fundingTarget }}万
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/student/projects/edit/${row.id}`)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && projectList.length === 0" description="暂无项目">
        <el-button type="primary" @click="$router.push('/student/projects/create')">发布项目</el-button>
      </el-empty>

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, prev, pager, next"
          background
          @current-change="fetchProjects"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyProjects, deleteProject } from '@/api/project'

const loading = ref(false)
const projectList = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

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
    // cancelled
  }
}

async function fetchProjects() {
  loading.value = true
  try {
    const res = await getMyProjects({ page: pagination.page, pageSize: pagination.pageSize })
    projectList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    projectList.value = [
      { id: 1, title: '智能校园助手', category: '人工智能', status: 'approved', fundingTarget: 50, createdAt: '2024-05-20' },
      { id: 2, title: '在线教育平台', category: '教育科技', status: 'pending', fundingTarget: 80, createdAt: '2024-05-22' }
    ]
    pagination.total = 2
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchProjects()
})
</script>

<style scoped>
.my-projects-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
