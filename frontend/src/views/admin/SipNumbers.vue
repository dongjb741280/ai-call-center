<template>
  <div class="page-card">
    <div class="page-header">
      <h2>sip分机</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="sip号">
        <el-input v-model="searchForm.sip" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="企业ID">
        <el-input v-model="searchForm.companyId" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="坐席账号">
        <el-input v-model="searchForm.agentKey" placeholder="请输入" clearable />
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
      <el-table-column label="sip号" width="120">
        <template #default="{ row }">{{ row.sip }}</template>
      </el-table-column>
      <el-table-column prop="sipPwd" label="sip密码" width="120" show-overflow-tooltip />
      <el-table-column prop="companyId" label="企业ID" width="80" />
      <el-table-column label="在线" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="坐席账号" width="120">
        <template #default="{ row }">{{ row.agentKey || (row.agent && row.agent.agentKey) || '' }}</template>
      </el-table-column>
      <el-table-column label="注册时间" width="170">
        <template #default="{ row }">{{ row.registerTime ? formatTime(row.registerTime) : formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="expires" width="80" align="center">
        <template #default="{ row }">{{ row.expires || '-' }}</template>
      </el-table-column>
      <el-table-column label="注册地址" width="200" show-overflow-tooltip>
        <template #default="{ row }">{{ row.registerAddr || '-' }}</template>
      </el-table-column>
      <el-table-column label="客户端地址" width="180" show-overflow-tooltip>
        <template #default="{ row }">{{ row.clientAddr || '-' }}</template>
      </el-table-column>
      <el-table-column label="userAgent" width="160" show-overflow-tooltip>
        <template #default="{ row }">{{ row.userAgent || '-' }}</template>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑sip分机' : '新增sip分机'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="sip号" prop="sip">
          <el-input v-model.number="form.sip" placeholder="如：950001" />
        </el-form-item>
        <el-form-item label="sip密码" prop="sipPwd">
          <el-input v-model="form.sipPwd" placeholder="SIP注册密码" show-password />
        </el-form-item>
        <el-form-item label="企业ID" prop="companyId">
          <el-input v-model.number="form.companyId" placeholder="企业ID" />
        </el-form-item>
        <el-form-item label="坐席ID">
          <el-input v-model.number="form.agentId" placeholder="绑定坐席ID" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSipList, addSip, updateSip, deleteSip } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ sip: '', companyId: '', agentKey: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, sip: '', sipPwd: '', companyId: null, agentId: null, status: 1 })

const rules = {
  sip: [{ required: true, message: '请输入sip号', trigger: 'blur' }],
  sipPwd: [{ required: true, message: '请输入sip密码', trigger: 'blur' }],
  companyId: [{ required: true, message: '请输入企业ID', trigger: 'blur' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, sip: '', sipPwd: '', companyId: null, agentId: null, status: 1 })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.sip) query.sip = searchForm.sip
    if (searchForm.companyId) query.companyId = searchForm.companyId
    if (searchForm.agentKey) query.agentKey = searchForm.agentKey
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getSipList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.sip = ''; searchForm.companyId = ''; searchForm.agentKey = ''; pagination.currentPage = 1; loadData() }

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, sip: row.sip, sipPwd: row.sipPwd || '',
    companyId: row.companyId ?? null, agentId: row.agentId ?? null, status: row.status ?? 1
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updateSip(form.id, { ...form })
    else await addSip({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除sip号 "${row.sip}" 吗？`, '提示', { type: 'warning' })
    await deleteSip(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
