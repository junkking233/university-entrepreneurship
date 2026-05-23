<template>
  <div class="my-investments-page">
    <el-card>
      <template #header>
        <h2>我的投资记录</h2>
      </template>

      <!-- 筛选 -->
      <div class="filter-bar">
        <el-radio-group v-model="filterStatus" @change="fetchList">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button value="pending">待确认</el-radio-button>
          <el-radio-button value="active">进行中</el-radio-button>
          <el-radio-button value="completed">已完成</el-radio-button>
        </el-radio-group>
      </div>

      <el-table :data="investmentList" v-loading="loading" stripe>
        <el-table-column prop="projectTitle" label="项目名称" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push(`/projects/${row.projectId}`)">
              {{ row.projectTitle }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="投资金额" width="150">
          <template #default="{ row }">
            <span class="amount-text">{{ row.amount }}万</span>
          </template>
        </el-table-column>
        <el-table-column prop="equity" label="占股比例" width="120">
          <template #default="{ row }">
            {{ row.equity }}%
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
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && investmentList.length === 0" description="暂无投资记录" />

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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="投资详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="项目名称">{{ currentItem.projectTitle }}</el-descriptions-item>
        <el-descriptions-item label="投资金额">{{ currentItem.amount }}万</el-descriptions-item>
        <el-descriptions-item label="占股比例">{{ currentItem.equity }}%</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentItem.status)" size="small">
            {{ statusLabel(currentItem.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投资时间">{{ currentItem.investDate }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentItem.note || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getMyInvestments } from '@/api/investment'

const loading = ref(false)
const investmentList = ref([])
const filterStatus = ref('')
const detailVisible = ref(false)
const currentItem = ref({})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { pending: 'warning', active: 'success', completed: 'info', cancelled: 'danger' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待确认', active: '进行中', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}

function showDetail(row) {
  currentItem.value = row
  detailVisible.value = true
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMyInvestments({
      page: pagination.page,
      pageSize: pagination.pageSize,
      status: filterStatus.value
    })
    investmentList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    investmentList.value = []
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
.my-investments-page {
  max-width: 1200px;
  margin: 0 auto;
}

.my-investments-page h2 {
  margin: 0;
  font-size: 20px;
}

.filter-bar {
  margin-bottom: 20px;
}

.amount-text {
  color: #e6a23c;
  font-weight: bold;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
