<template>
  <div class="page-card">
    <div class="page-header">
      <h2>任务监控</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="任务id">
        <el-input v-model="searchForm.taskId" placeholder="任务id" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width:100%">
      <el-table-column prop="taskId" label="任务id" width="100" />
      <el-table-column prop="taskName" label="任务名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="transferName" label="转接名称" min-width="120" show-overflow-tooltip />
      <el-table-column label="统计时间" width="180">
        <template #default="{ row }">{{ formatTime(row.statsTime) }}</template>
      </el-table-column>
      <el-table-column label="任务状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="listTotal" label="名单数量" width="100" align="center" />
      <el-table-column prop="dialedCount" label="已拨数" width="90" align="center" />
      <el-table-column prop="answeredCount" label="接听数" width="90" align="center" />
      <el-table-column label="接通率" width="100" align="center">
        <template #default="{ row }">
          <span :style="{ color: rateColor(row.answerRate) }">{{ row.answerRate || '0%' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="pendingCount" label="待接听数" width="100" align="center" />
      <el-table-column prop="inCall" label="通话中" width="80" align="center" />
      <el-table-column prop="queueCount" label="排队数" width="80" align="center" />
      <el-table-column prop="signedInAgents" label="签入坐席" width="100" align="center" />
      <el-table-column prop="idleAgents" label="空闲坐席" width="100" align="center" />
      <el-table-column prop="busyAgents" label="忙碌坐席" width="100" align="center" />
      <el-table-column prop="afterCallAgents" label="话后坐席" width="100" align="center" />
      <el-table-column prop="abandonedCount" label="弃线数" width="90" align="center" />
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
import { getTaskStatList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ taskId: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const statusMap = { 1: '拨打中', 2: '已暂停', 3: '已完成', 4: '已停止' }
const statusTypeMap = { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger' }
const statusLabel = (s) => statusMap[s] || '-'
const statusType = (s) => statusTypeMap[s] || 'info'

const rateColor = (rate) => {
  if (!rate) return '#909399'
  const v = parseFloat(rate)
  if (v >= 80) return '#67c23a'
  if (v >= 40) return '#e6a23c'
  return '#f56c6c'
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.taskId) query.taskId = searchForm.taskId
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getTaskStatList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.taskId = ''; pagination.currentPage = 1; loadData() }
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
