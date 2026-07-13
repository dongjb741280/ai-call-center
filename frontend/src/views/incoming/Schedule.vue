<template>
  <div class="page-card">
    <div class="page-header">
      <h2>日程管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="日程名称">
        <el-input v-model="searchForm.name" placeholder="日程名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="时间纬度">
        <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 130px">
          <el-option label="指定时间" :value="1" />
          <el-option label="相对时间" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column label="序号" width="60" align="center">
        <template #default="{ $index }">{{ (pagination.currentPage - 1) * pagination.pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" label="日程名称" min-width="150" show-overflow-tooltip />
      <el-table-column label="日程等级" width="80" align="center">
        <template #default="{ row }">{{ row.levelValue ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="时间类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="workTypeTag(row.workType)">{{ row.workType || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间纬度" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.type === 1 ? 'primary' : 'warning'">
            {{ row.type === 1 ? '指定时间' : row.type === 2 ? '相对时间' : '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始日期" width="120" align="center">
        <template #default="{ row }">{{ row.startDay || '-' }}</template>
      </el-table-column>
      <el-table-column label="结束日期" width="120" align="center">
        <template #default="{ row }">{{ row.endDay || '-' }}</template>
      </el-table-column>
      <el-table-column label="开始时间" width="100" align="center">
        <template #default="{ row }">{{ row.startTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="结束时间" width="100" align="center">
        <template #default="{ row }">{{ row.endTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="周一" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.mon === 1 ? 'success' : 'info'">{{ row.mon === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周二" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.tue === 1 ? 'success' : 'info'">{{ row.tue === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周三" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.wed === 1 ? 'success' : 'info'">{{ row.wed === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周四" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.thu === 1 ? 'success' : 'info'">{{ row.thu === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周五" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.fri === 1 ? 'success' : 'info'">{{ row.fri === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周六" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.sat === 1 ? 'success' : 'info'">{{ row.sat === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="周日" width="65" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.sun === 1 ? 'success' : 'info'">{{ row.sun === 1 ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑日程' : '新增日程'" width="640px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="日程名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入日程名称" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="日程等级" prop="levelValue">
              <el-input-number v-model="form.levelValue" :min="0" :max="99" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="时间纬度">
              <el-radio-group v-model="form.type">
                <el-radio :value="1">指定时间</el-radio>
                <el-radio :value="2">相对时间</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时间类型">
              <el-select v-model="form.workType" placeholder="时间类型" style="width:100%" clearable>
                <el-option label="上班" value="上班" />
                <el-option label="下班" value="下班" />
                <el-option label="周末" value="周末" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0"
                active-text="启用" inactive-text="禁用" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期">
              <el-select v-model="form.startDay" placeholder="开始日期" style="width:100%" clearable>
                <el-option v-for="d in 31" :key="d" :label="d" :value="String(d)" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期">
              <el-select v-model="form.endDay" placeholder="结束日期" style="width:100%" clearable>
                <el-option v-for="d in 31" :key="d" :label="d" :value="String(d)" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间">
              <el-time-picker v-model="form.startTime" placeholder="开始时间"
                value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间">
              <el-time-picker v-model="form.endTime" placeholder="结束时间"
                value-format="HH:mm:ss" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="周一到周日">
          <el-checkbox-group v-model="form.weekdays" class="weekday-group">
            <el-checkbox label="mon">星期一</el-checkbox>
            <el-checkbox label="tue">星期二</el-checkbox>
            <el-checkbox label="wed">星期三</el-checkbox>
            <el-checkbox label="thu">星期四</el-checkbox>
            <el-checkbox label="fri">星期五</el-checkbox>
            <el-checkbox label="sat">星期六</el-checkbox>
            <el-checkbox label="sun">星期日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleList, saveSchedule, updateSchedule, deleteSchedule } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', type: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', levelValue: 0, type: 1, workType: '',
  startDay: '', endDay: '', startTime: '', endTime: '',
  weekdays: [], status: 1
})

const rules = {
  name: [{ required: true, message: '请输入日程名称', trigger: 'blur' }]
}

const weekKeys = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun']
const weekLabels = [
  { key: 'mon', label: '周一' }, { key: 'tue', label: '周二' }, { key: 'wed', label: '周三' },
  { key: 'thu', label: '周四' }, { key: 'fri', label: '周五' }, { key: 'sat', label: '周六' }, { key: 'sun', label: '周日' }
]

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const workTypeTag = (val) => {
  if (val === '上班') return 'primary'
  if (val === '下班') return 'warning'
  if (val === '周末') return 'danger'
  return 'info'
}

const getWeekdays = (row) => {
  return weekLabels.map(w => ({ label: w.label, active: row[w.key] === 1 }))
}

const loadScheduleToForm = (row) => {
  const weekdays = weekKeys.filter(k => row[k] === 1)
  Object.assign(form, {
    id: row.id, name: row.name, levelValue: row.levelValue ?? 0, type: row.type ?? 1,
    workType: row.workType || '',
    startDay: row.startDay || '', endDay: row.endDay || '',
    startTime: row.startTime || '', endTime: row.endTime || '',
    weekdays, status: row.status ?? 1
  })
}

const buildScheduleData = () => {
  const data = {
    name: form.name, levelValue: form.levelValue, type: form.type,
    workType: form.workType || null,
    startDay: form.startDay || null, endDay: form.endDay || null,
    startTime: form.startTime || null, endTime: form.endTime || null,
    status: form.status
  }
  weekKeys.forEach(k => { data[k] = form.weekdays.includes(k) ? 1 : 0 })
  return data
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, name: '', levelValue: 0, type: 1, workType: '',
    startDay: '', endDay: '', startTime: '', endTime: '',
    weekdays: [], status: 1
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({
        name: searchForm.name || undefined,
        type: searchForm.type || undefined
      })
    }
    const res = await getScheduleList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.type = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  loadScheduleToForm(row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const data = buildScheduleData()
    if (isEdit.value) {
      await updateSchedule(form.id, data)
    } else {
      await saveSchedule(data)
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除日程 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}
.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.weekday-group {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 16px;
}
</style>
