<template>
  <div class="mentor-trainings-page">
    <el-card>
      <template #header>
        <div class="page-header">
          <h2>培训管理</h2>
          <el-button type="primary" @click="openCreate">
            <el-icon><Plus /></el-icon> 创建培训
          </el-button>
        </div>
      </template>

      <el-table :data="trainingList" v-loading="loading" stripe>
        <el-table-column prop="title" label="活动名称" min-width="200" />
        <el-table-column prop="startTime" label="活动时间" width="180" />
        <el-table-column prop="location" label="地点" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报名" width="80">
          <template #default="{ row }">
            {{ row.currentParticipants || 0 }}/{{ row.maxParticipants || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && trainingList.length === 0" description="暂无培训活动" />

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

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑培训' : '创建培训'"
      width="600px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择活动时间"
            style="width: 100%"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="人数上限" prop="maxParticipants">
          <el-input-number v-model="form.maxParticipants" :min="1" :max="1000" style="width: 200px" />
        </el-form-item>
        <el-form-item label="活动介绍" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入活动介绍"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          {{ isEdit ? '保存修改' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getMentorTrainings, createTraining, updateTraining, deleteTraining } from '@/api/mentor'

const loading = ref(false)
const saving = ref(false)
const trainingList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const form = reactive({
  title: '',
  startTime: '',
  location: '',
  maxParticipants: 50,
  description: ''
})

const rules = {
  title: [{ required: true, message: '请输入活动名称', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择活动时间', trigger: 'change' }],
  location: [{ required: true, message: '请输入活动地点', trigger: 'blur' }]
}

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

function statusType(status) {
  const map = { upcoming: 'primary', ongoing: 'success', completed: 'info' }
  return map[status] || 'info'
}

function statusLabel(status) {
  const map = { upcoming: '即将开始', ongoing: '进行中', completed: '已结束' }
  return map[status] || status
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  form.title = ''
  form.startTime = ''
  form.location = ''
  form.maxParticipants = 50
  form.description = ''
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editId.value = row.id
  form.title = row.title
  form.startTime = row.startTime
  form.location = row.location
  form.maxParticipants = row.maxParticipants || 50
  form.description = row.description || ''
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该培训活动吗？', '提示', { type: 'warning' })
    await deleteTraining(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // cancelled
  }
}

async function handleSave() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (isEdit.value) {
      await updateTraining(editId.value, { ...form, instructor: '创业指导' })
      ElMessage.success('更新成功')
    } else {
      await createTraining({ ...form, instructor: '创业指导', status: 'upcoming' })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch {
    // error handled
  } finally {
    saving.value = false
  }
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getMentorTrainings({ page: pagination.page, pageSize: pagination.pageSize })
    trainingList.value = res.data?.records || res.data?.list || res.data || []
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
.mentor-trainings-page {
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
