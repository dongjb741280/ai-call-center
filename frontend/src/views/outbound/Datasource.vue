<template>
  <div class="page-card">
    <div class="page-header">
      <h2>数据源</h2>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="名称">
        <el-input v-model="searchForm.name" placeholder="数据源名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width:100%">
      <el-table-column prop="id" label="id" width="80" />
      <el-table-column prop="name" label="数据源名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="180">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="名单数量" width="100" align="center">
        <template #default="{ row }">{{ row.listCount ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="字段数量" width="100" align="center">
        <template #default="{ row }">{{ row.fieldCount ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :page-sizes="[10,20,50,100]" :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑数据源' : '新增数据源'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="数据源名称" prop="name">
          <el-input v-model="form.name" placeholder="数据源名称" maxlength="64" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <div class="dialog-section" v-if="isEdit" style="margin-top:16px">
        <div class="dialog-section-title">字段列表</div>
        <el-select v-model="selectedFieldIds" multiple filterable placeholder="请选择字段" style="width:100%">
          <el-option v-for="f in allFields" :key="f.id" :label="f.name" :value="f.id" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="数据源详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="数据源名称">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag size="small" :type="detail.status === 1 ? 'success' : 'info'">{{ detail.status === 1 ? '启用' : '禁用' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(detail.cts) }}</el-descriptions-item>
        <el-descriptions-item label="修改时间">{{ formatTime(detail.uts) }}</el-descriptions-item>
      </el-descriptions>
      <div class="dialog-section" style="margin-top:16px">
        <div class="dialog-section-title">字段列表</div>
        <el-table :data="detailFieldList" size="small" max-height="250">
          <el-table-column label="序号" width="60" align="center">
            <template #default="{ $index }">{{ $index + 1 }}</template>
          </el-table-column>
          <el-table-column prop="name" label="字段名称" min-width="140" />
          <el-table-column label="号码字段" width="90" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="row.isPhone === 1 ? 'success' : 'info'">{{ row.isPhone === 1 ? '是' : '否' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="fieldType" label="字段类型" width="110" align="center" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskSourceList, saveTaskSource, updateTaskSource, deleteTaskSource, getSourceFieldList, getTaskFieldList, updateSourceFields } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, name: '', status: 1 })
const allFields = ref([])
const selectedFieldIds = ref([])
const detailVisible = ref(false)
const detail = reactive({ id: null, name: '', status: 1, cts: null, uts: null })
const detailFieldList = ref([])
const rules = { name: [{ required: true, message: '请输入数据源名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', status: 1 })
  selectedFieldIds.value = []
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify({ name: searchForm.name || undefined }) }
    const res = await getTaskSourceList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()
const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, name: row.name, status: row.status ?? 1 })
  selectedFieldIds.value = []
  allFields.value = []
  try {
    const [allRes, sourceRes] = await Promise.all([
      getTaskFieldList({ pageNum: 1, pageSize: 999 }),
      getSourceFieldList(row.id)
    ])
    allFields.value = allRes.code === 0 ? (allRes.data?.list || []) : []
    const sourceFields = sourceRes.code === 0 ? (sourceRes.data || []) : []
    selectedFieldIds.value = sourceFields.map(f => f.id)
  } catch { /* keep empty */ }
  dialogVisible.value = true
}

const handleDetail = async (row) => {
  Object.assign(detail, { id: row.id, name: row.name, status: row.status ?? 1, cts: row.cts, uts: row.uts })
  detailVisible.value = true
  try {
    const res = await getSourceFieldList(row.id)
    detailFieldList.value = res.code === 0 ? (res.data || []) : []
  } catch { detailFieldList.value = [] }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateTaskSource(form.id, { name: form.name, status: form.status })
      await updateSourceFields(form.id, { fieldIds: selectedFieldIds.value })
    }
    else { await saveTaskSource({ name: form.name, status: form.status }) }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false; loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除数据源 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteTaskSource(row.id)
    ElMessage.success('删除成功'); loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.dialog-section-title { font-size: 14px; font-weight: 600; color: #303133; padding-left: 10px; border-left: 3px solid #409eff; margin-bottom: 12px; line-height: 1.3; }
</style>
