<template>
  <div class="page-card">
    <div class="page-header">
      <h2>IVR报表</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="callId">
        <el-input v-model="searchForm.callId" placeholder="callId" clearable />
      </el-form-item>
      <el-form-item label="被叫号码">
        <el-input v-model="searchForm.called" placeholder="被叫号码" clearable />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="searchForm.timeRange"
          type="datetimerange"
          range-separator="—"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
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
        <el-table-column prop="callId" label="callId" width="200" show-overflow-tooltip />
        <el-table-column prop="ivrId" label="ivrId" width="80" align="center" />
        <el-table-column prop="taskId" label="任务id" width="80" align="center" />
        <el-table-column prop="callerDisplay" label="外显号码" width="130" show-overflow-tooltip />
        <el-table-column prop="called" label="被叫号码" width="130" />
        <el-table-column prop="numberLocation" label="归属地" min-width="110" show-overflow-tooltip />
        <el-table-column label="拨打时间" width="170">
          <template #default="{ row }">{{ formatTime(row.callTime) }}</template>
        </el-table-column>
        <el-table-column label="应答时间" width="170">
          <template #default="{ row }">{{ formatTime(row.answerTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="170">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="呼叫结果" width="100" align="center">
          <template #default="{ row }">{{ row.answerFlag === 1 ? '接通' : '未接通' }}</template>
        </el-table-column>
        <el-table-column label="通话时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.talkTime) }}</template>
        </el-table-column>
        <el-table-column label="挂机" width="80" align="center">
          <template #default="{ row }">{{ row.hangupDir === 1 ? '主叫' : row.hangupDir === 2 ? '被叫' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="hangupCode" label="挂机原因" width="150" show-overflow-tooltip />
        <el-table-column prop="ext2" label="sip状态" width="80" align="center" />
        <el-table-column label="录音时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.recordTime) }}</template>
        </el-table-column>
        <el-table-column prop="record" label="录音" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="detailVisible" title="IVR通话详情" width="900px" top="5vh" @close="detailVisible = false">
      <template v-if="detailRow">
        <el-descriptions :column="3" border size="small" style="margin-bottom: 20px">
          <el-descriptions-item label="Call ID">{{ detailRow.callId }}</el-descriptions-item>
          <el-descriptions-item label="IVR ID">{{ detailRow.ivrId }}</el-descriptions-item>
          <el-descriptions-item label="任务ID">{{ detailRow.taskId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="外显号码">{{ detailRow.callerDisplay || '-' }}</el-descriptions-item>
          <el-descriptions-item label="被叫号码">{{ detailRow.called || '-' }}</el-descriptions-item>
          <el-descriptions-item label="归属地">{{ detailRow.numberLocation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="拨打时间">{{ formatTime(detailRow.callTime) }}</el-descriptions-item>
          <el-descriptions-item label="应答时间">{{ formatTime(detailRow.answerTime) }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ formatTime(detailRow.endTime) }}</el-descriptions-item>
          <el-descriptions-item label="呼叫结果">
            <el-tag size="small" :type="detailRow.answerFlag === 1 ? 'success' : 'danger'">
              {{ detailRow.answerFlag === 1 ? '接通' : '未接通' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="通话时长">{{ fmtDur(detailRow.talkTime) }}</el-descriptions-item>
          <el-descriptions-item label="挂机方">
            {{ detailRow.hangupDir === 1 ? '主叫' : detailRow.hangupDir === 2 ? '被叫' : '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <div class="detail-section-title">IVR 流程节点</div>
          <el-table :data="ivrFlowList" v-loading="flowLoading" size="small" max-height="260">
            <el-table-column label="序号" width="60" align="center">
              <template #default="{ $index }">{{ $index + 1 }}</template>
            </el-table-column>
            <el-table-column prop="nodeName" label="节点名称" min-width="120" show-overflow-tooltip />
            <el-table-column prop="nodeType" label="节点类型" width="100" align="center">
              <template #default="{ row: r }">
                <el-tag size="small">{{ r.nodeType || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="进入时间" min-width="150">
              <template #default="{ row: r }">{{ formatTime(r.enterTime) }}</template>
            </el-table-column>
            <el-table-column label="离开时间" min-width="150">
              <template #default="{ row: r }">{{ formatTime(r.leaveTime) }}</template>
            </el-table-column>
            <el-table-column label="停留时长" width="100" align="center">
              <template #default="{ row: r }">{{ fmtDur(r.duration) }}</template>
            </el-table-column>
            <el-table-column prop="result" label="执行结果" min-width="120" show-overflow-tooltip>
              <template #default="{ row: r }">{{ r.result || '-' }}</template>
            </el-table-column>
          </el-table>
          <div v-if="!flowLoading && !ivrFlowList.length" class="empty-hint">暂无 IVR 流程数据</div>
        </div>

        <div class="detail-section" style="margin-top: 20px">
          <div class="detail-section-title">通话明细</div>
          <el-table :data="callDetailList" v-loading="detailLoading" size="small" max-height="260">
            <el-table-column label="序号" width="60" align="center">
              <template #default="{ $index }">{{ $index + 1 }}</template>
            </el-table-column>
            <el-table-column label="事件时间" min-width="150">
              <template #default="{ row: r }">{{ formatTime(r.eventTime) }}</template>
            </el-table-column>
            <el-table-column prop="eventType" label="事件类型" width="120" align="center">
              <template #default="{ row: r }">
                <el-tag size="small" :type="eventTagType(r.eventType)">{{ r.eventType || '-' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="eventDesc" label="事件描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="detail" label="详情" min-width="160" show-overflow-tooltip>
              <template #default="{ row: r }">{{ r.detail || r.eventValue || '-' }}</template>
            </el-table-column>
          </el-table>
          <div v-if="!detailLoading && !callDetailList.length" class="empty-hint">暂无通话明细</div>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <div class="pagination">
      <el-pagination v-model:current-page="pagination.currentPage" v-model:page-size="pagination.pageSize"
        :page-sizes="[10,20,50,100]" :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCallMonitorList, getIvrFlowList, getCallDetailList } from '@/api/config'

const loading = ref(false)
const flowLoading = ref(false)
const detailLoading = ref(false)
const detailVisible = ref(false)
const detailRow = ref(null)
const ivrFlowList = ref([])
const callDetailList = ref([])
const list = ref([])
const searchForm = reactive({ callId: '', called: '', timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

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
const eventTagType = (type) => {
  if (!type) return 'info'
  if (type.includes('answer') || type.includes('ANSWER')) return 'success'
  if (type.includes('hangup') || type.includes('HANGUP')) return 'danger'
  if (type.includes('bridge') || type.includes('BRIDGE')) return 'primary'
  return 'info'
}

const handleDetail = async (row) => {
  detailRow.value = row
  detailVisible.value = true
  ivrFlowList.value = []
  callDetailList.value = []
  flowLoading.value = true
  detailLoading.value = true
  try {
    const [flowRes, detailRes] = await Promise.all([
      getIvrFlowList(row.callId),
      getCallDetailList(row.callId)
    ])
    if (flowRes.code === 0) ivrFlowList.value = flowRes.data || []
    if (detailRes.code === 0) callDetailList.value = detailRes.data || []
  } catch { /* empty */ }
  finally {
    flowLoading.value = false
    detailLoading.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.callId) query.callId = searchForm.callId
    if (searchForm.called) query.called = searchForm.called
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

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { Object.assign(searchForm, { callId: '', called: '', timeRange: null }); pagination.currentPage = 1; loadData() }
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
.detail-section { margin-top: 10px; }
.detail-section-title { font-size: 14px; font-weight: 600; color: #303133; padding-left: 10px; border-left: 3px solid #409eff; margin-bottom: 12px; line-height: 1.3; }
.empty-hint { text-align: center; color: #c0c4cc; font-size: 13px; padding: 30px 0; }
</style>
