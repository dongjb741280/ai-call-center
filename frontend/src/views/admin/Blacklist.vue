<template>
  <div class="page-card">
    <div class="page-header">
      <h2>黑名单</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="号码">
        <el-input v-model="searchForm.numPrefix" placeholder="输入号码查询" clearable @keyup.enter="handleSearch" />
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
      <el-table-column prop="numPrefix" label="号码" width="180" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="黑名单类型" width="120">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? 'danger' : 'warning'" size="small">{{ row.type === 1 ? '黑名单' : '灰名单' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="呼叫方向" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ directionMap[row.callDirection] || row.callDirection }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑黑名单' : '新增黑名单'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="号码" prop="numPrefix">
          <el-input v-model="form.numPrefix" placeholder="拉黑号码" />
        </el-form-item>
        <el-form-item label="黑名单类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="黑名单" :value="1" />
            <el-option label="灰名单" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="呼叫方向" prop="callDirection">
          <el-select v-model="form.callDirection" style="width: 100%">
            <el-option label="所有" :value="0" />
            <el-option label="呼入" :value="1" />
            <el-option label="外呼" :value="2" />
          </el-select>
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
import { getBlackPhoneList, saveBlackPhone, deleteBlackPhone } from '@/api/config'

const directionMap = { 0: '所有', 1: '呼入', 2: '外呼' }

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ numPrefix: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, numPrefix: '', type: 1, callDirection: 0, status: 2 })

const rules = {
  numPrefix: [{ required: true, message: '请输入号码', trigger: 'blur' }],
  callDirection: [{ required: true, message: '请选择呼叫方向', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, numPrefix: '', type: 1, callDirection: 0, status: 2 })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.numPrefix) query.numPrefix = searchForm.numPrefix
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify(query)
    }
    const res = await getBlackPhoneList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.numPrefix = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, numPrefix: row.numPrefix, type: row.type ?? 1, callDirection: row.callDirection ?? 0, status: row.status ?? 2 })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    await saveBlackPhone({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除号码 "${row.numPrefix}" 吗？`, '提示', { type: 'warning' })
    await deleteBlackPhone(row.id)
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
</style>
