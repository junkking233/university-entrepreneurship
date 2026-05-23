<template>
  <div class="user-management-page">
    <el-card>
      <template #header>
        <h2>用户管理</h2>
      </template>

      <div class="search-bar">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户名/昵称"
              clearable
              @keyup.enter="fetchList"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchRole" placeholder="角色筛选" clearable @change="fetchList">
              <el-option label="创业者" value="student" />
              <el-option label="导师" value="mentor" />
              <el-option label="投资者" value="investor" />
              <el-option label="管理员" value="admin" />
            </el-select>
          </el-col>
        </el-row>
      </div>

      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="roleType(row.role)" size="small">
              {{ roleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 'active' ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && userList.length === 0" description="暂无用户数据" />

      <div class="pagination-wrapper" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus, deleteUser } from '@/api/admin'

const loading = ref(false)
const userList = ref([])
const searchKeyword = ref('')
const searchRole = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function roleType(role) {
  const map = { student: 'primary', mentor: 'success', investor: 'warning', admin: 'danger' }
  return map[role] || 'info'
}

function roleLabel(role) {
  const map = { student: '创业者', mentor: '导师', investor: '投资者', admin: '管理员' }
  return map[role] || role
}

async function toggleStatus(row) {
  const newStatus = row.status === 'active' ? 'disabled' : 'active'
  const action = newStatus === 'active' ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, { status: newStatus })
    ElMessage.success(`${action}成功`)
    fetchList()
  } catch {
    // cancelled
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？此操作不可恢复！', '警告', {
      type: 'warning',
      confirmButtonText: '确定删除',
      confirmButtonClass: 'el-button--danger'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // cancelled
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value,
      role: searchRole.value
    })
    userList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    userList.value = []
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
.user-management-page {
  max-width: 1200px;
  margin: 0 auto;
}

.user-management-page h2 {
  margin: 0;
  font-size: 20px;
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
