<template>
  <div class="project-list-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>创业项目</h2>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <div class="search-bar">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索项目名称"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.category" placeholder="项目分类" clearable @change="handleSearch">
              <el-option label="人工智能" value="人工智能" />
              <el-option label="环保科技" value="环保科技" />
              <el-option label="教育科技" value="教育科技" />
              <el-option label="医疗健康" value="医疗健康" />
              <el-option label="电子商务" value="电子商务" />
              <el-option label="金融科技" value="金融科技" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="项目状态" clearable @change="handleSearch">
              <el-option label="融资中" value="funding" />
              <el-option label="已融资" value="funded" />
              <el-option label="孵化中" value="incubating" />
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon> 搜索
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 项目列表 -->
      <el-table :data="projectList" v-loading="loading" stripe style="width: 100%">
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
        <el-table-column prop="founder" label="创始人" width="120" />
        <el-table-column prop="fundingTarget" label="融资金额" width="120">
          <template #default="{ row }">
            <span class="funding-amount">{{ row.fundingTarget }}万</span>
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="$router.push(`/projects/${row.id}`)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getProjectList } from '@/api/project'

const loading = ref(false)
const projectList = ref([])

const searchForm = reactive({
  keyword: '',
  category: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { funding: 'primary', funded: 'success', incubating: 'warning' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { funding: '融资中', funded: '已融资', incubating: '孵化中' }
  return map[status] || status
}

async function fetchProjects() {
  loading.value = true
  try {
    const res = await getProjectList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    projectList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    // 使用模拟数据
    projectList.value = [
      { id: 1, title: '智能校园助手', category: '人工智能', founder: '张三', fundingTarget: 50, status: 'funding', createdAt: '2024-05-20' },
      { id: 2, title: '绿色循环快递盒', category: '环保科技', founder: '李四', fundingTarget: 100, status: 'funded', createdAt: '2024-05-18' },
      { id: 3, title: 'VR虚拟实验室', category: '教育科技', founder: '王五', fundingTarget: 80, status: 'incubating', createdAt: '2024-05-15' }
    ]
    pagination.total = 3
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.page = 1
  fetchProjects()
}

onMounted(() => {
  fetchProjects()
})
</script>

<style scoped>
.project-list-page {
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

.funding-amount {
  color: #e6a23c;
  font-weight: bold;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
