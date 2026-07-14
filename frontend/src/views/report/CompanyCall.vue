<template>
  <div class="page-card">
    <div class="page-header">
      <h2>企业话务统计</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="企业ID">
        <el-input v-model="searchForm.companyId" placeholder="企业ID" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="searchForm.timeRange"
          type="daterange"
          range-separator="—"
          start-placeholder="请选择开始时间"
          end-placeholder="请选择结束时间"
          format="YYYY-MM-DD"
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
        <el-table-column prop="companyId" label="企业ID" width="90" align="center" />
        <el-table-column label="统计时间" width="140">
          <template #default="{ row }">{{ formatDay(row.statTime) }}</template>
        </el-table-column>
        <el-table-column prop="agentTotal" label="坐席总数" width="90" align="center" />
        <el-table-column prop="agentUsed" label="坐席使用数" width="100" align="center" />
        <el-table-column prop="callTotal" label="呼叫总数" width="90" align="center" />
        <el-table-column prop="planTotal" label="计划总额" width="90" align="center" />
        <el-table-column prop="outboundTotal" label="外呼总数" width="90" align="center" />
        <el-table-column prop="outboundAnswered" label="外呼接通数" width="100" align="center" />
        <el-table-column prop="taskOutbound" label="任务外呼" width="90" align="center" />
        <el-table-column prop="agentOutboundAnswered" label="坐席外呼接通数" width="120" align="center" />
        <el-table-column prop="inboundTotal" label="呼入总数" width="90" align="center" />
        <el-table-column prop="inboundAssigned" label="呼入分配数" width="100" align="center" />
        <el-table-column prop="inboundAnswered" label="呼入应答数" width="100" align="center" />
        <el-table-column prop="ivrCalls" label="IVR通话数" width="100" align="center" />
        <el-table-column prop="abandonedCnt" label="弃线数" width="80" align="center" />
        <el-table-column prop="recordingCnt" label="录音数" width="80" align="center" />
        <el-table-column label="录音大小" width="110" align="center">
          <template #default="{ row }">{{ fmtSize(row.recordingSize) }}</template>
        </el-table-column>
        <el-table-column label="通话总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.callDuration) }}</template>
        </el-table-column>
        <el-table-column label="呼入总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.inboundDuration) }}</template>
        </el-table-column>
        <el-table-column label="外呼总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.outboundDuration) }}</template>
        </el-table-column>
        <el-table-column prop="queueCnt" label="排队数" width="80" align="center" />
        <el-table-column label="排队总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.queueDuration) }}</template>
        </el-table-column>
        <el-table-column prop="loginCnt" label="登录次数" width="90" align="center" />
        <el-table-column label="登录总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.loginDuration) }}</template>
        </el-table-column>
        <el-table-column prop="idleCnt" label="空闲次数" width="90" align="center" />
        <el-table-column label="空闲总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.idleDuration) }}</template>
        </el-table-column>
        <el-table-column prop="busyCnt" label="忙碌次数" width="90" align="center" />
        <el-table-column label="忙碌总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.busyDuration) }}</template>
        </el-table-column>
        <el-table-column prop="afterCallCnt" label="话后次数" width="90" align="center" />
        <el-table-column label="话后时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.afterCallDuration) }}</template>
        </el-table-column>
        <el-table-column label="自定义忙碌总时间" width="140" align="center">
          <template #default="{ row }">{{ fmtDur(row.customBusyDuration) }}</template>
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
import { getCompanyStatList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ companyId: '', timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const formatDay = (ts) => {
  if (!ts) return '-'
  const d = new Date(ts * 1000)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}
const fmtDur = (sec) => {
  if (!sec && sec !== 0) return '00:00:00'
  const h = Math.floor(sec / 3600)
  const m = Math.floor((sec % 3600) / 60)
  const s = sec % 60
  return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
}
const fmtSize = (bytes) => {
  if (!bytes) return '0'
  if (bytes >= 1048576) return (bytes / 1048576).toFixed(2) + ' MB'
  if (bytes >= 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return bytes + ' B'
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.companyId) query.companyId = searchForm.companyId
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.startTime = Math.floor(searchForm.timeRange[0] / 1000)
      query.endTime = Math.floor(searchForm.timeRange[1] / 1000) + 86399
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getCompanyStatList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.companyId = ''; searchForm.timeRange = null; pagination.currentPage = 1; loadData() }
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
