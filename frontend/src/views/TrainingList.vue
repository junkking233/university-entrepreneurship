<template>
  <div class="training-list-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>培训活动</h2>
        </div>
      </template>

      <div class="search-bar">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索培训活动"
              clearable
              @clear="fetchList"
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchStatus" placeholder="状态筛选" clearable @change="fetchList">
              <el-option label="即将开始" value="upcoming" />
              <el-option label="进行中" value="ongoing" />
              <el-option label="已结束" value="finished" />
            </el-select>
          </el-col>
        </el-row>
      </div>

      <el-table :data="trainingList" v-loading="loading" stripe>
        <el-table-column prop="title" label="活动名称" min-width="200" />
        <el-table-column prop="speaker" label="主讲人" width="150" />
        <el-table-column prop="time" label="活动时间" width="180" />
        <el-table-column prop="location" label="地点" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enrolled" label="报名人数" width="100">
          <template #default="{ row }">
            {{ row.enrolled }}/{{ row.capacity }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 'finished'"
              type="primary"
              size="small"
              @click="handleEnroll(row)"
            >
              报名
            </el-button>
            <el-tag v-else type="info" size="small">已结束</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && trainingList.length === 0" description="暂无培训活动" />

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getTrainingList, enrollTraining } from '@/api/training'

const loading = ref(false)
const trainingList = ref([])
const searchKeyword = ref('')
const searchStatus = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { upcoming: 'primary', ongoing: 'success', finished: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { upcoming: '即将开始', ongoing: '进行中', finished: '已结束' }
  return map[status] || status
}

async function handleEnroll(row) {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再报名')
    return
  }
  try {
    await enrollTraining(row.id)
    ElMessage.success('报名成功')
    fetchList()
  } catch {
    // error handled in interceptor
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getTrainingList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      status: searchStatus.value
    })
    trainingList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    trainingList.value = []
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
.training-list-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.search-bar {
  margin-bottom: 20px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
