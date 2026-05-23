<template>
  <div class="my-consultations-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>我的咨询记录</h2>
          <el-button type="primary" @click="dialogVisible = true">
            <el-icon><Plus /></el-icon> 新建咨询
          </el-button>
        </div>
      </template>

      <el-table :data="consultationList" v-loading="loading" stripe>
        <el-table-column prop="mentorName" label="咨询导师" width="120" />
        <el-table-column prop="projectTitle" label="关联项目" min-width="200" />
        <el-table-column prop="content" label="咨询内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="咨询时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">
              详情
            </el-button>
            <el-button
              v-if="row.status === 'replied'"
              type="success"
              link
              size="small"
              @click="openRate(row)"
            >
              评价
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && consultationList.length === 0" description="暂无咨询记录" />

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

    <!-- 新建咨询对话框 -->
    <el-dialog v-model="dialogVisible" title="新建咨询" width="500px">
      <el-form :model="consultForm" label-width="80px">
        <el-form-item label="咨询导师">
          <el-select v-model="consultForm.mentorId" placeholder="请选择导师" style="width: 100%">
            <el-option
              v-for="m in mentors"
              :key="m.id"
              :label="m.name"
              :value="m.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联项目">
          <el-select v-model="consultForm.projectId" placeholder="请选择关联项目" style="width: 100%">
            <el-option
              v-for="p in myProjects"
              :key="p.id"
              :label="p.title"
              :value="p.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="咨询内容">
          <el-input
            v-model="consultForm.content"
            type="textarea"
            :rows="4"
            placeholder="请描述您想咨询的问题"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConsultation">提交咨询</el-button>
      </template>
    </el-dialog>

    <!-- 咨询详情对话框 -->
    <el-dialog v-model="detailVisible" title="咨询详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="导师">{{ currentItem.mentorName }}</el-descriptions-item>
        <el-descriptions-item label="项目">{{ currentItem.projectTitle }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(currentItem.status)" size="small">
            {{ statusLabel(currentItem.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="咨询内容">{{ currentItem.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentItem.reply" label="导师回复">
          {{ currentItem.reply }}
        </el-descriptions-item>
        <el-descriptions-item label="咨询时间">{{ currentItem.createdAt }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMyConsultations, createConsultation, rateConsultation } from '@/api/consultation'
import { getMyProjects } from '@/api/project'
import { getMentorList } from '@/api/mentor'

const loading = ref(false)
const consultationList = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentItem = ref({})

const consultForm = reactive({
  mentorId: '',
  projectId: '',
  content: ''
})

const mentors = ref([])
const myProjects = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { pending: 'warning', replied: 'success', closed: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { pending: '待回复', replied: '已回复', closed: '已关闭' }
  return map[status] || status
}

function showDetail(row) {
  currentItem.value = row
  detailVisible.value = true
}

function openRate(row) {
  ElMessage.info('评价功能开发中')
}

async function submitConsultation() {
  if (!consultForm.content) {
    ElMessage.warning('请输入咨询内容')
    return
  }
  try {
    await createConsultation(consultForm)
    ElMessage.success('咨询已提交')
    dialogVisible.value = false
    fetchList()
  } catch {
    // error handled
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMyConsultations({ page: pagination.page, pageSize: pagination.pageSize })
    consultationList.value = res.data?.list || res.data || []
    pagination.total = res.data?.total || 0
  } catch {
    consultationList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  fetchList()
  // 加载可选的导师和项目
  try {
    const [mentorRes, projectRes] = await Promise.all([
      getMentorList(),
      getMyProjects()
    ])
    mentors.value = mentorRes.data?.list || mentorRes.data || []
    myProjects.value = projectRes.data?.list || projectRes.data || []
  } catch {
    mentors.value = []
    myProjects.value = []
  }
})
</script>

<style scoped>
.my-consultations-page {
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
