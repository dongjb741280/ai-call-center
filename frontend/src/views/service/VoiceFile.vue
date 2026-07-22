<template>
  <div class="page-card">
    <div class="page-header">
      <h2>语音文件</h2>
      <el-button type="primary"><el-icon><Plus /></el-icon> 新增</el-button>
    </div>

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="id" width="70" />
      <el-table-column label="创建时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column prop="name" label="文件名称" min-width="180" show-overflow-tooltip />
      <el-table-column label="文件类型" min-width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ row.type === 1 ? '文件上传' : row.type === 2 ? '语音合成' : row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="engineName" label="tts引擎" min-width="130" show-overflow-tooltip>
        <template #default="{ row }">{{ row.engineName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="speechText" label="文件内容" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">{{ row.speechText || row.name || '-' }}</template>
      </el-table-column>
      <el-table-column label="录音" min-width="120" align="center">
        <template #default="{ row }">
          <div class="play-cell">
            <el-button :icon="VideoPlay" circle size="small" type="primary" class="play-btn" @click="handlePlay(row)" />
            <span class="play-time">0:00/0:00</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small">删除</el-button>
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

    <el-dialog v-model="dialogVisible" title="编辑语音" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="语音文件" prop="name">
          <el-input v-model="form.name" placeholder="文件名称" />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="文件上传" :value="1" />
            <el-option label="语音合成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 2" label="tts引擎">
          <el-select v-model="form.engineId" style="width: 100%">
            <el-option label="aliyun-tts-1" :value="13" />
            <el-option label="aliyun-tts" :value="13" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 2" label="语音内容">
          <el-input v-model="form.speechText" type="textarea" :rows="4" placeholder="请输入语音合成内容" />
        </el-form-item>
        <el-form-item v-if="form.type === 1" label="语音文件">
          <el-upload :show-file-list="false" :http-request="handleUpload">
            <el-button type="primary">上传文件</el-button>
          </el-upload>
          <div v-if="form.ossId" class="uploaded-file">{{ form.ossId }}</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPlaybackList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const list = ref([])
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, name: '', type: 1, engineId: null, speechText: '', ossId: '' })
const rules = { name: [{ required: true, message: '请输入文件名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: '{}' }
    const res = await getPlaybackList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', type: 1, engineId: null, speechText: '', ossId: '' })
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id, name: row.name || '', type: row.type ?? 1,
    engineId: row.engineId ?? null, speechText: row.speechText || '',
    ossId: row.ossId || ''
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    // TODO: call save API
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleUpload = async ({ file }) => {
  ElMessage.info(`上传文件: ${file.name}`)
}

const handlePlay = (row) => {
  const url = row.ossId ? `https://dev.voice9.com${row.ossId}` : row.playback
  if (url) window.open(url, '_blank')
}

const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.play-cell { display: flex; align-items: center; justify-content: center; gap: 6px; }
.play-btn { width: 28px; height: 28px; }
.play-time { font-size: 12px; color: #909399; white-space: nowrap; }
.uploaded-file { margin-top: 4px; font-size: 12px; color: #909399; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
