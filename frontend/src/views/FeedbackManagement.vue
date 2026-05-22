<template>
  <div class="feedback-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-size: 18px; font-weight: bold">反馈管理</span>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable @change="fetchData" style="width: 150px">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已解决" value="resolved" />
          </el-select>
        </div>
      </template>

      <el-table :data="feedbacks" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.type || '建议' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待处理</el-tag>
            <el-tag v-else-if="row.status === 'processing'" type="info">处理中</el-tag>
            <el-tag v-else-if="row.status === 'resolved'" type="success">已解决</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleResolve(row)" v-if="row.status !== 'resolved'">
              标记解决
            </el-button>
            <el-button size="small" type="warning" @click="handleProcess(row)" v-if="row.status === 'pending'">
              处理中
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="fetchData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFeedbacks, updateFeedbackStatus } from '@/api/admin'

export default {
  name: 'FeedbackManagement',
  setup() {
    const feedbacks = ref([])
    const loading = ref(false)
    const total = ref(0)
    const pageNum = ref(1)
    const pageSize = ref(10)
    const filterStatus = ref('')

    const fetchData = async () => {
      loading.value = true
      try {
        const params = { page: pageNum.value, size: pageSize.value }
        if (filterStatus.value) params.status = filterStatus.value
        const res = await listFeedbacks(params)
        feedbacks.value = res.data?.records || []
        total.value = res.data?.total || 0
      } catch (err) {
        ElMessage.error('获取反馈列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleResolve = async (row) => {
      try {
        await ElMessageBox.confirm('确认将该反馈标记为已解决？', '提示')
        await updateFeedbackStatus(row.id, 'resolved')
        ElMessage.success('已标记为已解决')
        fetchData()
      } catch {}
    }

    const handleProcess = async (row) => {
      try {
        await updateFeedbackStatus(row.id, 'processing')
        ElMessage.success('已标记为处理中')
        fetchData()
      } catch (err) {
        ElMessage.error('操作失败')
      }
    }

    onMounted(fetchData)

    return { feedbacks, loading, total, pageNum, pageSize, filterStatus, fetchData, handleResolve, handleProcess }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
