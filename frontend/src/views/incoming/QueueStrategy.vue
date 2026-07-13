<template>
  <div class="page-card">
    <div class="page-header">
      <h2>排队策略</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="策略名称">
        <el-input v-model="searchForm.name" placeholder="策略名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="策略类型">
        <el-select v-model="searchForm.handleType" placeholder="全部" clearable style="width: 120px">
          <el-option label="排队" :value="1" />
          <el-option label="溢出" :value="2" />
          <el-option label="挂机" :value="3" />
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
      <el-table-column prop="name" label="策略名称" min-width="160" show-overflow-tooltip />
      <el-table-column label="策略类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="handleTypeTag(row.handleType)">{{ handleTypeMap[row.handleType] || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排队方式" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="busyTypeTag(row.busyType)">{{ busyTypeMap[row.busyType] || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排队超时" width="100" align="center">
        <template #default="{ row }">{{ row.queueTimeout ? row.queueTimeout + 's' : '-' }}</template>
      </el-table-column>
      <el-table-column label="超时处理" width="90" align="center">
        <template #default="{ row }">
          {{ row.busyTimeoutType === 1 ? '溢出' : row.busyTimeoutType === 2 ? '挂机' : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right" align="center">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑排队策略' : '新增排队策略'" width="750px" @close="resetForm" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="策略名称" prop="name">
              <el-input v-model="form.name" placeholder="策略名称" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="策略类型" prop="handleType">
              <el-select v-model="form.handleType" style="width:100%">
                <el-option label="排队" :value="1" />
                <el-option label="溢出" :value="2" />
                <el-option label="挂机" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排队方式">
              <el-select v-model="form.busyType" style="width:100%">
                <el-option label="先进先出" :value="1" />
                <el-option label="VIP" :value="2" />
                <el-option label="自定义" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排队超时(秒)">
              <el-input-number v-model="form.queueTimeout" :min="0" :max="9999" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="超时处理">
              <el-select v-model="form.busyTimeoutType" style="width:100%">
                <el-option label="溢出" :value="1" />
                <el-option label="挂机" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="溢出类型">
              <el-select v-model="form.overflowType" style="width:100%">
                <el-option label="技能组" :value="1" />
                <el-option label="IVR" :value="2" />
                <el-option label="路由子码" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="溢出对象" v-if="form.overflowType">
          <el-input v-model="form.overflowValue" placeholder="溢出对象ID" style="width:200px" />
        </el-form-item>
        <el-form-item label="自定义表达式" v-if="form.busyType === 3">
          <el-input v-model="form.lineupExpression" placeholder="自定义排队表达式" maxlength="128" />
        </el-form-item>
      </el-form>

      <div class="dialog-section">
        <div class="dialog-section-title">
          <span>前置条件</span>
          <span class="dialog-section-hint">满足以下所有条件</span>
          <el-button type="primary" size="small" @click="handleEditAddExp">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="editExpList" size="small" max-height="250">
          <el-table-column label="序号" width="50" align="center">
            <template #default="{ $index }">{{ $index + 1 }}</template>
          </el-table-column>
          <el-table-column label="条件类型" min-width="160">
            <template #default="{ row }">
              <el-input v-model="row.expKey" placeholder="条件类型" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="条件范围" width="100" align="center">
            <template #default>等于</template>
          </el-table-column>
          <el-table-column label="条件范围值" width="130" align="center">
            <template #default="{ row }">
              <el-input-number v-model="row.rate" :min="0" size="small" style="width:100%" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row, $index }">
              <el-button type="danger" size="small" @click="editExpList.splice($index, 1)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 详情弹窗 ==================== -->
    <el-dialog v-model="detailVisible" title="排队策略详情" width="800px" @close="detailVisible = false">
      <template v-if="detail.id">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="id">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="策略名称">{{ detail.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="策略类型">
            <el-tag size="small" :type="handleTypeTag(detail.handleType)">{{ handleTypeMap[detail.handleType] || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排队方式">
            <el-tag size="small" :type="busyTypeTag(detail.busyType)">{{ busyTypeMap[detail.busyType] || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="排队超时">{{ detail.queueTimeout ? detail.queueTimeout + 's' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="排队超时处理">
            {{ detail.busyTimeoutType === 1 ? '溢出' : detail.busyTimeoutType === 2 ? '挂机' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="溢出策略">
            {{ detail.overflowType === 1 ? '技能组' : detail.overflowType === 2 ? 'IVR' : detail.overflowType === 3 ? '路由子码' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="溢出对象">{{ detail.overflowValue ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="自定义表达式">{{ detail.lineupExpression || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="dialog-section">
          <div class="dialog-section-title">
            <span>前置条件</span>
            <el-button type="primary" size="small" @click="handleAddExp">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
          </div>
          <el-table :data="expList" size="small" max-height="300">
            <el-table-column label="序号" width="50" align="center">
              <template #default="{ $index }">{{ $index + 1 }}</template>
            </el-table-column>
            <el-table-column label="条件类型" min-width="150">
              <template #default="{ row }">
                <el-input v-if="row._editing" v-model="row.expKey" placeholder="条件类型" size="small" />
                <span v-else>{{ row.expKey || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="条件范围" width="100" align="center">
              <template #default="{ row }">
                <span v-if="row._editing">-</span>
                <span v-else>等于</span>
              </template>
            </el-table-column>
            <el-table-column label="起始值" width="90" align="center">
              <template #default="{ row }">
                <span v-if="row._editing">-</span>
                <span v-else>0</span>
              </template>
            </el-table-column>
            <el-table-column label="条件范围值" width="120" align="center">
              <template #default="{ row }">
                <el-input-number v-if="row._editing" v-model="row.rate" :min="0" size="small" style="width:100%" />
                <span v-else>{{ row.rate ?? '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right" align="center">
              <template #default="{ row }">
                <template v-if="row._editing">
                  <el-button type="primary" size="small" @click="handleSaveExp(row)">保存</el-button>
                  <el-button size="small" @click="handleCancelExp(row)">取消</el-button>
                </template>
                <template v-else>
                  <el-button type="primary" size="small" @click="handleEditExp(row)">编辑</el-button>
                  <el-button type="danger" size="small" @click="handleDeleteExp(row)">删除</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOverflowList, saveOverflow, updateOverflow, deleteOverflow,
  getOverflowExpList, saveOverflowExp, updateOverflowExp, deleteOverflowExp } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', handleType: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', handleType: 1, busyType: 1,
  queueTimeout: 60, busyTimeoutType: 2, overflowType: 1,
  overflowValue: null, lineupExpression: ''
})
const editExpList = ref([])

const handleTypeMap = { 1: '排队', 2: '溢出', 3: '挂机' }
const busyTypeMap = { 1: '先进先出', 2: 'VIP', 3: '自定义' }

const handleTypeTag = (v) => v === 1 ? 'primary' : v === 2 ? 'warning' : 'danger'
const busyTypeTag = (v) => v === 1 ? 'success' : v === 2 ? 'warning' : 'info'

const rules = {
  name: [{ required: true, message: '请输入策略名称', trigger: 'blur' }],
  handleType: [{ required: true, message: '请选择策略类型', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, name: '', handleType: 1, busyType: 1,
    queueTimeout: 60, busyTimeoutType: 2, overflowType: 1,
    overflowValue: null, lineupExpression: ''
  })
  editExpList.value = []
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({
        name: searchForm.name || undefined,
        handleType: searchForm.handleType || undefined
      })
    }
    const res = await getOverflowList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.handleType = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, name: row.name, handleType: row.handleType ?? 1,
    busyType: row.busyType ?? 1, queueTimeout: row.queueTimeout ?? 60,
    busyTimeoutType: row.busyTimeoutType ?? 2, overflowType: row.overflowType ?? 1,
    overflowValue: row.overflowValue, lineupExpression: row.lineupExpression || ''
  })
  try {
    const res = await getOverflowExpList(row.id)
    editExpList.value = (res.code === 0 ? (res.data || []) : [])
      .map(e => ({ id: e.id, expKey: e.expKey, rate: e.rate, status: e.status }))
  } catch { editExpList.value = [] }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const data = {
      name: form.name, handleType: form.handleType, busyType: form.busyType,
      queueTimeout: form.queueTimeout, busyTimeoutType: form.busyTimeoutType,
      overflowType: form.overflowType, overflowValue: form.overflowValue,
      lineupExpression: form.lineupExpression || null
    }
    let overflowId = form.id
    if (isEdit.value) {
      await updateOverflow(overflowId, data)
    } else {
      const res = await saveOverflow(data)
      await loadData()
      // find the newly created overflow by checking the latest list
      overflowId = list.value[0]?.id
    }
    // sync preconditions: save new, update existing, delete removed
    if (overflowId) {
      const origRes = await getOverflowExpList(overflowId)
      const origIds = (origRes.code === 0 ? (origRes.data || []) : []).map(e => e.id)
      const keepIds = editExpList.value.filter(e => e.id).map(e => e.id)
      const removeIds = origIds.filter(id => !keepIds.includes(id))
      for (const exp of editExpList.value) {
        const payload = { expKey: exp.expKey, rate: exp.rate, status: exp.status ?? 1 }
        if (exp.id) {
          await updateOverflowExp(exp.id, payload)
        } else {
          await saveOverflowExp(overflowId, payload)
        }
      }
      for (const id of removeIds) {
        await deleteOverflowExp(id)
      }
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除排队策略 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteOverflow(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

// ==================== 详情弹窗 ====================
const detailVisible = ref(false)
const detail = ref({})
const expList = ref([])

const handleDetail = async (row) => {
  detailVisible.value = true
  detail.value = row
  await loadExpList()
}

const handleEditAddExp = () => {
  editExpList.value.push({ id: null, expKey: '', rate: 0, status: 1 })
}

const loadExpList = async () => {
  try {
    const res = await getOverflowExpList(detail.value.id)
    if (res.code === 0) {
      expList.value = (res.data || []).map(e => ({ ...e, _editing: false, _orig: null }))
    }
  } catch { expList.value = [] }
}

const handleAddExp = () => {
  expList.value.unshift({
    id: null, overflowId: detail.value.id, expKey: '', rate: 0, status: 1,
    _editing: true, _orig: null
  })
}

const handleEditExp = (row) => { row._editing = true; row._orig = { ...row } }

const handleCancelExp = (row) => {
  if (row._orig) { Object.assign(row, row._orig); row._editing = false }
  else { const i = expList.value.indexOf(row); if (i > -1) expList.value.splice(i, 1) }
}

const handleSaveExp = async (row) => {
  try {
    if (row.id) {
      await updateOverflowExp(row.id, { expKey: row.expKey, rate: row.rate, status: row.status })
    } else {
      await saveOverflowExp(detail.value.id, { expKey: row.expKey, rate: row.rate, status: row.status })
    }
    ElMessage.success('保存成功')
    await loadExpList()
  } catch { ElMessage.error('保存失败') }
}

const handleDeleteExp = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该前置条件吗？', '提示', { type: 'warning' })
    await deleteOverflowExp(row.id)
    ElMessage.success('删除成功')
    await loadExpList()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.dialog-section { margin-top: 20px; }
.dialog-section-title { display: flex; justify-content: space-between; align-items: center; font-size: 14px; font-weight: 600; color: #303133; padding-left: 10px; border-left: 3px solid #409eff; margin-bottom: 14px; line-height: 1.3; }
.dialog-section-hint { font-size: 12px; font-weight: 400; color: #909399; margin-left: 8px; }
</style>
