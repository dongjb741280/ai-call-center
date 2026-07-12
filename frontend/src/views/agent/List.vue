<template>
  <div class="page-card">
    <div class="page-header">
      <h2>坐席管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button type="success" @click="batchVisible = true">
          <el-icon><Plus /></el-icon>
          批量新增
        </el-button>
      </div>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="技能组">
        <el-select v-model="searchForm.groupId" placeholder="请选择" clearable style="width: 160px">
          <el-option v-for="g in groupOptions" :key="g.id" :label="g.name" :value="g.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="技能组id">
        <el-input v-model="searchForm.groupId" placeholder="请输入" clearable style="width: 100px" />
      </el-form-item>
      <el-form-item label="分机号">
        <el-input v-model="searchForm.sip" placeholder="请输入" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="坐席账号">
        <el-input v-model="searchForm.agentKey" placeholder="请输入" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="在线">
        <el-select v-model="searchForm.online" placeholder="请选择" clearable style="width: 100px">
          <el-option label="在线" :value="1" />
          <el-option label="下线" :value="0" />
        </el-select>
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
      <el-table-column prop="agentKey" label="坐席账号" width="100" />
      <el-table-column prop="agentName" label="坐席名称" width="100" />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="分机号" width="100">
        <template #default="{ row }">{{ row.sip || row.sipNum || '-' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="坐席类型" width="100">
        <template #default="{ row }">
          <el-tag size="small">{{ row.agentType || '普通坐席' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="技能组" width="80" align="center">
        <template #default="{ row }">{{ row.groupId || row.group?.id || '-' }}</template>
      </el-table-column>
      <el-table-column label="开启asr" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.asr ? 'success' : 'info'">{{ row.asr ? '已开启' : '未开启' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总机坐席" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.isOperator ? 'success' : 'info'">{{ row.isOperator ? '已启用' : '未启用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总机日程" width="100" align="center">
        <template #default="{ row }">{{ row.scheduleName || '-' }}</template>
      </el-table-column>
      <el-table-column label="话后时长" width="80" align="center">
        <template #default="{ row }">{{ row.afterWork || row.afterInterval || '-' }}s</template>
      </el-table-column>
      <el-table-column label="外显号码" width="120" show-overflow-tooltip>
        <template #default="{ row }">{{ row.display || '-' }}</template>
      </el-table-column>
      <el-table-column label="振铃时长" width="80" align="center">
        <template #default="{ row }">{{ row.ringTime || '-' }}s</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="在线" width="80" align="center" fixed="right">
        <template #default="{ row }">
          <el-tag size="small" :type="row.agentState === 'TALKING' ? 'warning' : row.online ? 'success' : 'danger'">
            {{ row.agentState === 'TALKING' ? '通话' : row.online ? '在线' : '下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="warning" size="small" @click="handleOffline(row)">IsOffline</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑坐席' : '新增坐席'" width="650px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="坐席账号" prop="agentKey">
              <el-input v-model="form.agentKey" placeholder="坐席账号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="坐席名称" prop="agentName">
              <el-input v-model="form.agentName" placeholder="坐席名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码">
              <el-input v-model="form.passwd" placeholder="密码" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="坐席手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="技能组" prop="groupId">
              <el-select v-model="form.groupId" style="width: 100%" placeholder="选择技能组">
                <el-option v-for="g in groupOptions" :key="g.id" :label="g.name" :value="g.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="坐席类型">
              <el-select v-model="form.agentType" style="width: 100%">
                <el-option label="普通坐席" value="普通坐席" />
                <el-option label="班长坐席" value="班长坐席" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分机号">
              <el-input v-model="form.sip" placeholder="分机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总机坐席">
              <el-switch v-model="form.isOperator" active-text="启用" inactive-text="未启用" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="外显号码">
              <el-input v-model="form.display" placeholder="外显号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="振铃时长(秒)">
              <el-input v-model.number="form.ringTime" placeholder="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="话后时长(秒)">
              <el-input v-model.number="form.afterWork" placeholder="5" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开启asr">
              <el-switch v-model="form.asr" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="chat">
              <el-switch v-model="form.chat" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="videoChat">
              <el-switch v-model="form.videoChat" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总机日程">
              <el-input v-model="form.scheduleName" placeholder="日程名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="坐席详情" width="650px" @close="detailVisible = false">
      <div v-loading="detailLoading" v-if="detail">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="坐席账号">{{ detail.agentKey }}</el-descriptions-item>
          <el-descriptions-item label="分机号">{{ detail.sip || detail.sipNum || '-' }}</el-descriptions-item>
          <el-descriptions-item label="坐席名称">{{ detail.agentName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(detail.cts) }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ formatTime(detail.uts) }}</el-descriptions-item>
          <el-descriptions-item label="坐席类型">{{ detail.agentType || '普通坐席' }}</el-descriptions-item>
          <el-descriptions-item label="技能组">{{ detail.groupName || detail.groupId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="chat">
            <el-tag size="small" :type="detail.chat ? 'success' : 'info'">{{ detail.chat ? '已开启' : '未开启' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="videoChat">
            <el-tag size="small" :type="detail.videoChat ? 'success' : 'info'">{{ detail.videoChat ? '已开启' : '未开启' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总机坐席">
            <el-tag size="small" :type="detail.isOperator ? 'success' : 'info'">{{ detail.isOperator ? '已启用' : '未启用' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开启asr">
            <el-tag size="small" :type="detail.asr ? 'success' : 'info'">{{ detail.asr ? '已开启' : '未开启' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="总机日程">{{ detail.scheduleName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="话后时长">{{ detail.afterWork || detail.afterInterval || '-' }}s</el-descriptions-item>
          <el-descriptions-item label="外显号码">{{ detail.display || '-' }}</el-descriptions-item>
          <el-descriptions-item label="振铃时长">{{ detail.ringTime || '-' }}s</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detail.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="在线">
            <el-tag size="small" :type="detail.agentState === 'TALKING' ? 'warning' : detail.online ? 'success' : 'danger'">
              {{ detail.agentState === 'TALKING' ? '通话' : detail.online ? '在线' : '下线' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-card shadow="never" class="detail-card">
          <template #header><span>技能组</span></template>
          <el-table :data="groupTableData" size="small" max-height="250">
            <el-table-column prop="id" label="技能组id" width="100" />
            <el-table-column prop="name" label="技能组" width="140" />
            <el-table-column label="技能组等级" width="100" align="center">
              <template #default="{ row }">{{ row.level || row.levelValue || '-' }}</template>
            </el-table-column>
          </el-table>
          <div v-if="groupTableData.length === 0" style="color:#909399;padding:12px 0">暂无数据</div>
        </el-card>

        <el-card shadow="never" class="detail-card">
          <template #header><span>坐席技能</span></template>
          <el-table :data="skillTableData" size="small" max-height="250">
            <el-table-column prop="skillId" label="技能id" width="80" />
            <el-table-column label="技能名称" width="140">
              <template #default="{ row }">{{ row.skillName || row.name || '-' }}</template>
            </el-table-column>
            <el-table-column label="等级类型" width="100">
              <template #default="{ row }">{{ {1:'等级',2:'百分比',3:'固定值'}[row.rankType] || row.rankValue || '-' }}</template>
            </el-table-column>
            <el-table-column label="匹配规则" width="100">
              <template #default="{ row }">{{ row.matchType === 1 ? '低到高' : row.matchType === 2 ? '高到低' : row.matchType || '-' }}</template>
            </el-table-column>
            <el-table-column label="占用率" width="80" align="center">
              <template #default="{ row }">{{ row.shareValue ?? row.rankValue ?? '-' }}</template>
            </el-table-column>
          </el-table>
          <div v-if="skillTableData.length === 0" style="color:#909399;padding:12px 0">暂无数据</div>
        </el-card>
      </div>
      <el-empty v-else description="暂无详情数据" />
    </el-dialog>

    <el-dialog v-model="batchVisible" title="批量新增" width="500px" @close="resetBatch">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="新增坐席数" prop="count">
          <el-input v-model.number="batchForm.count" placeholder="1-1000" />
        </el-form-item>
        <el-form-item label="起始编号" prop="start">
          <el-input v-model.number="batchForm.start" placeholder="100-1000000" />
        </el-form-item>
        <el-form-item label="坐席前缀" prop="prefix">
          <el-input v-model="batchForm.prefix" placeholder="最多4位" maxlength="4" />
        </el-form-item>
        <el-form-item label="技能组">
          <el-select v-model="batchForm.groupId" style="width: 100%" placeholder="选择技能组">
            <el-option v-for="g in groupOptions" :key="g.id" :label="g.name" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码" prop="passwd">
          <el-input v-model="batchForm.passwd" placeholder="8-32位" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit" :loading="batchLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAgentConfigList, getAgentConfigDetail, addAgentConfig, updateAgentConfig, deleteAgentConfig, batchAddAgents } from '@/api/config'
import { getGroupConfigList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const groupOptions = ref([])
const searchForm = reactive({ groupId: '', sip: '', agentKey: '', agentName: '', online: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, agentKey: '', agentName: '', passwd: '', phone: '', sip: '',
  groupId: null, agentType: '普通坐席', asr: false, playAgent: true,
  scheduleName: '', afterWork: 5, display: '', ringTime: 30,
  chat: false, videoChat: false, isOperator: false, status: 1
})

const rules = {
  agentKey: [{ required: true, message: '请输入坐席账号', trigger: 'blur' }],
  agentName: [{ required: true, message: '请输入坐席名称', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择技能组', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, agentKey: '', agentName: '', passwd: '', phone: '', sip: '', groupId: null, agentType: '普通坐席', asr: false, playAgent: true, scheduleName: '', afterWork: 5, display: '', ringTime: 30, chat: false, videoChat: false, isOperator: false, status: 1 })
}

const loadGroups = async () => {
  try {
    const res = await getGroupConfigList({ pageNum: 1, pageSize: 100, query: '{}' })
    if (res.code === 0) groupOptions.value = res.data?.list || []
  } catch { /* empty */ }
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.groupId) query.groupId = searchForm.groupId
    if (searchForm.sip) query.sip = searchForm.sip
    if (searchForm.agentKey) query.agentKey = searchForm.agentKey
    if (searchForm.agentName) query.agentName = searchForm.agentName
    if (searchForm.online !== '') query.online = searchForm.online
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getAgentConfigList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const batchVisible = ref(false)
const batchLoading = ref(false)
const batchFormRef = ref()
const batchForm = reactive({ count: null, start: null, prefix: '', groupId: null, passwd: '' })
const batchRules = {
  count: [{ required: true, message: '请输入坐席数量', trigger: 'blur' }],
  start: [{ required: true, message: '请输入起始编号', trigger: 'blur' }],
  passwd: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const resetBatch = () => {
  batchFormRef.value?.resetFields()
  Object.assign(batchForm, { count: null, start: null, prefix: '', groupId: null, passwd: '' })
}

const handleBatchSubmit = async () => {
  if (!batchFormRef.value) return
  try { await batchFormRef.value.validate() } catch { return }
  batchLoading.value = true
  try {
    await batchAddAgents({ ...batchForm })
    ElMessage.success('批量新增成功')
    batchVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { batchLoading.value = false }
}

const handleExport = () => {
  const params = new URLSearchParams()
  params.set('pageNum', '1')
  params.set('pageSize', '999999')
  params.set('query', '{}')
  window.open(`/cc-api/config/agent/export?${params.toString()}`, '_blank')
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.groupId = ''; searchForm.sip = ''; searchForm.agentKey = ''; searchForm.agentName = ''; searchForm.online = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); if (groupOptions.value.length === 0) loadGroups(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, agentKey: row.agentKey || '', agentName: row.agentName || '',
    phone: row.phone || '', sip: row.sip || row.sipNum || '',
    groupId: row.groupId ?? null, agentType: row.agentType || '普通坐席',
    asr: row.asr ?? false, playAgent: row.playAgent ?? true,
    scheduleName: row.scheduleName || '', afterWork: row.afterWork ?? 5,
    display: row.display || '', ringTime: row.ringTime ?? 30,
    chat: row.chat ?? false, videoChat: row.videoChat ?? false, isOperator: row.isOperator ?? false, status: row.status ?? 1
  })
  if (groupOptions.value.length === 0) loadGroups()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updateAgentConfig(form.id, { ...form })
    else await addAgentConfig({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const detailVisible = ref(false)
const detail = ref(null)
const detailLoading = ref(false)

const skillTableData = computed(() => {
  if (!detail.value) return []
  return detail.value.skillAgents || detail.value.skills || detail.value.skillList || []
})

const groupTableData = computed(() => {
  if (!detail.value) return []
  const arr = detail.value.groups || detail.value.groupList
  if (arr && arr.length) return arr
  if (detail.value.groupId) {
    return [{ id: detail.value.groupId, name: detail.value.groupName || `技能组${detail.value.groupId}`, level: detail.value.groupLevel }]
  }
  return []
})

const handleDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    const res = await getAgentConfigDetail(row.id)
    if (res.code === 0) detail.value = res.data
    else detail.value = row
  } catch { detail.value = row }
  finally { detailLoading.value = false }
}

const handleOffline = async (row) => {
  try {
    await ElMessageBox.confirm(`确认将坐席 "${row.agentKey}" 强制下线吗？`, '提示', { type: 'warning' })
    // TODO: call offline API
    ElMessage.success(`${row.agentKey} 已下线`)
  } catch { /* cancelled */ }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除坐席 "${row.agentKey}" 吗？`, '提示', { type: 'warning' })
    await deleteAgentConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadData(); loadGroups() })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.header-actions { display: flex; gap: 8px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
