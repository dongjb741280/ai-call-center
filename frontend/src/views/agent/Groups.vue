<template>
  <div class="page-card">
    <div class="page-header">
      <h2>技能组</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="技能组名称">
        <el-input v-model="searchForm.name" placeholder="请输入" clearable />
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
      <el-table-column prop="id" label="id" width="70" />
      <el-table-column prop="name" label="技能组名称" width="120" />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="外显号码" width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.display || '-' }}</template>
      </el-table-column>
      <el-table-column label="录音类型" width="140" show-overflow-tooltip>
        <template #default="{ row }">{{ row.recordType || row.record || '双声道振铃录音' }}</template>
      </el-table-column>
      <el-table-column label="技能组类型" width="120">
        <template #default="{ row }">
          <el-tag size="small">{{ row.groupType || '普通技能组' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="优先级" width="80" align="center" />
      <el-table-column label="话后时长" width="80" align="center">
        <template #default="{ row }">{{ row.afterWork || row.afterWorkTime || 5 }}s</template>
      </el-table-column>
      <el-table-column label="服务评价" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.evaluate ? 'success' : 'info'">{{ row.evaluate ? '开启' : '未开启' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="外呼超时" width="90" align="center">
        <template #default="{ row }">{{ row.callTimeout || 60 }}s</template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" size="small" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑技能组' : '新增技能组'" width="900px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息 -->
        <el-card shadow="never" class="detail-card">
          <template #header><span>基本信息</span></template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="技能组名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="外显号码">
                <el-input v-model="form.display" placeholder="外显号码" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="录音类型">
                <el-select v-model="form.recordType" style="width: 100%">
                  <el-option label="双声道振铃录音" value="双声道振铃录音" />
                  <el-option label="单声道振铃录音" value="单声道振铃录音" />
                  <el-option label="双声道接通录音" value="双声道接通录音" />
                  <el-option label="单声道接通录音" value="单声道接通录音" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="技能组类型">
                <el-select v-model="form.groupType" style="width: 100%">
                  <el-option label="普通技能组" value="普通技能组" />
                  <el-option label="外呼技能组" value="外呼技能组" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="优先级">
                <el-input v-model.number="form.level" placeholder="数字越大优先级越高" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="话后时长(秒)">
                <el-input v-model.number="form.afterWork" placeholder="5" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="外呼超时(秒)">
                <el-input v-model.number="form.callTimeout" placeholder="60" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="播放工号">
                <el-switch v-model="form.playAgent" :active-value="1" :inactive-value="0" active-text="播放" inactive-text="不播放" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="排队位置播报">
                <el-switch v-model="form.notifyPosition" :active-value="1" :inactive-value="0" active-text="开启" inactive-text="不开启" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="坐席记忆">
                <el-switch v-model="form.callMemory" :active-value="1" :inactive-value="0" active-text="开启" inactive-text="不开启" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="策略类型">
                <el-select v-model="form.strategyType" style="width: 100%">
                  <el-option label="内置策略" value="内置策略" />
                  <el-option label="最长空闲" value="最长空闲" />
                  <el-option label="轮选" value="轮选" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="服务评价">
                <el-switch v-model="form.evaluate" active-text="开启" inactive-text="未开启" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="主叫号码池">
                <el-input v-model="form.callerDisplay" placeholder="主叫号码池" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="被叫号码池">
                <el-input v-model="form.calledDisplay" placeholder="被叫号码池" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="转坐席提示音">
                <el-input v-model="form.transferPlay" placeholder="转坐席提示音" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="话单回调">
                <el-switch v-model="form.callback" active-text="开启" inactive-text="未开启" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 技能列表 -->
        <el-card shadow="never" class="detail-card">
          <template #header><div class="card-header-row"><span>技能列表</span><el-button type="primary" size="small" @click="addSkill">+ 添加技能</el-button></div></template>
          <el-table :data="form.skills || []" size="small" max-height="250">
            <el-table-column label="技能名称" width="140">
              <template #default="{ row, $index }">
                <el-input v-model="form.skills[$index].skillName" size="small" placeholder="技能名称" />
              </template>
            </el-table-column>
            <el-table-column label="等级类型" width="120">
              <template #default="{ row, $index }">
                <el-select v-model="form.skills[$index].rankType" size="small" style="width: 100%">
                  <el-option label="等级" :value="1" />
                  <el-option label="百分比" :value="2" />
                  <el-option label="固定值" :value="3" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="匹配规则" width="100">
              <template #default="{ row, $index }">
                <el-select v-model="form.skills[$index].matchType" size="small" style="width: 100%">
                  <el-option label="低到高" :value="1" />
                  <el-option label="高到低" :value="2" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" size="small" @click="form.skills.splice($index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 排队策略列表 -->
        <el-card shadow="never" class="detail-card">
          <template #header><div class="card-header-row"><span>排队策略列表</span><el-button type="primary" size="small" @click="addOverflow">+ 添加策略</el-button></div></template>
          <el-table :data="form.overflows || []" size="small" max-height="250">
            <el-table-column label="策略名称" width="180">
              <template #default="{ row, $index }">
                <el-input v-model="form.overflows[$index].name" size="small" placeholder="策略名称" />
              </template>
            </el-table-column>
            <el-table-column label="排队类型" width="120">
              <template #default="{ row, $index }">
                <el-select v-model="form.overflows[$index].handleType" size="small" style="width: 100%">
                  <el-option label="排队" :value="1" />
                  <el-option label="溢出" :value="2" />
                  <el-option label="挂机" :value="3" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="排队方式" width="120">
              <template #default="{ row, $index }">
                <el-select v-model="form.overflows[$index].busyType" size="small" style="width: 100%">
                  <el-option label="先进先出" :value="1" />
                  <el-option label="vip" :value="2" />
                  <el-option label="自定义" :value="3" />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="70" align="center">
              <template #default="{ $index }">
                <el-button link type="danger" size="small" @click="form.overflows.splice($index, 1)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="技能组详情" width="1000px" @close="detailVisible = false">
      <div v-loading="detailLoading" v-if="detail">
        <el-descriptions :column="3" border size="small" class="detail-desc">
          <el-descriptions-item label="技能组id">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(detail.cts) }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ formatTime(detail.uts) }}</el-descriptions-item>
          <el-descriptions-item label="话后自动空闲">{{ detail.afterInterval || 5 }}s</el-descriptions-item>
          <el-descriptions-item label="外显号码">{{ detail.display || '-' }}</el-descriptions-item>
          <el-descriptions-item label="录音类型">{{ recordTypeMap[detail.recordType] || detail.recordType }}</el-descriptions-item>
          <el-descriptions-item label="转坐席播放内容">{{ detail.transferPlayName || detail.playContent || '转坐席音' }}</el-descriptions-item>
          <el-descriptions-item label="技能组名称">{{ detail.name }}</el-descriptions-item>
          <el-descriptions-item label="外呼超时">{{ detail.callTimeOut || 60 }}s</el-descriptions-item>
          <el-descriptions-item label="优先级">{{ detail.levelValue ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="播放工号">{{ detail.playAgent ? '播放' : '不播放' }}</el-descriptions-item>
          <el-descriptions-item label="策略类型">{{ detail.groupAgentStrategyPo?.strategyType || '内置策略' }}</el-descriptions-item>
          <el-descriptions-item label="最长空闲时长">{{ detail.maxReadyTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="主叫号码池">{{ (detail.callerDisplays || []).join(',') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="被叫号码池">{{ (detail.calledDisplays || []).join(',') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转坐席提示音">{{ detail.transferPlayName || '转坐席音' }}</el-descriptions-item>
          <el-descriptions-item label="排队位置播报">{{ detail.notifyPosition ? '开启' : '不开启' }}</el-descriptions-item>
          <el-descriptions-item label="坐席策略">{{ detail.groupAgentStrategyPo?.name || '内置策略' }}</el-descriptions-item>
          <el-descriptions-item label="坐席记忆">{{ detail.callMemory ? '开启' : '不开启' }}</el-descriptions-item>
          <el-descriptions-item label="技能组类型">{{ detail.groupType === 1 ? '普通技能组' : '外呼技能组' }}</el-descriptions-item>
          <el-descriptions-item label="排队音">{{ detail.queuePlayName || '排队音' }}</el-descriptions-item>
          <el-descriptions-item label="外呼技能组">{{ detail.groupType === 2 ? '开启' : '未开启' }}</el-descriptions-item>
          <el-descriptions-item label="服务评价">{{ detail.evaluate ? '开启' : '未开启' }}</el-descriptions-item>
          <el-descriptions-item label="话单回调">{{ detail.ext1 ? '开启' : '未开启' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <!-- 坐席列表 -->
        <el-card shadow="never" class="detail-card">
          <template #header><div class="card-header-row"><span>坐席列表</span><span class="tab-count">列表总数: {{ agentList.length }}</span></div></template>
          <el-table :data="agentList" size="small" max-height="300">
            <el-table-column label="坐席账号" width="140">
              <template #default="{ row }">{{ row.agentKey || row.agent?.agentKey || '' }}</template>
            </el-table-column>
            <el-table-column label="技能组id" width="100">
              <template #default>{{ detail.id }}</template>
            </el-table-column>
            <el-table-column label="坐席名称" width="120">
              <template #default="{ row }">{{ row.agent?.name || row.agentName || '' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag size="small" :type="row.agent?.status === 1 ? 'success' : 'info'">
                  {{ row.agent?.status === 1 ? '在线' : '离线' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 技能列表 -->
        <el-card shadow="never" class="detail-card">
          <template #header><div class="card-header-row"><span>技能列表</span><span class="tab-count">列表总数: {{ detail.skills?.length || 0 }}</span></div></template>
          <el-table :data="detail.skills || []" size="small" max-height="300">
            <el-table-column prop="skillId" label="技能ID" width="80" />
            <el-table-column prop="skillName" label="技能名称" width="120" />
            <el-table-column label="等级类型" width="100">
              <template #default="{ row }">{{ rankTypeMap[row.rankType] || row.rankType }}</template>
            </el-table-column>
            <el-table-column label="匹配规则" width="100">
              <template #default="{ row }">{{ row.matchType === 1 ? '低到高' : row.matchType === 2 ? '高到低' : row.matchType }}</template>
            </el-table-column>
            <el-table-column prop="shareValue" label="占用率" width="80" align="center" />
          </el-table>
        </el-card>

        <!-- 排队策略 -->
        <el-card shadow="never" class="detail-card">
          <template #header><div class="card-header-row"><span>排队策略</span><span class="tab-count">列表总数: {{ detail.groupOverflows?.length || 0 }}</span></div></template>
          <el-table :data="detail.groupOverflows || []" size="small" max-height="300">
            <el-table-column prop="id" label="溢出策略Id" width="100" />
            <el-table-column prop="name" label="策略名称" width="160" />
            <el-table-column label="排队类型" width="100">
              <template #default="{ row }">{{ handleTypeMap[row.handleType] || row.handleType }}</template>
            </el-table-column>
            <el-table-column label="排队方式" width="100">
              <template #default="{ row }">{{ busyTypeMap[row.busyType] || row.busyType }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      <el-empty v-else description="暂无详情数据" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGroupConfigList, getGroupConfigDetail, getGroupAgents, addGroupConfig, deleteGroupConfig } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', display: '', recordType: '双声道振铃录音',
  groupType: '普通技能组', level: 1, afterWork: 5, callTimeout: 60, evaluate: false,
  playAgent: 1, notifyPosition: 0, callMemory: 0, callback: false,
  strategyType: '内置策略', callerDisplay: '', calledDisplay: '', transferPlay: '',
  skills: [], overflows: []
})

const recordTypeMap = { 1: '单声道振铃录音', 2: '单声道接通录音', 3: '双声道振铃录音', 4: '双声道接通录音' }
const handleTypeMap = { 1: '排队', 2: '溢出', 3: '挂机' }
const busyTypeMap = { 1: '先进先出', 2: 'vip', 3: '自定义' }
const rankTypeMap = { 1: '等级', 2: '百分比', 3: '固定值' }

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)
const agentList = ref([])
const rules = { name: [{ required: true, message: '请输入技能组名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', display: '', recordType: '双声道振铃录音', groupType: '普通技能组', level: 1, afterWork: 5, callTimeout: 60, evaluate: false, playAgent: 1, notifyPosition: 0, callMemory: 0, callback: false, strategyType: '内置策略', callerDisplay: '', calledDisplay: '', transferPlay: '', skills: [], overflows: [] })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.name) query.name = searchForm.name
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getGroupConfigList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  agentList.value = []
  try {
    const [detailRes, agentRes] = await Promise.all([
      getGroupConfigDetail(row.id),
      getGroupAgents(row.id)
    ])
    if (detailRes.code === 0) detail.value = detailRes.data
    if (agentRes.code === 0) agentList.value = agentRes.data || []
  } catch { /* empty */ }
  finally { detailLoading.value = false }
}

const addSkill = () => { form.skills.push({ skillName: '', rankType: 1, matchType: 1 }) }
const addOverflow = () => { form.overflows.push({ name: '', handleType: 1, busyType: 1 }) }

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = async (row) => {
  isEdit.value = true
  dialogVisible.value = true
  submitLoading.value = true
  // 从详情接口加载完整数据
  try {
    const res = await getGroupConfigDetail(row.id)
    if (res.code === 0) {
      const d = res.data
      Object.assign(form, {
        id: d.id, name: d.name || '', display: d.display || '',
        recordType: d.recordType || '双声道振铃录音', groupType: d.groupType || '普通技能组',
        level: d.levelValue ?? 1, afterWork: d.afterInterval ?? 5,
        callTimeout: d.callTimeOut ?? 60, evaluate: d.evaluate ?? false,
        playAgent: d.playAgent ?? 1, notifyPosition: d.notifyPosition ?? 0,
        callMemory: d.callMemory ?? 0, callback: d.ext1 ? true : false,
        strategyType: d.groupAgentStrategyPo?.strategyType || '内置策略',
        callerDisplay: (d.callerDisplays || []).join(','),
        calledDisplay: (d.calledDisplays || []).join(','),
        transferPlay: d.transferPlayName || '',
        skills: (d.skills || []).map(s => ({...s})),
        overflows: (d.groupOverflows || []).map(o => ({...o}))
      })
    }
  } catch { /* empty */ }
  finally { submitLoading.value = false }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    await addGroupConfig({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除技能组 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteGroupConfig(row.id)
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
.detail-card { margin-bottom: 16px; }
.card-header-row { display: flex; justify-content: space-between; align-items: center; }
.tab-count { color: #909399; font-size: 12px; font-weight: normal; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
