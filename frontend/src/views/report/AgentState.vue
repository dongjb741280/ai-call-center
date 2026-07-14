<template>
  <div class="page-card">
    <div class="page-header">
      <h2>坐席状态详单</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="坐席">
        <el-input v-model="searchForm.agentKey" placeholder="坐席编号" clearable @keyup.enter="handleSearch" />
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

    <el-table :data="list" v-loading="loading" style="width:100%" size="small">
      <el-table-column prop="agentKey" label="坐席账号" width="140" />
      <el-table-column prop="agentName" label="坐席名称" width="120" />
      <el-table-column label="当前状态" width="140">
        <template #default="{ row }">
          <el-tag size="small" :type="stateTag(row.state)">{{ row.state || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前时间" width="180">
        <template #default="{ row }">{{ formatTime(row.stateTime) }}</template>
      </el-table-column>
      <el-table-column label="之前状态" width="140">
        <template #default="{ row }">
          <el-tag size="small" :type="stateTag(row.beforeState)">{{ row.beforeState || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="之前时间" width="180">
        <template #default="{ row }">{{ formatTime(row.beforeTime) }}</template>
      </el-table-column>
      <el-table-column label="持续时间" width="100" align="center">
        <template #default="{ row }">{{ formatDuration(row.duration) }}</template>
      </el-table-column>
      <el-table-column prop="callId" label="callId" width="200" show-overflow-tooltip />
      <el-table-column label="登录类型" width="100" align="center">
        <template #default="{ row }">{{ loginTypeLabel(row.loginType) }}</template>
      </el-table-column>
      <el-table-column label="工作类型" width="100" align="center">
        <template #default="{ row }">{{ workTypeLabel(row.workType) }}</template>
      </el-table-column>
      <el-table-column prop="groupId" label="技能组id" width="120" />
      <el-table-column prop="host" label="cti地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="remoteAddress" label="坐席端地址" width="150" show-overflow-tooltip />
    </el-table>

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
import { getAgentStateList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ agentKey: '', timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const stateTag = (s) => {
  const map = { READY: 'success', LOGIN: '', LOGOUT: 'info', NOT_READY: 'warning', OUT_CALL: 'danger', AFTER: 'warning', OUT_CALLER_RING: 'danger' }
  return map[s] || 'info'
}

const loginTypeLabel = (t) => ({ 1: 'webrtc', 2: 'sip' }[t] || '-')
const workTypeLabel = (t) => ({ 1: '普通坐席', 2: '班长坐席' }[t] || '-')

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const formatDuration = (sec) => {
  if (!sec && sec !== 0) return '-'
  const h = Math.floor(sec / 3600)
  const m = Math.floor((sec % 3600) / 60)
  const s = sec % 60
  if (h > 0) return `${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
  return `${String(m).padStart(2,'0')}:${String(s).padStart(2,'0')}`
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.agentKey) query.agentKey = searchForm.agentKey
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.start = Math.floor(searchForm.timeRange[0] / 1000)
      query.end = Math.floor(searchForm.timeRange[1] / 1000)
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getAgentStateList(params)
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
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
