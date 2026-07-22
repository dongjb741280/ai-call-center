<template>
  <div class="page-card">
    <div class="page-header">
      <h2>模块站点</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="模块类型">
        <el-select v-model="searchForm.appType" placeholder="请选择" clearable style="width: 160px">
          <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="所在组">
        <el-input v-model="searchForm.appGroup" placeholder="请输入" clearable />
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
      <el-table-column label="模块名称" min-width="120">
        <template #default="{ row }">{{ row.appName || typeMap[row.applicationId || row.appType] || '' }}</template>
      </el-table-column>
      <el-table-column label="模块类型" min-width="90">
        <template #default="{ row }">{{ typeMap[row.applicationId || row.appType] || '' }}</template>
      </el-table-column>
      <el-table-column prop="applicationGroup" label="所在组" min-width="110">
        <template #default="{ row }">{{ row.applicationGroup || row.appGroup || '' }}</template>
      </el-table-column>
      <el-table-column label="操作时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="地址" min-width="140">
        <template #default="{ row }">{{ row.applicationHost || row.appHost || '' }}</template>
      </el-table-column>
      <el-table-column label="端口" min-width="80" align="center">
        <template #default="{ row }">{{ row.applicationPort || row.appPort || '' }}</template>
      </el-table-column>
      <el-table-column label="用户名" min-width="90">
        <template #default="{ row }">{{ row.username || '-' }}</template>
      </el-table-column>
      <el-table-column label="密码" min-width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ row.pwd || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 2 ? 'success' : 'danger'" size="small">
            {{ row.status === 2 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑模块站点' : '新增模块站点'" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模块名称" prop="appName">
          <el-input v-model="form.appName" placeholder="模块名称" />
        </el-form-item>
        <el-form-item label="模块类型" prop="applicationId">
          <el-select v-model="form.applicationId" style="width: 100%">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所在组" prop="applicationGroup">
          <el-input v-model="form.applicationGroup" placeholder="如：cc-a" />
        </el-form-item>
        <el-form-item label="地址" prop="applicationHost">
          <el-input v-model="form.applicationHost" placeholder="主机地址" />
        </el-form-item>
        <el-form-item label="端口" prop="applicationPort">
          <el-input v-model.number="form.applicationPort" placeholder="端口号" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="选填" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.pwd" placeholder="选填" show-password />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="2" :inactive-value="0" active-text="启用" inactive-text="禁用" />
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
import { getStationList, saveStation, deleteStation } from '@/api/config'

const typeOptions = [
  { label: 'cc-api', value: 1 },
  { label: 'fs-api', value: 2 },
  { label: 'cc-ivr', value: 3 },
  { label: 'fs-media', value: 4 }
]
const typeMap = { 1: 'cc-api', 2: 'fs-api', 3: 'cc-ivr', 4: 'fs-media' }

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ appType: '', appGroup: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, appName: '', applicationId: null, applicationGroup: '',
  applicationHost: '', applicationPort: null, username: '', pwd: '', status: 2
})

const rules = {
  applicationId: [{ required: true, message: '请选择模块类型', trigger: 'change' }],
  applicationGroup: [{ required: true, message: '请输入所在组', trigger: 'blur' }],
  applicationHost: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  applicationPort: [{ required: true, message: '请输入端口', trigger: 'blur' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, appName: '', applicationId: null, applicationGroup: '', applicationHost: '', applicationPort: null, username: '', pwd: '', status: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.appType) query.applicationId = searchForm.appType
    if (searchForm.appGroup) query.applicationGroup = searchForm.appGroup
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getStationList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.appType = ''; searchForm.appGroup = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, appName: row.appName || '', applicationId: row.applicationId || row.appType || null,
    applicationGroup: row.applicationGroup || row.appGroup || '',
    applicationHost: row.applicationHost || row.appHost || '',
    applicationPort: row.applicationPort || row.appPort || null,
    username: row.username || '', pwd: row.pwd || '', status: row.status ?? 2
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    await saveStation({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该模块站点吗？', '提示', { type: 'warning' })
    await deleteStation(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
