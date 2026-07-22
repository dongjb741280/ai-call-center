<template>
  <div class="page-card">
    <div class="page-header">
      <h2>ai引擎</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="名称">
        <el-input v-model="searchForm.name" placeholder="请输入" clearable />
      </el-form-item>
      <el-form-item label="企业ID">
        <el-input v-model="searchForm.companyId" placeholder="请输入" clearable />
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
      <el-table-column prop="id" label="id" width="60" />
      <el-table-column label="修改时间" min-width="160">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column prop="companyId" label="企业ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="mrcpProfile" label="profile" min-width="120" show-overflow-tooltip />
      <el-table-column label="ai引擎" min-width="80">
        <template #default="{ row }">{{ row.aiType === 1 ? 'tts' : 'asr' }}</template>
      </el-table-column>
      <el-table-column prop="aiProduct" label="服务商名称" min-width="100" />
      <el-table-column prop="voice" label="发音音色" min-width="90">
        <template #default="{ row }">{{ row.voice || '-' }}</template>
      </el-table-column>
      <el-table-column prop="grammar" label="grammar" min-width="110" show-overflow-tooltip>
        <template #default="{ row }">{{ row.grammar || '-' }}</template>
      </el-table-column>
      <el-table-column prop="aiParams" label="参数集" min-width="220" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑ai引擎' : '新增ai引擎'" width="580px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="如：aliyun-asr-1" />
        </el-form-item>
        <el-form-item label="profile" prop="mrcpProfile">
          <el-select v-model="form.mrcpProfile" style="width: 100%">
            <el-option label="asr-mrcp" value="asr-mrcp" />
            <el-option label="tts-mrcp" value="tts-mrcp" />
            <el-option label="baidu-asr" value="baidu-asr" />
            <el-option label="cosy_tts" value="cosy_tts" />
            <el-option label="audio_ws" value="audio_ws" />
          </el-select>
        </el-form-item>
        <el-form-item label="ai引擎类型" prop="aiType">
          <el-select v-model="form.aiType" style="width: 100%">
            <el-option label="tts (语音合成)" :value="1" />
            <el-option label="asr (语音识别)" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务商名称" prop="aiProduct">
          <el-select v-model="form.aiProduct" style="width: 100%">
            <el-option label="aliyun" value="aliyun" />
            <el-option label="tencent" value="tencent" />
            <el-option label="baidu" value="baidu" />
            <el-option label="cosy_tts" value="cosy_tts" />
            <el-option label="audio_ws" value="audio_ws" />
          </el-select>
        </el-form-item>
        <el-form-item label="发音音色">
          <el-input v-model="form.voice" placeholder="如：aixia" />
        </el-form-item>
        <el-form-item label="grammar">
          <el-input v-model="form.grammar" placeholder="如：test test" />
        </el-form-item>
        <el-form-item label="参数集">
          <el-input v-model="form.aiParams" type="textarea" :rows="3" placeholder="JSON参数" />
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
import { getEngineList, saveEngine, deleteEngine } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', companyId: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', mrcpProfile: '', aiType: null,
  aiProduct: '', voice: '', grammar: '', aiParams: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  mrcpProfile: [{ required: true, message: '请选择profile', trigger: 'change' }],
  aiType: [{ required: true, message: '请选择引擎类型', trigger: 'change' }],
  aiProduct: [{ required: true, message: '请选择服务商', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', mrcpProfile: '', aiType: null, aiProduct: '', voice: '', grammar: '', aiParams: '' })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.name) query.name = searchForm.name
    if (searchForm.companyId) query.companyId = searchForm.companyId
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getEngineList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.companyId = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, name: row.name || '', mrcpProfile: row.mrcpProfile || '',
    aiType: row.aiType ?? null, aiProduct: row.aiProduct || '',
    voice: row.voice || '', grammar: row.grammar || '', aiParams: row.aiParams || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    await saveEngine({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteEngine(row.id)
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
