<template>
  <div class="page-card">
    <div class="page-header">
      <h2>号码管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="号码">
        <el-input v-model="searchForm.phone" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="号码类型">
        <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 140px">
          <el-option label="外显号码" value="外显号码" />
          <el-option label="透传" value="透传" />
          <el-option label="随机" value="随机" />
        </el-select>
      </el-form-item>
      <el-form-item label="归属地">
        <el-input v-model="searchForm.location" placeholder="请输入" clearable />
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
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="号码" width="140" />
      <el-table-column label="号码类型" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ row.type || row.phoneType || '外显号码' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="归属地" width="100">
        <template #default="{ row }">{{ row.location || row.province || row.city || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑号码' : '新增号码'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="号码" prop="phone">
          <el-input v-model="form.phone" placeholder="外呼显示号码" />
        </el-form-item>
        <el-form-item label="号码类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="外显号码" value="外显号码" />
            <el-option label="透传" value="透传" />
            <el-option label="随机" value="随机" />
          </el-select>
        </el-form-item>
        <el-form-item label="归属地">
          <el-input v-model="form.location" placeholder="选填" />
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
import { getPhoneList, addPhone, updatePhone, deletePhone } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ phone: '', type: '', location: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, phone: '', type: '外显号码', location: '' })

const rules = { phone: [{ required: true, message: '请输入号码', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, phone: '', type: '外显号码', location: '' })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.phone) query.phone = searchForm.phone
    if (searchForm.type) query.type = searchForm.type
    if (searchForm.location) query.location = searchForm.location
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getPhoneList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.phone = ''; searchForm.type = ''; searchForm.location = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, phone: row.phone || '', type: row.type || '外显号码', location: row.location || row.province || row.city || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updatePhone(form.id, { ...form })
    else await addPhone({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除号码 "${row.phone}" 吗？`, '提示', { type: 'warning' })
    await deletePhone(row.id)
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
