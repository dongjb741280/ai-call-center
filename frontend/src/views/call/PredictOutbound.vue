<template>
  <div class="page-card">
    <div class="page-header">
      <h2>预测外呼</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="任务id">
        <el-input v-model="searchForm.taskId" placeholder="任务id" clearable />
      </el-form-item>
      <el-form-item label="callId">
        <el-input v-model="searchForm.callId" placeholder="callId" clearable />
      </el-form-item>
      <el-form-item label="被叫号码">
        <el-input v-model="searchForm.called" placeholder="被叫号码" clearable />
      </el-form-item>
      <el-form-item label="呼叫结果">
        <el-input v-model="searchForm.callResult" placeholder="呼叫结果" clearable />
      </el-form-item>
      <el-form-item label="坐席">
        <el-input v-model="searchForm.agentKey" placeholder="坐席" clearable />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="searchForm.timeRange"
          type="datetimerange"
          range-separator="—"
          start-placeholder="请选择开始时间"
          end-placeholder="请选择结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="x"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-wrapper">
      <el-table :data="list" v-loading="loading" size="small" style="width:100%">
        <el-table-column prop="taskId" label="任务id" width="90" align="center" />
        <el-table-column prop="callId" label="callId" width="200" show-overflow-tooltip />
        <el-table-column prop="callerDisplay" label="外显号码" width="140" show-overflow-tooltip />
        <el-table-column prop="called" label="被叫号码" width="130" />
        <el-table-column prop="numberLocation" label="归属地" min-width="100" show-overflow-tooltip />
        <el-table-column prop="uuid1" label="uuid1" width="120" show-overflow-tooltip />
        <el-table-column prop="ivrId" label="ivrId" width="80" align="center" />
        <el-table-column prop="agentKey" label="坐席" width="100" show-overflow-tooltip />
        <el-table-column prop="groupId" label="技能组" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.groupId || '-' }}</template>
        </el-table-column>
        <el-table-column label="拨打时间" width="170">
          <template #default="{ row }">{{ formatTime(row.callTime) }}</template>
        </el-table-column>
        <el-table-column label="用户接听时间" width="170">
          <template #default="{ row }">{{ formatTime(row.answerTime) }}</template>
        </el-table-column>
        <el-table-column label="坐席接听时间" width="170">
          <template #default="{ row }">{{ formatTime(row.bridgeTime || row.answerTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="170">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="呼叫结果" width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.answerFlag === 1 ? '接通' : '用户接通坐席未接通' }}</template>
        </el-table-column>
        <el-table-column label="通话时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.talkTime) }}</template>
        </el-table-column>
        <el-table-column label="排队开始" width="170">
          <template #default="{ row }">{{ formatTime(row.queueStartTime) }}</template>
        </el-table-column>
        <el-table-column label="排队结束" width="170">
          <template #default="{ row }">{{ formatTime(row.queueEndTime) }}</template>
        </el-table-column>
        <el-table-column label="排队时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.waitTime) }}</template>
        </el-table-column>
        <el-table-column label="挂机" width="80" align="center">
          <template #default="{ row }">{{ hangupLabel(row.hangupDir) }}</template>
        </el-table-column>
        <el-table-column prop="hangupCode" label="挂机原因" width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ row.hangupCode || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ext2" label="sip状态" width="80" align="center" />
        <el-table-column prop="record" label="录音" width="100" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :page-sizes="[10,20,50,100]" :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <el-dialog v-model="detailVisible" title="话单详情" width="800px" @close="detailVisible = false">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本" name="basic">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="callId" :span="2">{{ detail.callId }}</el-descriptions-item>
            <el-descriptions-item label="任务id">{{ detail.taskId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="通话类型">{{ detail.callType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="外显号码">{{ detail.callerDisplay || '-' }}</el-descriptions-item>
            <el-descriptions-item label="主叫号码">{{ detail.caller || '-' }}</el-descriptions-item>
            <el-descriptions-item label="被叫号码">{{ detail.called || '-' }}</el-descriptions-item>
            <el-descriptions-item label="归属地">{{ detail.numberLocation || '-' }}</el-descriptions-item>
            <el-descriptions-item label="坐席">{{ detail.agentKey || '-' }}</el-descriptions-item>
            <el-descriptions-item label="坐席名称">{{ detail.agentName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="技能组">{{ detail.groupId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="ivrId">{{ detail.ivrId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="uuid1">{{ detail.uuid1 || '-' }}</el-descriptions-item>
            <el-descriptions-item label="拨打时间">{{ formatTime(detail.callTime) }}</el-descriptions-item>
            <el-descriptions-item label="应答时间">{{ formatTime(detail.answerTime) }}</el-descriptions-item>
            <el-descriptions-item label="结束时间">{{ formatTime(detail.endTime) }}</el-descriptions-item>
            <el-descriptions-item label="通话时长">{{ fmtDur(detail.talkTime) }}</el-descriptions-item>
            <el-descriptions-item label="排队开始">{{ formatTime(detail.queueStartTime) }}</el-descriptions-item>
            <el-descriptions-item label="排队结束">{{ formatTime(detail.queueEndTime) }}</el-descriptions-item>
            <el-descriptions-item label="排队时长">{{ fmtDur(detail.waitTime) }}</el-descriptions-item>
            <el-descriptions-item label="呼叫结果">{{ callResult(detail) }}</el-descriptions-item>
            <el-descriptions-item label="挂机">{{ hangupLabel(detail.hangupDir) }}</el-descriptions-item>
            <el-descriptions-item label="挂机原因">{{ detail.hangupCode || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="呼叫详情" name="detail">
          <div class="detail-device" v-loading="deviceLoading">
            <div class="device-card" v-for="dev in deviceList" :key="dev.deviceId">
              <div class="device-header">
                <span class="device-label">deviceId: {{ dev.deviceId }}</span>
                <span class="device-type-tag">{{ dev.deviceType === 1 ? '坐席' : '用户' }}</span>
              </div>
              <el-descriptions :column="3" border size="small">
                <el-descriptions-item label="拨打时间">{{ formatTime(dev.callTime) }}</el-descriptions-item>
                <el-descriptions-item label="振铃时间">{{ formatTime(dev.ringStartTime) }}</el-descriptions-item>
                <el-descriptions-item label="应答时间">{{ formatTime(dev.answerTime) }}</el-descriptions-item>
                <el-descriptions-item label="结束时间">{{ formatTime(dev.endTime) }}</el-descriptions-item>
                <el-descriptions-item label="通话时长">{{ fmtDur(dev.talkTime) }}</el-descriptions-item>
                <el-descriptions-item label="主叫号码">{{ dev.caller || '-' }}</el-descriptions-item>
                <el-descriptions-item label="被叫号码">{{ dev.called || '-' }}</el-descriptions-item>
                <el-descriptions-item label="外显号码">{{ dev.display || '-' }}</el-descriptions-item>
                <el-descriptions-item label="坐席">{{ dev.agentKey || '-' }}</el-descriptions-item>
                <el-descriptions-item label="设备类型">{{ dev.deviceType === 1 ? '坐席' : '用户' }}</el-descriptions-item>
                <el-descriptions-item label="通话类型">{{ dev.cdrType === 1 ? '外呼' : '呼入' }}</el-descriptions-item>
                <el-descriptions-item label="channel">{{ dev.channelName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="sip协议">{{ dev.sipProtocol || '-' }}</el-descriptions-item>
                <el-descriptions-item label="挂机原因">{{ dev.hangupCause || '-' }}</el-descriptions-item>
                <el-descriptions-item label="录音时长">{{ fmtDur(dev.recordTime) }}</el-descriptions-item>
                <el-descriptions-item label="录音">{{ dev.record || '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>
            <el-empty v-if="!deviceLoading && deviceList.length === 0" description="暂无设备数据" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="呼叫流程" name="flow">
          <el-table :data="detailList" v-loading="detailLoading" size="small" style="width:100%">
            <el-table-column label="开始时间" width="170">
              <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="结束时间" width="170">
              <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
            </el-table-column>
            <el-table-column prop="detailIndex" label="序号" width="70" align="center" />
            <el-table-column label="路由类型" width="100" align="center">
              <template #default="{ row }">{{ transferTypeLabel(row.transferType) }}</template>
            </el-table-column>
            <el-table-column prop="reason" label="Reason" min-width="120" show-overflow-tooltip />
          </el-table>
          <el-empty v-if="!detailLoading && detailList.length === 0" description="暂无呼叫流程数据" />
        </el-tab-pane>
        <el-tab-pane label="IVR流程" name="ivr">
          <el-table :data="ivrFlowList" v-loading="ivrFlowLoading" size="small" style="width:100%">
            <el-table-column prop="ivrId" label="ivrId" width="80" align="center" />
            <el-table-column prop="nodeId" label="节点id" width="100" />
            <el-table-column prop="nodeType" label="节点类型" width="100" />
            <el-table-column prop="nodeName" label="节点名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="keyPress" label="keyPress" width="90" align="center" />
            <el-table-column label="开始时间" width="170">
              <template #default="{ row }">{{ formatTime(row.startTime) }}</template>
            </el-table-column>
            <el-table-column label="结束时间" width="170">
              <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!ivrFlowLoading && ivrFlowList.length === 0" description="暂无IVR流程数据" />
        </el-tab-pane>
        <el-tab-pane label="对话流程" name="dialog">
          <el-empty description="暂无对话流程数据" />
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCallMonitorList, getCallDeviceList, getCallDetailList, getIvrFlowList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ taskId: '', callId: '', called: '', callResult: '', agentKey: '', timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const detailVisible = ref(false)
const activeTab = ref('basic')
const detail = reactive({})
const deviceList = ref([])
const deviceLoading = ref(false)
const detailList = ref([])
const detailLoading = ref(false)
const ivrFlowList = ref([])
const ivrFlowLoading = ref(false)

const hangupLabel = (d) => ({ 1: '主叫挂断', 2: '被叫挂断', 3: '系统挂机' }[d] || '-')
const transferTypeLabel = (t) => ({ 1: '路由', 2: '桥接', 3: '转接', 4: '咨询', 5: '会议' }[t] || t)
const callResult = (r) => r.answerFlag === 1 ? '接通' : '用户接通坐席未接通'

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}
const fmtDur = (sec) => {
  if (!sec) return '00:00'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.taskId) query.taskId = searchForm.taskId
    if (searchForm.callId) query.callId = searchForm.callId
    if (searchForm.called) query.called = searchForm.called
    if (searchForm.agentKey) query.agentKey = searchForm.agentKey
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.start = Math.floor(searchForm.timeRange[0] / 1000)
      query.end = Math.floor(searchForm.timeRange[1] / 1000)
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getCallMonitorList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleDetail = async (row) => {
  Object.assign(detail, row)
  activeTab.value = 'basic'
  detailVisible.value = true
  deviceLoading.value = true
  detailLoading.value = true
  ivrFlowLoading.value = true
  try {
    const [devRes, dtlRes, ivrRes] = await Promise.all([
      getCallDeviceList(row.callId),
      getCallDetailList(row.callId),
      getIvrFlowList(row.callId)
    ])
    deviceList.value = devRes.code === 0 ? (devRes.data || []) : []
    detailList.value = dtlRes.code === 0 ? (dtlRes.data || []) : []
    ivrFlowList.value = ivrRes.code === 0 ? (ivrRes.data || []) : []
  } catch { deviceList.value = []; detailList.value = []; ivrFlowList.value = [] }
  finally { deviceLoading.value = false; detailLoading.value = false; ivrFlowLoading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { Object.assign(searchForm, { taskId: '', callId: '', called: '', callResult: '', agentKey: '', timeRange: null }); pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.table-wrapper { overflow-x: auto; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.device-card { margin-bottom: 16px; border: 1px solid #ebeef5; border-radius: 8px; overflow: hidden; }
.device-header { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; background: #f5f7fa; border-bottom: 1px solid #ebeef5; }
.device-label { font-size: 14px; font-weight: 600; color: #303133; }
.device-type-tag { font-size: 12px; padding: 2px 10px; border-radius: 4px; color: #fff; background: #409eff; }
</style>
