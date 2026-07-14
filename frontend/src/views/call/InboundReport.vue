<template>
  <div class="page-card">
    <div class="page-header">
      <h2>呼入报表</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="来电号码">
        <el-input v-model="searchForm.caller" placeholder="来电号码" clearable />
      </el-form-item>
      <el-form-item label="被叫号码">
        <el-input v-model="searchForm.called" placeholder="被叫号码" clearable />
      </el-form-item>
      <el-form-item label="坐席">
        <el-input v-model="searchForm.agentKey" placeholder="坐席编号" clearable />
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
        <el-table-column prop="caller" label="来电号码" width="130" />
        <el-table-column prop="callerDisplay" label="接入号" width="130" show-overflow-tooltip />
        <el-table-column prop="called" label="被叫号码" width="130" />
        <el-table-column prop="numberLocation" label="归属地" min-width="120" show-overflow-tooltip />
        <el-table-column prop="groupId" label="技能组" width="80" align="center" />
        <el-table-column prop="agentName" label="坐席" width="100" show-overflow-tooltip />
        <el-table-column label="拨打时间" width="170">
          <template #default="{ row }">{{ formatTime(row.callTime) }}</template>
        </el-table-column>
        <el-table-column label="转坐席时间" width="170">
          <template #default="{ row }">{{ formatTime(row.fristQueueTime) }}</template>
        </el-table-column>
        <el-table-column label="应答时间" width="170">
          <template #default="{ row }">{{ formatTime(row.answerTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" width="170">
          <template #default="{ row }">{{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="通话时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.talkTime) }}</template>
        </el-table-column>
        <el-table-column label="呼叫结果" width="90" align="center">
          <template #default="{ row }">{{ row.answerFlag === 1 ? '已接听' : '未接听' }}</template>
        </el-table-column>
        <el-table-column label="排队开始" width="170">
          <template #default="{ row }">{{ formatTime(row.queueStartTime) }}</template>
        </el-table-column>
        <el-table-column label="排队结束" width="170">
          <template #default="{ row }">{{ formatTime(row.queueEndTime) }}</template>
        </el-table-column>
        <el-table-column label="挂机" width="80" align="center">
          <template #default="{ row }">{{ row.hangupDir === 1 ? '主叫' : row.hangupDir === 2 ? '被叫' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="hangupCode" label="挂机原因" width="90" align="center" />
        <el-table-column prop="ext1" label="服务评价" width="90" align="center" />
        <el-table-column label="录音时长" width="90" align="center">
          <template #default="{ row }">{{ fmtDur(row.recordTime) }}</template>
        </el-table-column>
        <el-table-column prop="record" label="录音" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template #default>
            <el-button size="small" type="primary" link>详情</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCallMonitorList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ caller: '', called: '', agentKey: '', timeRange: null })
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

const loadData = async () => {
  loading.value = true
  try {
    const query = { direction: 'inbound' }
    if (searchForm.caller) query.caller = searchForm.caller
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

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { Object.assign(searchForm, { caller: '', called: '', agentKey: '', timeRange: null }); pagination.currentPage = 1; loadData() }
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
</style>
