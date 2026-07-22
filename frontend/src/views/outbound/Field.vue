<template>
  <div class="page-card">
    <div class="page-header">
      <h2>字段管理</h2>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增</el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="字段名称">
        <el-input v-model="searchForm.name" placeholder="字段名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="字段类型">
        <el-select v-model="searchForm.fieldType" placeholder="全部" clearable style="width:130px">
          <el-option label="系统字段" value="系统字段" />
          <el-option label="自定义字段" value="自定义字段" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width:100%">
      <el-table-column label="序号" width="60" align="center">
        <template #default="{ $index }">{{ (pagination.currentPage-1)*pagination.pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" label="字段名称" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="号码字段" min-width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.isPhone === 1 ? 'success' : 'info'">
            {{ row.isPhone === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="字段类型" min-width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.fieldType === '系统字段' ? 'warning' : 'primary'">
            {{ row.fieldType || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字段' : '新增字段'" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="字段名称" prop="name">
          <el-input v-model="form.name" placeholder="字段名称" maxlength="64" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="号码字段">
              <el-switch v-model="form.isPhone" :active-value="1" :inactive-value="0" active-text="是" inactive-text="否" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字段类型">
              <el-select v-model="form.fieldType" style="width:100%">
                <el-option label="自定义字段" value="自定义字段" />
                <el-option label="系统字段" value="系统字段" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getTaskFieldList, saveTaskField, updateTaskField, deleteTaskField } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', fieldType: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, name: '', isPhone: 1, fieldType: '自定义字段' })
const rules = { name: [{ required: true, message: '请输入字段名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', isPhone: 1, fieldType: '自定义字段' })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify({ name: searchForm.name || undefined, fieldType: searchForm.fieldType || undefined }) }
    const res = await getTaskFieldList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.fieldType = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()
const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, name: row.name, isPhone: row.isPhone ?? 1, fieldType: row.fieldType || '自定义字段' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const data = { name: form.name, isPhone: form.isPhone, fieldType: form.fieldType }
    isEdit.value ? await updateTaskField(form.id, data) : await saveTaskField(data)
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false; loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除字段 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteTaskField(row.id)
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
</style>
