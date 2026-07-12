<template>
  <div class="page-card">
    <div class="page-header">
      <h2>号码路由</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增路由
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="企业ID">
        <el-input v-model="searchForm.companyId" placeholder="请输入企业ID" clearable />
      </el-form-item>
      <el-form-item label="路由号码">
        <el-input v-model="searchForm.routeNum" placeholder="请输入路由号码" clearable />
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
      <el-table-column prop="id" label="id" width="80" />
      <el-table-column label="修改时间" width="180">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column prop="companyId" label="企业ID" width="100" />
      <el-table-column label="路由号码" width="140">
        <template #default="{ row }">{{ row.routeNum || '' }}</template>
      </el-table-column>
      <el-table-column prop="numMin" label="最小长度" width="100" align="center" />
      <el-table-column prop="numMax" label="最大长度" width="100" align="center" />
      <el-table-column label="主叫号码规则" width="120" align="center">
        <template #default="{ row }">{{ row.callerChange ?? '' }}</template>
      </el-table-column>
      <el-table-column label="主叫替换号码" width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ row.callerChangeNum || '' }}</template>
      </el-table-column>
      <el-table-column label="被叫号码规则" width="120" align="center">
        <template #default="{ row }">{{ row.calledChange ?? '' }}</template>
      </el-table-column>
      <el-table-column label="被叫替换号码" width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ row.calledChangeNum || '' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑号码路由' : '新增号码路由'" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="路由号码" prop="routeNum">
          <el-input v-model="form.routeNum" placeholder="如：950、010" maxlength="32" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最小长度" prop="numMin">
              <el-input v-model.number="form.numMin" placeholder="1-32" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大长度" prop="numMax">
              <el-input v-model.number="form.numMax" placeholder="1-32" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="网关列表" prop="routeGroupId">
          <el-select v-model="form.routeGroupId" placeholder="选择网关组" style="width: 100%">
            <el-option v-for="g in groupList" :key="g.id" :label="g.routeGroup || g.name" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-divider content-position="left">主叫替换</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="替换规则" label-width="80px">
              <el-input v-model.number="form.callerChange" placeholder="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="替换号码" label-width="80px">
              <el-input v-model="form.callerChangeNum" placeholder="替换号码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">被叫替换</el-divider>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="替换规则" label-width="80px">
              <el-input v-model.number="form.calledChange" placeholder="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="替换号码" label-width="80px">
              <el-input v-model="form.calledChangeNum" placeholder="替换号码" />
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
import { getRouteCallList, addRouteCall, updateRouteCall, deleteRouteCall } from '@/api/config'
import { getRouteGroupList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const groupList = ref([])

const searchForm = reactive({ companyId: '', routeNum: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, routeNum: '', numMin: null, numMax: null, routeGroupId: null,
  callerChange: 0, callerChangeNum: '', calledChange: 0, calledChangeNum: ''
})

const rules = {
  routeNum: [{ required: true, message: '请输入路由号码', trigger: 'blur' }],
  numMin: [
    { required: true, message: '请输入最小长度', trigger: 'blur' },
    { type: 'number', min: 1, max: 32, message: '范围1-32', trigger: 'blur' }
  ],
  numMax: [
    { required: true, message: '请输入最大长度', trigger: 'blur' },
    { type: 'number', min: 1, max: 32, message: '范围1-32', trigger: 'blur' }
  ],
  routeGroupId: [{ required: true, message: '请选择网关组', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, routeNum: '', numMin: null, numMax: null, routeGroupId: null,
    callerChange: 0, callerChangeNum: '', calledChange: 0, calledChangeNum: ''
  })
}

const loadGroupList = async () => {
  try {
    const res = await getRouteGroupList({ pageNum: 1, pageSize: 100 })
    if (res.code === 0) groupList.value = res.data?.list || []
  } catch { /* non-critical */ }
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.companyId) query.companyId = searchForm.companyId
    if (searchForm.routeNum) query.routeNum = searchForm.routeNum
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify(query)
    }
    const res = await getRouteCallList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载号码路由失败') }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.companyId = ''; searchForm.routeNum = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => {
  isEdit.value = false; resetForm()
  if (groupList.value.length === 0) loadGroupList()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, routeNum: row.routeNum || '',
    numMin: row.numMin ?? null, numMax: row.numMax ?? null, routeGroupId: row.routeGroupId ?? null,
    callerChange: row.callerChange ?? 0, callerChangeNum: row.callerChangeNum || '',
    calledChange: row.calledChange ?? 0, calledChangeNum: row.calledChangeNum || ''
  })
  if (groupList.value.length === 0) loadGroupList()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updateRouteCall(form.id, { ...form })
    else await addRouteCall({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除路由 "${row.routeNum}" 吗？`, '提示', { type: 'warning' })
    await deleteRouteCall(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadData(); loadGroupList() })
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
