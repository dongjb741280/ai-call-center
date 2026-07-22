<template>
  <div class="page-card">
    <div class="page-header">
      <h2>接入号码</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="接入号">
        <el-input v-model="searchForm.phone" placeholder="接入号码" clearable @keyup.enter="handleSearch" />
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
      <el-table-column prop="phone" label="接入号" min-width="150" show-overflow-tooltip />
      <el-table-column label="路由子码" min-width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ getVdnName(row.vdnId) }}</template>
      </el-table-column>
      <el-table-column label="主叫前缀去除" min-width="110" align="center">
        <template #default="{ row }">{{ row.callerPrefix || '-' }}</template>
      </el-table-column>
      <el-table-column label="被叫前缀去除" min-width="110" align="center">
        <template #default="{ row }">{{ row.calledPrefix || '-' }}</template>
      </el-table-column>
      <el-table-column label="被叫后缀去除" min-width="110" align="center">
        <template #default="{ row }">{{ row.calledSuffix || '-' }}</template>
      </el-table-column>
      <el-table-column label="ringing" min-width="90" align="center">
        <template #default="{ row }">{{ row.ringing || '不开启' }}</template>
      </el-table-column>
      <el-table-column label="状态" min-width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" min-width="160">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" min-width="160">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑接入号码' : '新增接入号码'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接入号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入接入号码" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="路由子码" prop="vdnId">
              <el-select v-model="form.vdnId" placeholder="选择路由子码" style="width:100%">
                <el-option v-for="v in vdnOptions" :key="v.id" :label="v.name" :value="v.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主叫前缀去除">
              <el-input v-model="form.callerPrefix" placeholder="主叫前缀去除" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="被叫前缀去除">
              <el-input v-model="form.calledPrefix" placeholder="被叫前缀去除" maxlength="32" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="被叫后缀去除">
              <el-input v-model="form.calledSuffix" placeholder="被叫后缀去除" maxlength="32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="ringing">
              <el-input v-model="form.ringing" placeholder="ringing" maxlength="32" />
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
import { getVdnPhoneList, saveVdnPhone, updateVdnPhone, deleteVdnPhone } from '@/api/config'
import { getVdnList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const vdnOptions = ref([])
const searchForm = reactive({ phone: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, phone: '', vdnId: null,
  callerPrefix: '', calledPrefix: '', calledSuffix: '',
  ringing: '', status: 1
})

const rules = {
  phone: [{ required: true, message: '请输入接入号码', trigger: 'blur' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const getVdnName = (vdnId) => {
  if (!vdnId) return '-'
  const v = vdnOptions.value.find(o => o.id === vdnId)
  return v ? v.name : vdnId
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, phone: '', vdnId: null,
    callerPrefix: '', calledPrefix: '', calledSuffix: '',
    ringing: '', status: 1
  })
}

const loadVdnOptions = async () => {
  try {
    const res = await getVdnList({ pageNum: 1, pageSize: 200 })
    if (res.code === 0) vdnOptions.value = res.data?.list || []
  } catch { /* empty */ }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({ phone: searchForm.phone || undefined })
    }
    const res = await getVdnPhoneList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.phone = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, phone: row.phone, vdnId: row.vdnId,
    callerPrefix: row.callerPrefix || '', calledPrefix: row.calledPrefix || '',
    calledSuffix: row.calledSuffix || '', ringing: row.ringing || '',
    status: row.status ?? 1
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const data = {
      phone: form.phone, vdnId: form.vdnId,
      callerPrefix: form.callerPrefix || null, calledPrefix: form.calledPrefix || null,
      calledSuffix: form.calledSuffix || null, ringing: form.ringing || null,
      status: form.status
    }
    if (isEdit.value) {
      await updateVdnPhone(form.id, data)
    } else {
      await saveVdnPhone(data)
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除接入号码 "${row.phone}" 吗？`, '提示', { type: 'warning' })
    await deleteVdnPhone(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadVdnOptions(); loadData() })
</script>

<style scoped>
.page-header {
  display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
}
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form {
  margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px;
}
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
