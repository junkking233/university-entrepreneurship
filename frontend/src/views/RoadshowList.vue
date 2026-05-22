<template>
  <div class="roadshow-list-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>路演预告</h2>
        </div>
      </template>

      <div class="search-bar">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索路演活动"
              clearable
              @clear="fetchList"
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
        </el-row>
      </div>

      <el-table :data="roadshowList" v-loading="loading" stripe>
        <el-table-column prop="title" label="路演名称" min-width="250" />
        <el-table-column prop="time" label="路演时间" width="180" />
        <el-table-column prop="location" label="地点" width="200" />
        <el-table-column prop="organizer" label="主办方" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectCount" label="参与项目" width="100" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'upcoming'"
              type="primary"
              size="small"
              @click="handleEnroll(row)"
            >
              报名参加
            </el-button>
            <el-button v-else size="small" disabled>已结束</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
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
import { getRoadshowList, enrollRoadshow } from '@/api/roadshow'

const loading = ref(false)
const roadshowList = ref([])
const searchKeyword = ref('')

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
    await enrollRoadshow(row.id)
    ElMessage.success('报名成功')
  } catch {
    // error handled in interceptor
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getRoadshowList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value
    })
    roadshowList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    roadshowList.value = [
      { id: 1, title: '第六届大学生创业路演大赛', time: '2024-07-15 14:00', location: '学校大礼堂', organizer: '创新创业学院', status: 'upcoming', projectCount: 25 },
      { id: 2, title: '科技创新项目专场路演', time: '2024-08-01 09:30', location: '科技园国际会议中心', organizer: '科技园管委会', status: 'upcoming', projectCount: 15 },
      { id: 3, title: '春季创业项目路演', time: '2024-04-10 13:00', location: '大学生活动中心', organizer: '创业协会', status: 'finished', projectCount: 20 }
    ]
    pagination.total = 3
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.roadshow-list-page {
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
