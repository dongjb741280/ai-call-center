<template>
  <div class="page-card">
    <div class="page-header">
      <h2>技能管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增技能
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="技能名称" min-width="160" />
      <el-table-column prop="type" label="类型" width="120">
        <template #default="{ row }">
          <el-tag>{{ row.type || '默认' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="companyName" label="所属企业" width="160" />
      <el-table-column prop="agentCount" label="坐席数" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="success" size="small" @click="handleManageAgents(row)">管理坐席</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <!-- 技能弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑技能' : '新增技能'" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="技能名称" prop="name">
          <el-input v-model="form.name" placeholder="技能名称" />
        </el-form-item>
        <el-form-item label="技能类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="普通" value="普通" />
            <el-option label="VIP" value="VIP" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" placeholder="技能描述" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理坐席弹窗 -->
    <el-dialog v-model="agentDialogVisible" :title="`管理坐席 - ${currentSkill?.name || ''}`" width="700px" @close="agentDialogVisible = false">
      <div v-loading="agentLoading">
        <div class="tab-actions">
          <el-input v-model="agentSearch" placeholder="搜索坐席" clearable style="width: 200px" />
          <el-button type="primary" @click="loadSkillAgents">刷新</el-button>
        </div>
        <el-table :data="filteredSkillAgents" border max-height="400">
          <el-table-column prop="agentKey" label="坐席账号" width="180" />
          <el-table-column prop="agentName" label="坐席名称" min-width="140" />
          <el-table-column prop="skillLevel" label="技能等级" width="120" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="handleRemoveAgent(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSkillList, getSkillDetail, addSkill, updateSkill, deleteSkill, deleteSkillAgent } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])

const pagination = reactive({ currentPage: 1, pageSize: 20, total: 0 })
const form = reactive({ id: null, name: '', type: '普通', description: '' })

const rules = { name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }] }

// 坐席管理
const agentDialogVisible = ref(false)
const agentLoading = ref(false)
const currentSkill = ref(null)
const skillAgents = ref([])
const agentSearch = ref('')

const filteredSkillAgents = computed(() => {
  if (!agentSearch.value) return skillAgents.value
  const kw = agentSearch.value.toLowerCase()
  return skillAgents.value.filter(a =>
    (a.agentKey || '').toLowerCase().includes(kw) ||
    (a.agentName || '').toLowerCase().includes(kw)
  )
})

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', type: '普通', description: '' })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize }
    const res = await getSkillList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, name: row.name, type: row.type || '普通', description: row.description || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateSkill(form.id, { ...form })
    } else {
      await addSkill({ ...form })
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除技能 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteSkill(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

const loadSkillAgents = async () => {
  if (!currentSkill.value) return
  agentLoading.value = true
  try {
    const res = await getSkillDetail(currentSkill.value.id)
    if (res.code === 0) {
      skillAgents.value = res.data?.agents || res.data?.agentList || []
    }
  } catch { ElMessage.error('加载坐席失败') }
  finally { agentLoading.value = false }
}

const handleManageAgents = (row) => {
  currentSkill.value = row
  agentSearch.value = ''
  agentDialogVisible.value = true
  loadSkillAgents()
}

const handleRemoveAgent = async (row) => {
  try {
    await ElMessageBox.confirm(`确认从技能中移除坐席 "${row.agentKey}" 吗？`, '提示', { type: 'warning' })
    await deleteSkillAgent(row.id, { agentId: row.agentId })
    ElMessage.success('移除成功')
    loadSkillAgents()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-card { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
.tab-actions { display: flex; gap: 8px; align-items: center; margin-bottom: 12px; }
</style>
