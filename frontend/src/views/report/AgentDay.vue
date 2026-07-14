<template>
  <div class="page-card">
    <div class="page-header">
      <h2>坐席日报统计</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="坐席">
        <el-input v-model="searchForm.agentKey" placeholder="坐席账号" clearable @keyup.enter="handleSearch" />
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
        <el-table-column label="统计时间" width="140">
          <template #default="{ row }">{{ formatDay(row.statTime) }}</template>
        </el-table-column>
        <el-table-column prop="agentKey" label="坐席账号" width="130" />
        <el-table-column prop="agentName" label="坐席名称" width="100" />
        <el-table-column label="登录总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.loginTime) }}</template>
        </el-table-column>
        <el-table-column prop="loginCnt" label="登录次数" width="90" align="center" />
        <el-table-column prop="logoutCnt" label="退出次数" width="90" align="center" />
        <el-table-column label="空闲总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.readyTime) }}</template>
        </el-table-column>
        <el-table-column prop="readyCnt" label="空闲次数" width="90" align="center" />
        <el-table-column label="忙碌总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.notReadyTime) }}</template>
        </el-table-column>
        <el-table-column prop="notReadyCnt" label="忙碌次数" width="90" align="center" />
        <el-table-column label="通话总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.talkTime) }}</template>
        </el-table-column>
        <el-table-column prop="talkCnt" label="通话次数" width="90" align="center" />
        <el-table-column label="呼入总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.callinTalkTime) }}</template>
        </el-table-column>
        <el-table-column prop="callinAnswerCnt" label="呼入接起数" width="100" align="center" />
        <el-table-column prop="callinCnt" label="呼入分配数" width="100" align="center" />
        <el-table-column label="外呼总时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.calloutTalkTime) }}</template>
        </el-table-column>
        <el-table-column prop="calloutCnt" label="外呼总数" width="90" align="center" />
        <el-table-column prop="calloutAnswerCnt" label="外呼接通数" width="100" align="center" />
        <el-table-column label="保持时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.holdTime) }}</template>
        </el-table-column>
        <el-table-column prop="holdCnt" label="保持次数" width="90" align="center" />
        <el-table-column label="会议时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.conferenceTime) }}</template>
        </el-table-column>
        <el-table-column prop="conferenceCnt" label="会议次数" width="90" align="center" />
        <el-table-column label="咨询时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.consultTime) }}</template>
        </el-table-column>
        <el-table-column prop="consultCnt" label="咨询次数" width="90" align="center" />
        <el-table-column label="被咨询时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.consultedTime) }}</template>
        </el-table-column>
        <el-table-column prop="consultedCnt" label="被咨询次数" width="100" align="center" />
        <el-table-column label="被转接时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.transferredTime) }}</template>
        </el-table-column>
        <el-table-column prop="transferredCnt" label="被转接次数" width="100" align="center" />
        <el-table-column prop="transferCnt" label="转接次数" width="90" align="center" />
        <el-table-column prop="call15sCnt" label="15秒通话数" width="110" align="center" />
        <el-table-column label="自定义忙碌" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.busyTime) }}</template>
        </el-table-column>
        <el-table-column label="话后时长" width="110" align="center">
          <template #default="{ row }">{{ fmtDur(row.afterTime) }}</template>
        </el-table-column>
        <el-table-column prop="afterCnt" label="话后次数" width="90" align="center" />
        <el-table-column label="服务评价" width="80" align="center">
          <template #default="{ row }">
            {{ (row.ratingVerySatisfied||0) + (row.ratingSatisfied||0) + (row.ratingNormal||0) + (row.ratingDissatisfied||0) + (row.ratingVeryDissatisfied||0) }}
          </template>
        </el-table-column>
        <el-table-column prop="ratingVerySatisfied" label="非常满意" width="90" align="center" />
        <el-table-column prop="ratingSatisfied" label="满意" width="70" align="center" />
        <el-table-column prop="ratingNormal" label="一般" width="70" align="center" />
        <el-table-column prop="ratingDissatisfied" label="不满意" width="80" align="center" />
        <el-table-column prop="ratingVeryDissatisfied" label="非常不满意" width="100" align="center" />
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
import { getAgentDayStatList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ agentKey: '', timeRange: null })
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

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.agentKey) query.agentKey = searchForm.agentKey
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.startTime = Math.floor(searchForm.timeRange[0] / 1000)
      query.endTime = Math.floor(searchForm.timeRange[1] / 1000) + 86399
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getAgentDayStatList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.agentKey = ''; searchForm.timeRange = null; pagination.currentPage = 1; loadData() }
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
