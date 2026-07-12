<template>
  <div class="page-card">
    <div class="page-header">
      <h2>sip网关</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="网关账号">
        <el-input v-model="searchForm.username" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="企业名称">
        <el-input v-model="searchForm.companyName" placeholder="请输入" clearable />
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
      <el-table-column prop="username" label="网关账号" width="140" />
      <el-table-column prop="passwd" label="网关密码" width="120" show-overflow-tooltip />
      <el-table-column prop="companyCode" label="企业编码" width="100" />
      <el-table-column prop="companyName" label="企业名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="registerAddr" label="注册地址" width="180" show-overflow-tooltip />
      <el-table-column prop="external" label="网关外网" width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.external || '-' }}</template>
      </el-table-column>
      <el-table-column prop="internal" label="网关内网" width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.internal || '-' }}</template>
      </el-table-column>
      <el-table-column label="注册时间" width="100" align="center">
        <template #default="{ row }">{{ row.registerTime ? formatTime(row.registerTime) : '-' }}</template>
      </el-table-column>
      <el-table-column prop="expire" label="注册周期" width="90" align="center">
        <template #default="{ row }">{{ row.expire ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 2 ? 'success' : 'danger'" size="small">
            {{ row.status === 2 ? '在线' : '下线' }}
          </el-tag>
        </template>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑sip网关' : '新增sip网关'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="企业编码" prop="companyCode">
          <el-input v-model="form.companyCode" placeholder="企业编码" />
        </el-form-item>
        <el-form-item label="网关账号" prop="username">
          <el-input v-model="form.username" placeholder="2-16个字符" maxlength="16" />
        </el-form-item>
        <el-form-item label="网关密码" prop="passwd">
          <el-input v-model="form.passwd" placeholder="2-16个字符" maxlength="16" show-password />
        </el-form-item>
        <el-form-item label="注册地址" prop="registerAddr">
          <el-input v-model="form.registerAddr" placeholder="如 81.71.143.162:7450" />
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
import { getSipGatewayList, saveSipGateway, deleteSipGateway } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ username: '', companyName: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, companyCode: '', username: '', passwd: '', registerAddr: '' })

const rules = {
  companyCode: [{ required: true, message: '请输入企业编码', trigger: 'blur' }],
  username: [{ required: true, message: '请输入网关账号', trigger: 'blur' }],
  passwd: [{ required: true, message: '请输入网关密码', trigger: 'blur' }],
  registerAddr: [{ required: true, message: '请输入注册地址', trigger: 'blur' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, companyCode: '', username: '', passwd: '', registerAddr: '' })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.username) query.username = searchForm.username
    if (searchForm.companyName) query.companyName = searchForm.companyName
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getSipGatewayList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.username = ''; searchForm.companyName = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, companyCode: row.companyCode || '', username: row.username || '',
    passwd: row.passwd || '', registerAddr: row.registerAddr || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    await saveSipGateway({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除 "${row.username}" 吗？`, '提示', { type: 'warning' })
    await deleteSipGateway([row.id])
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
