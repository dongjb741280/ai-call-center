<template>
  <div class="page-card">
    <div class="page-header">
      <h2>技能</h2>
    </div>

    <div class="skill-layout">
      <!-- 左侧：分组列表 -->
      <div class="left-panel">
        <div v-for="group in groupSkillsData" :key="group.id" class="group-block">
          <div class="group-title">
            <span>{{ group.name }}</span>
            <el-tag size="small">{{ group.skills?.length || 0 }}</el-tag>
          </div>
          <div class="skill-items">
            <div
              v-for="skill in group.skills"
              :key="skill.id"
              :class="['skill-row', { active: selectedSkill?.id === skill.id }]"
              @click="selectSkill(skill, group)"
            >
              <span class="skill-name">{{ skill.skillName || skill.name || '-' }}</span>
              <div class="skill-actions">
                <el-button link type="primary" size="small" @click.stop="handleEdit(skill)">编辑</el-button>
                <el-button link type="danger" size="small" @click.stop="handleDelete(skill)">删除</el-button>
              </div>
            </div>
            <div v-if="!group.skills?.length" class="empty-text">暂无技能</div>
          </div>
        </div>
      </div>

      <!-- 右侧：坐席列表 -->
      <div class="right-panel">
        <el-card shadow="never" class="agent-card">
          <template #header>
            <div class="agent-card-header">
              <el-button type="primary" size="small" @click="handleAdd">新增</el-button>
              <span v-if="selectedSkill" class="skill-ref">
                {{ selectedSkill.name || selectedSkill.skillName }}
                <el-tag size="small" type="info" style="margin-left: 4px">{{ selectedGroup?.name }}</el-tag>
              </span>
              <span v-if="agentList.length" class="tab-count">列表总数: {{ filteredAgentList.length }}</span>
            </div>
          </template>
          <div class="agent-search">
            <el-input v-model="agentSearchForm.keyword" placeholder="坐席" clearable size="small" style="width: 120px" />
            <el-select v-model="agentSearchForm.type" placeholder="坐席类型" clearable size="small" style="width: 120px">
              <el-option label="普通坐席" value="普通坐席" />
              <el-option label="班长坐席" value="班长坐席" />
            </el-select>
            <el-select v-model="agentSearchForm.level" placeholder="技能等级" clearable size="small" style="width: 110px">
              <el-option label="等级" value="1" />
              <el-option label="百分比" value="2" />
              <el-option label="固定值" value="3" />
            </el-select>
            <el-button type="primary" size="small">搜索</el-button>
            <el-button size="small" @click="resetAgentSearch">重置</el-button>
          </div>
          <el-table :data="filteredAgentList" v-loading="agentLoading" size="small" max-height="500">
            <el-table-column label="坐席账号" min-width="110">
              <template #default="{ row }">{{ row.agentKey || row.agent?.agentKey || '' }}</template>
            </el-table-column>
            <el-table-column label="坐席名称" min-width="110">
              <template #default="{ row }">{{ row.agentName || row.agent?.name || '' }}</template>
            </el-table-column>
            <el-table-column label="等级类型" min-width="90">
              <template #default="{ row }">{{ rankTypeMap[row.rankType] || row.rankValue || row.skillLevel || '-' }}</template>
            </el-table-column>
            <el-table-column label="匹配规则" min-width="90">
              <template #default="{ row }">{{ row.matchType === 1 ? '低到高' : row.matchType === 2 ? '高到低' : row.matchType || '-' }}</template>
            </el-table-column>
            <el-table-column label="占用率" min-width="80" align="center">
              <template #default="{ row }">{{ row.shareValue || row.rankValue || '-' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="handleEditAgent(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleRemoveAgent(row)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="!selectedSkill" class="empty-hint">请从左侧选择一个技能</div>
          <div v-else-if="agentList.length === 0 && !agentLoading" class="empty-hint">该技能下暂无坐席</div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑技能' : '新增技能'" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="技能名称" prop="name">
          <el-input v-model="form.name" placeholder="技能名称" />
        </el-form-item>
        <el-form-item label="技能组">
          <el-select v-model="form.groupId" style="width: 100%" placeholder="选择技能组">
            <el-option v-for="g in allGroups" :key="g.id" :label="g.name" :value="g.id" />
          </el-select>
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSkillList, getSkillDetail, addSkill, updateSkill, deleteSkill, deleteSkillAgent } from '@/api/config'
import { getGroupConfigList, getGroupConfigDetail } from '@/api/config'

const rankTypeMap = { 1: '等级', 2: '百分比', 3: '固定值' }

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const allGroups = ref([])
const groupSkillsData = ref([])
const selectedSkill = ref(null)
const selectedGroup = ref(null)
const agentList = ref([])
const agentLoading = ref(false)
const agentSearchForm = reactive({ keyword: '', type: '', level: '' })

