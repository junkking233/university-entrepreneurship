<template>
  <div class="messages-page">
    <el-card class="messages-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h2 class="page-title">消息中心</h2>
            <p class="page-subtitle">{{ tab === 'inbox' ? '查看导师、平台和项目相关通知' : '查看你已发送的消息' }}</p>
          </div>
          <div class="header-actions">
            <el-button
              v-if="tab === 'inbox' && unreadCount > 0"
              type="primary"
              plain
              size="small"
              @click="handleMarkAllRead"
            >
              全部已读
            </el-button>
            <el-radio-group v-model="tab" @change="handleTabChange">
              <el-radio-button label="inbox">收件箱</el-radio-button>
              <el-radio-button label="sent">已发送</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <el-table :data="messages" stripe v-loading="loading" class="message-table">
        <el-table-column label="对方" width="180">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32">{{ getCounterparty(row).slice(0, 1) }}</el-avatar>
              <span>{{ getCounterparty(row) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="内容" min-width="420">
          <template #default="{ row }">
            <div class="message-content" :class="{ unread: isUnread(row) }">
              {{ row.content }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag v-if="isUnread(row)" type="danger" effect="light">未读</el-tag>
            <el-tag v-else type="info" effect="plain">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="markAsRead(row)" v-if="tab === 'inbox' && isUnread(row)">
              标记已读
            </el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty :description="tab === 'inbox' ? '暂无收到的消息' : '暂无已发送消息'" />
        </template>
      </el-table>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        background
        class="pagination"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  deleteMessage,
  getInbox,
  getSent,
  markAllAsRead,
  markAsRead as markRead
} from '@/api/message'

const messages = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const tab = ref('inbox')
const unreadCount = ref(0)

function isUnread(row) {
  return row.isRead === 0 || row.isRead === false
}

function getCounterparty(row) {
  const id = tab.value === 'inbox' ? row.senderId : row.receiverId
  return row.senderName || row.receiverName || (id ? `用户#${id}` : '平台消息')
}

function updateUnreadCount() {
  unreadCount.value = tab.value === 'inbox' ? messages.value.filter(isUnread).length : 0
}

async function fetchData() {
  loading.value = true
  try {
    const params = { page: pageNum.value, size: pageSize.value }
    const res = tab.value === 'inbox' ? await getInbox(params) : await getSent(params)
    messages.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
    updateUnreadCount()
  } catch (err) {
    messages.value = []
    total.value = 0
    unreadCount.value = 0
    ElMessage.error('获取消息列表失败')
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  pageNum.value = 1
  fetchData()
}

async function markAsRead(row) {
  try {
    await markRead(row.id)
    ElMessage.success('已标记为已读')
    fetchData()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

async function handleMarkAllRead() {
  try {
    await markAllAsRead()
    ElMessage.success('已全部标记为已读')
    fetchData()
  } catch (err) {
    ElMessage.error('操作失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除这条消息吗？', '提示', { type: 'warning' })
    await deleteMessage(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {
    // cancelled or handled by interceptor
  }
}

onMounted(fetchData)
</script>

<style scoped>
.messages-page {
  max-width: 1200px;
  margin: 0 auto;
}

.messages-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.page-subtitle {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.message-table {
  width: 100%;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #303133;
}

.message-content {
  color: #606266;
  line-height: 1.6;
  word-break: break-word;
}

.message-content.unread {
  color: #303133;
  font-weight: 600;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .card-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
