<template>
  <div class="messages-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span style="font-size: 18px; font-weight: bold">消息中心</span>
          <el-radio-group v-model="tab" @change="fetchData">
            <el-radio-button label="inbox">收件箱</el-radio-button>
            <el-radio-button label="sent">已发送</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table :data="messages" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="发送方" width="120">
          <template #default="{ row }">
            {{ tab === 'inbox' ? row.senderId : '我' }}
          </template>
        </el-table-column>
        <el-table-column label="接收方" width="120">
          <template #default="{ row }">
            {{ tab === 'inbox' ? '我' : row.receiverId }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isRead === 0 || row.isRead === false" type="danger">未读</el-tag>
            <el-tag v-else type="info">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="markAsRead(row)" v-if="tab === 'inbox' && (row.isRead === 0 || row.isRead === false)">
              标记已读
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
import { ElMessage } from 'element-plus'
import { getInbox, getSent, markAsRead as markRead } from '@/api/message'

export default {
  name: 'MessagesPage',
  setup() {
    const messages = ref([])
    const loading = ref(false)
    const total = ref(0)
    const pageNum = ref(1)
    const pageSize = ref(10)
    const tab = ref('inbox')

    const fetchData = async () => {
      loading.value = true
      try {
        const params = { page: pageNum.value, size: pageSize.value }
        const res = tab.value === 'inbox' ? await getInbox(params) : await getSent(params)
        messages.value = res.data?.records || []
        total.value = res.data?.total || 0
      } catch (err) {
        ElMessage.error('获取消息列表失败')
      } finally {
        loading.value = false
      }
    }

    const markAsRead = async (row) => {
      try {
        await markRead(row.id)
        ElMessage.success('已标记为已读')
        fetchData()
      } catch (err) {
        ElMessage.error('操作失败')
      }
    }

    onMounted(fetchData)

    return { messages, loading, total, pageNum, pageSize, tab, fetchData, markAsRead }
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