const filteredAgentList = computed(() => {
  return agentList.value.filter(a => {
    const matchKeyword = !agentSearchForm.keyword ||
      (a.agentKey || '').includes(agentSearchForm.keyword) ||
      (a.agentName || a.agent?.name || '').includes(agentSearchForm.keyword)
    const matchType = !agentSearchForm.type ||
      (a.agentType || '').includes(agentSearchForm.type)
    const matchLevel = !agentSearchForm.level ||
      String(a.rankType || a.rankValue || a.skillLevel) === agentSearchForm.level
    return matchKeyword && matchType && matchLevel
  })
})

const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, name: '', groupId: null, type: '普通', description: '' })
const rules = { name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', groupId: null, type: '普通', description: '' })
}

const loadData = async () => {
  loading.value = true
  try {
    const groupRes = await getGroupConfigList({ pageNum: 1, pageSize: 100, query: '{}' })
    if (groupRes.code === 0) {
      const groups = groupRes.data?.list || []
      allGroups.value = groups
      // 为每个组加载详情获取技能列表
      const details = await Promise.all(groups.map(g =>
        getGroupConfigDetail(g.id).then(r => (r.code === 0 ? r.data : null)).catch(() => null)
      ))
      groupSkillsData.value = groups.map((g, i) => ({
        ...g,
        skills: (details[i]?.skills || []).map(s => ({ ...s, groupId: g.id }))
      }))
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const resetAgentSearch = () => {
  agentSearchForm.keyword = ''
  agentSearchForm.type = ''
  agentSearchForm.level = ''
}

const selectSkill = async (skill, group) => {
  selectedSkill.value = skill
  selectedGroup.value = group
  agentLoading.value = true
  agentList.value = []
  try {
    const res = await getSkillDetail(skill.id)
    if (res.code === 0) {
      agentList.value = res.data?.skillAgents || res.data?.agents || res.data?.agentList || []
    }
  } catch { /* empty */ }
  finally { agentLoading.value = false }
}

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, name: row.name || row.skillName || '', groupId: row.groupId ?? null, type: row.type || '普通', description: row.description || '' })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updateSkill(form.id, { ...form })
    else await addSkill({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除技能 "${row.name || row.skillName}" 吗？`, '提示', { type: 'warning' })
    await deleteSkill(row.id)
    if (selectedSkill.value?.id === row.id) { selectedSkill.value = null; agentList.value = [] }
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

const handleEditAgent = (row) => {
  ElMessage.info(`编辑坐席: ${row.agentKey || row.agent?.agentKey}`)
}

const handleRemoveAgent = async (row) => {
  try {
    await ElMessageBox.confirm(`确认移除坐席 "${row.agentKey || row.agent?.agentKey}" 吗？`, '提示', { type: 'warning' })
    await deleteSkillAgent(row.id, { agentId: row.agentId || row.agent?.id })
    ElMessage.success('移除成功')
    selectSkill(selectedSkill.value, selectedGroup.value)
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.header-actions { display: flex; gap: 8px; }

.skill-layout { display: flex; gap: 16px; align-items: flex-start; }
.left-panel { width: 360px; flex-shrink: 0; display: flex; flex-direction: column; gap: 12px; }
.right-panel { flex: 1; min-width: 0; }

.group-block { margin-bottom: 8px; }
.group-title { display: flex; justify-content: space-between; align-items: center; padding: 10px 12px; background: #f0f2f5; border-radius: 4px 4px 0 0; font-weight: 600; font-size: 14px; }
.skill-items { border: 1px solid #ebeef5; border-top: none; border-radius: 0 0 4px 4px; }
.skill-row { display: flex; justify-content: space-between; align-items: center; padding: 8px 12px; border-bottom: 1px solid #f0f0f0; cursor: pointer; }
.skill-row:last-child { border-bottom: none; }
.skill-row:hover { background: #f5f7fa; }
.skill-row.active { background: #ecf5ff; color: #409EFF; }
.skill-name { font-size: 13px; }
.skill-actions { display: flex; gap: 4px; flex-shrink: 0; }
.empty-text { color: #909399; padding: 12px 0; text-align: center; font-size: 13px; }

.agent-card-header { display: flex; align-items: center; gap: 12px; }
.skill-ref { color: #409EFF; font-size: 13px; }
.tab-count { margin-left: auto; color: #909399; font-size: 12px; font-weight: normal; }
.agent-search { display: flex; gap: 8px; padding: 8px 0; flex-wrap: wrap; }
.empty-hint { color: #909399; padding: 40px 0; text-align: center; }
</style>
