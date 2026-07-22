<template>
  <div class="page-card">
    <div class="page-header">
      <h2>路由子码</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="名称">
        <el-input v-model="searchForm.name" placeholder="子码名称" clearable @keyup.enter="handleSearch" />
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
      <el-table-column prop="name" label="子码名称" min-width="140" show-overflow-tooltip />
      <el-table-column label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="子码日程个数" min-width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.vdnSchedulePoList?.length || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="生效的子码日程" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          <template v-if="row.vdnSchedulePoList && row.vdnSchedulePoList.length">
            <div v-for="item in row.vdnSchedulePoList" :key="item.id" class="schedule-item">
              <el-tag size="small" :type="item.effectiveSchedule ? 'success' : 'info'">
                {{ item.scheduleConfig?.name || item.name || '-' }}
              </el-tag>
            </div>
          </template>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
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

    <!-- ==================== 新增/编辑基础信息弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑路由子码' : '新增路由子码'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="子码名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入子码名称" maxlength="32" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 详情弹窗 ==================== -->
    <el-dialog v-model="detailVisible" title="路由子码详情" width="900px" @close="detailVisible = false">
      <template v-if="detail.id">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="子码名称">{{ detail.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detail.status === 1 ? 'success' : 'danger'" size="small">
              {{ detail.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="日程个数">
            <el-tag size="small" type="info">{{ detail.vdnSchedulePoList?.length || 0 }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(detail.cts) }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ formatTime(detail.uts) }}</el-descriptions-item>
          <el-descriptions-item label="生效日程">
            <template v-if="effectiveSchedule">
              <el-tag size="small" type="success">{{ effectiveSchedule.scheduleConfig?.name || '-' }}</el-tag>
            </template>
            <span v-else class="text-muted">-</span>
          </el-descriptions-item>
        </el-descriptions>

        <div class="dialog-section">
          <div class="dialog-section-title">子码日程列表</div>
          <el-form :model="detailSearch" inline class="dialog-search-form">
            <el-form-item label="日程名称">
              <el-input v-model="detailSearch.scheduleName" placeholder="日程名称" clearable size="small" />
            </el-form-item>
            <el-form-item label="路由类型">
              <el-select v-model="detailSearch.routeType" placeholder="全部" clearable size="small" style="width:120px">
                <el-option label="技能组" :value="1" />
                <el-option label="放音" :value="2" />
                <el-option label="IVR" :value="3" />
                <el-option label="坐席" :value="4" />
                <el-option label="外呼" :value="5" />
              </el-select>
            </el-form-item>
          </el-form>

          <el-table :data="detailFilteredList" size="small" max-height="300">
            <el-table-column label="序号" width="50" align="center">
              <template #default="{ $index }">{{ $index + 1 }}</template>
            </el-table-column>
            <el-table-column label="日程名称" min-width="130" show-overflow-tooltip>
              <template #default="{ row }">{{ row.scheduleConfig?.name || row.name || '-' }}</template>
            </el-table-column>
            <el-table-column label="优先级" width="70" align="center">
              <template #default="{ row }">{{ row.scheduleConfig?.levelValue ?? '-' }}</template>
            </el-table-column>
            <el-table-column label="路由类型" width="90" align="center">
              <template #default="{ row }">{{ routeTypeMap[row.routeType] || '-' }}</template>
            </el-table-column>
            <el-table-column label="路由值" min-width="120" show-overflow-tooltip>
              <template #default="{ row }">{{ row.routeValue || '-' }}</template>
            </el-table-column>
            <el-table-column label="时间范围" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">
                <template v-if="row.scheduleConfig">
                  <span>{{ row.scheduleConfig.startDay || '-' }} ~ {{ row.scheduleConfig.endDay || '-' }}</span>
                  <br />
                  <span class="text-secondary">{{ row.scheduleConfig.startTime || '00:00:00' }} ~ {{ row.scheduleConfig.endTime || '23:59:59' }}</span>
                </template>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
            <el-table-column label="有效周期" min-width="140">
              <template #default="{ row }">
                <template v-if="row.scheduleConfig">
                  <el-tag v-for="w in getWeekdays(row.scheduleConfig)" :key="w.label"
                    size="small" :type="w.active ? 'success' : 'info'" style="margin:1px">{{ w.label }}</el-tag>
                </template>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 编辑弹窗 ==================== -->
    <el-dialog v-model="editVisible" title="编辑路由子码" width="960px" @close="editVisible = false" :close-on-click-modal="false">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="100px" class="edit-basic-form">
        <el-form-item label="子码名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入子码名称" maxlength="32" style="width:300px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>

      <div class="dialog-section">
        <div class="dialog-section-title">
          <span>子码日程配置</span>
          <el-button type="primary" size="small" @click="handleAddConfigRow">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>

        <el-table :data="editConfigList" size="small" max-height="350">
          <el-table-column label="序号" width="50" align="center">
            <template #default="{ $index }">{{ $index + 1 }}</template>
          </el-table-column>
          <el-table-column label="配置名称" min-width="120">
            <template #default="{ row }">
              <el-input v-if="row._editing" v-model="row.name" placeholder="配置名称" size="small" />
              <span v-else>{{ row.name || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="关联日程" min-width="120">
            <template #default="{ row }">
              <template v-if="row._editing">
                <el-select v-model="row.scheduleId" placeholder="选择日程" size="small" style="width:100%">
                  <el-option v-for="s in scheduleOptions" :key="s.id" :label="s.name" :value="s.id" />
                </el-select>
              </template>
              <span v-else>{{ row.scheduleConfig?.name || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="路由类型" width="100" align="center">
            <template #default="{ row }">
              <template v-if="row._editing">
                <el-select v-model="row.routeType" placeholder="类型" size="small" style="width:100%">
                  <el-option v-for="item in routeTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </template>
              <span v-else>{{ routeTypeMap[row.routeType] || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="路由值" min-width="120">
            <template #default="{ row }">
              <el-input v-if="row._editing" v-model="row.routeValue" placeholder="路由值" size="small" />
              <span v-else>{{ row.routeValue || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="放音类型" width="100" align="center">
            <template #default="{ row }">
              <template v-if="row._editing">
                <el-select v-model="row.playType" placeholder="类型" size="small" style="width:100%">
                  <el-option v-for="item in playTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </template>
              <span v-else>{{ playTypeMap[row.playType] || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="放音值" width="90" align="center">
            <template #default="{ row }">
              <el-input v-if="row._editing" v-model="row.playValue" placeholder="值" size="small" />
              <span v-else>{{ row.playValue || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="结束音" width="80" align="center">
            <template #default="{ row }">
              <el-input v-if="row._editing" v-model="row.dtmfEnd" placeholder="#" size="small" />
              <span v-else>{{ row.dtmfEnd || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="70" align="center">
            <template #default="{ row }">
              <template v-if="row._editing">
                <el-switch v-model="row.status" :active-value="1" :inactive-value="0" size="small" />
              </template>
              <el-tag v-else :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right" align="center">
            <template #default="{ row }">
              <template v-if="row._editing">
                <el-button type="primary" size="small" @click="handleSaveConfigRow(row)">保存</el-button>
                <el-button size="small" @click="handleCancelConfigRow(row)">取消</el-button>
              </template>
              <template v-else>
                <el-button type="primary" size="small" @click="handleEditConfigRow(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeleteConfigRow(row)">删除</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editSubmitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVdnList, saveVdn, updateVdn, deleteVdn, getVdnDetail,
  saveVdnConfig, updateVdnConfig, deleteVdnConfig } from '@/api/config'

// ==================== 列表数据 ====================
const loading = ref(false)
const list = ref([])
const searchForm = reactive({ name: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const routeTypeMap = { 1: '技能组', 2: '放音', 3: 'IVR', 4: '坐席', 5: '外呼' }
const routeTypeOptions = [
  { value: 1, label: '技能组' }, { value: 2, label: '放音' },
  { value: 3, label: 'IVR' }, { value: 4, label: '坐席' }, { value: 5, label: '外呼' }
]
const playTypeMap = { 1: '按键导航', 2: '技能组', 3: 'IVR', 4: '路由字码', 5: '挂机' }
const playTypeOptions = [
  { value: 1, label: '按键导航' }, { value: 2, label: '技能组' },
  { value: 3, label: 'IVR' }, { value: 4, label: '路由字码' }, { value: 5, label: '挂机' }
]
const weekLabels = [
  { key: 'mon', label: '一' }, { key: 'tue', label: '二' }, { key: 'wed', label: '三' },
  { key: 'thu', label: '四' }, { key: 'fri', label: '五' }, { key: 'sat', label: '六' }, { key: 'sun', label: '日' }
]
const scheduleOptions = ref([])

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}
const getWeekdays = (schedule) => {
  return weekLabels.map(w => ({ label: w.label, active: schedule[w.key] === 1 }))
}

// ==================== 新增弹窗 ====================
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitLoading = ref(false)
const form = reactive({ id: null, name: '', status: 1 })
const rules = { name: [{ required: true, message: '请输入子码名称', trigger: 'blur' }] }

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', status: 1 })
}

// ==================== 详情弹窗 ====================
const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref({})
const detailSearch = reactive({ scheduleName: '', routeType: '' })

const effectiveSchedule = computed(() => {
  if (!detail.value.vdnSchedulePoList?.length) return null
  return detail.value.vdnSchedulePoList.find(s => s.effectiveSchedule) || null
})

const detailFilteredList = computed(() => {
  let list = detail.value.vdnSchedulePoList || []
  if (detailSearch.scheduleName) {
    const kw = detailSearch.scheduleName.toLowerCase()
    list = list.filter(item => {
      const name = item.scheduleConfig?.name || item.name || ''
      return name.toLowerCase().includes(kw)
    })
  }
  if (detailSearch.routeType) {
    list = list.filter(item => item.routeType === detailSearch.routeType)
  }
  return list
})

// ==================== 编辑弹窗 ====================
const editVisible = ref(false)
const editFormRef = ref()
const editSubmitLoading = ref(false)
const editForm = reactive({ name: '', status: 1 })
const editConfigList = ref([])
const editRules = { name: [{ required: true, message: '请输入子码名称', trigger: 'blur' }] }

// ==================== 列表方法 ====================
const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({ name: searchForm.name || undefined })
    }
    const res = await getVdnList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateVdn(form.id, { name: form.name, status: form.status })
    } else {
      await saveVdn({ name: form.name, status: form.status })
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除路由子码 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteVdn(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

// ==================== 详情弹窗方法 ====================
const handleDetail = async (row) => {
  detailVisible.value = true
  detail.value = {}
  detailSearch.scheduleName = ''
  detailSearch.routeType = ''
  detailLoading.value = true
  try {
    const res = await getVdnDetail(row.id)
    if (res.code === 0) {
      detail.value = res.data || {}
    }
  } catch { /* empty */ }
  finally { detailLoading.value = false }
}

// ==================== 编辑弹窗方法 ====================
const handleEdit = async (row) => {
  editVisible.value = true
  editForm.name = ''
  editForm.status = 1
  editConfigList.value = []
  editSubmitLoading.value = false
  try {
    const res = await getVdnDetail(row.id)
    if (res.code === 0) {
      const data = res.data || {}
      editForm.name = data.name || ''
      editForm.status = data.status ?? 1
      editForm._id = data.id
      editConfigList.value = (data.vdnSchedulePoList || []).map(item => ({
        ...item, _editing: false, _orig: { ...item }
      }))
    }
  } catch { /* empty */ }
}

const handleAddConfigRow = () => {
  editConfigList.value.unshift({
    id: null, name: '', vdnId: editForm._id, scheduleId: null,
    routeType: 1, routeValue: '', playType: null, playValue: '',
    dtmfEnd: '', retry: 1, dtmfMax: 1, dtmfMin: 1, status: 1,
    scheduleConfig: null, dtmfList: [],
    _editing: true, _orig: null
  })
}

const handleEditConfigRow = (row) => {
  row._editing = true
  row._orig = { ...row }
}

const handleCancelConfigRow = (row) => {
  if (row._orig && row._orig.id) {
    Object.assign(row, row._orig)
    row._editing = false
  } else {
    const idx = editConfigList.value.indexOf(row)
    if (idx > -1) editConfigList.value.splice(idx, 1)
  }
}

const handleSaveConfigRow = async (row) => {
  try {
    if (row.id) {
      await updateVdnConfig(row.id, {
        name: row.name, scheduleId: row.scheduleId, routeType: row.routeType,
        routeValue: row.routeValue, playType: row.playType, playValue: row.playValue,
        dtmfEnd: row.dtmfEnd, retry: row.retry, dtmfMax: row.dtmfMax,
        dtmfMin: row.dtmfMin, status: row.status
      })
    } else {
      await saveVdnConfig(row.vdnId, {
        name: row.name, scheduleId: row.scheduleId, routeType: row.routeType,
        routeValue: row.routeValue, playType: row.playType, playValue: row.playValue,
        dtmfEnd: row.dtmfEnd, retry: row.retry, dtmfMax: row.dtmfMax,
        dtmfMin: row.dtmfMin, status: row.status
      })
    }
    ElMessage.success('保存成功')
    await refreshEditConfig()
  } catch { ElMessage.error('保存失败') }
}

const handleDeleteConfigRow = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该子码配置吗？', '提示', { type: 'warning' })
    await deleteVdnConfig(row.id)
    ElMessage.success('删除成功')
    await refreshEditConfig()
  } catch { /* cancelled */ }
}

const refreshEditConfig = async () => {
  try {
    const res = await getVdnDetail(editForm._id)
    if (res.code === 0) {
      const data = res.data || {}
      editConfigList.value = (data.vdnSchedulePoList || []).map(item => ({
        ...item, _editing: false, _orig: { ...item }
      }))
    }
  } catch { /* empty */ }
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  try { await editFormRef.value.validate() } catch { return }
  editSubmitLoading.value = true
  try {
    await updateVdn(editForm._id, { name: editForm.name, status: editForm.status })
    ElMessage.success('保存成功')
    editVisible.value = false
    loadData()
  } catch { ElMessage.error('保存失败') }
  finally { editSubmitLoading.value = false }
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
.schedule-item {
  margin-bottom: 2px;
}
.schedule-item:last-child {
  margin-bottom: 0;
}
.text-muted {
  color: #c0c4cc;
}
.text-secondary {
  color: #909399;
  font-size: 12px;
}
.dialog-section {
  margin-top: 20px;
}
.dialog-section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-left: 10px;
  border-left: 3px solid #409eff;
  margin-bottom: 14px;
  line-height: 1.3;
}
.dialog-search-form {
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 14px;
}
.edit-basic-form {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}
</style>
