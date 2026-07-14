<template>
  <div class="page-card">
    <div class="page-header">
      <h2>话务监控</h2>
      <div class="header-right">
        <span class="refresh-time">{{ refreshTime }}</span>
        <el-button :icon="Refresh" circle size="small" @click="loadData" :loading="loading" />
      </div>
    </div>

    <div class="stats-bar" v-if="list.length > 0">
      <span class="stat-item">通话总数 <strong>{{ list.length }}</strong></span>
      <span class="stat-sep">|</span>
      <span class="stat-item outbound">外呼 <strong>{{ list.filter(c => c.direction === 'OUTBOUND' || c.direction === 'outbound').length }}</strong></span>
      <span class="stat-item inbound">呼入 <strong>{{ list.filter(c => c.direction === 'INBOUND' || c.direction === 'inbound').length }}</strong></span>
    </div>

    <div class="card-grid" v-loading="loading">
      <div class="call-card" v-for="call in list" :key="call.callId" :class="cardClass(call)">
        <div class="card-top">
          <span class="card-direction">
            {{ (call.direction === 'OUTBOUND' || call.direction === 'outbound') ? '外呼' : '呼入' }}
          </span>
          <span class="card-callid">{{ call.callId }}</span>
        </div>
        <div class="card-numbers">
          <div class="caller-line">
            <span class="num-label">主叫</span>
            <span class="num-value">{{ call.caller || '-' }}</span>
          </div>
          <div class="called-line">
            <span class="num-label">被叫</span>
            <span class="num-value">{{ call.called || '-' }}</span>
          </div>
        </div>
        <div class="card-info">
          <div class="info-row">
            <span>坐席: {{ call.agentKey || '-' }}</span>
            <span>{{ formatTime(call.callTime) }}</span>
          </div>
          <div class="info-row" v-if="call.answerTime > 0">
            <span>应答: {{ formatTime(call.answerTime) }}</span>
            <span>通话 {{ fmtDur(call.talkTime) }}</span>
          </div>
        </div>
        <div class="card-follow" v-if="call.followData">
          {{ call.followData }}
        </div>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无通话数据" />
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getCallMonitorList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const refreshTime = ref('')
let timer = null

const cardClass = (c) => {
  if (c.answerTime > 0 && c.endTime === 0) return 'card-active'
  if (c.endTime > 0) return 'card-ended'
  return 'card-ringing'
}

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
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify({}) }
    const res = await getCallMonitorList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
    refreshTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

onMounted(() => { loadData(); timer = setInterval(loadData, 10000) })
onUnmounted(() => { clearInterval(timer) })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.header-right { display: flex; align-items: center; gap: 12px; }
.refresh-time { font-size: 13px; color: #909399; }

.stats-bar { display: flex; align-items: center; gap: 16px; padding: 12px 20px; background: #fff; border-radius: 8px; border: 1px solid #ebeef5; margin-bottom: 16px; font-size: 14px; color: #606266; }
.stat-item strong { font-size: 18px; margin-left: 4px; }
.stat-item.outbound strong { color: #409eff; }
.stat-item.inbound strong { color: #67c23a; }
.stat-sep { color: #dcdfe6; }

.card-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(340px, 1fr)); gap: 16px; min-height: 200px; }

.call-card { padding: 16px 20px; border-radius: 8px; background: #fff; border: 1px solid #ebeef5; transition: box-shadow 0.2s; }
.call-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.card-active { border-left: 4px solid #67c23a; }
.card-ringing { border-left: 4px solid #e6a23c; }
.card-ended { border-left: 4px solid #909399; }

.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.card-direction { font-size: 12px; padding: 1px 8px; border-radius: 4px; color: #fff; background: #409eff; }
.card-callid { font-size: 12px; color: #909399; font-family: monospace; }

.card-numbers { margin-bottom: 10px; }
.caller-line, .called-line { display: flex; gap: 8px; font-size: 13px; line-height: 1.8; }
.num-label { color: #909399; min-width: 32px; }
.num-value { color: #303133; font-weight: 500; }

.card-info { font-size: 12px; color: #909399; }
.info-row { display: flex; justify-content: space-between; margin-bottom: 2px; }
.card-follow { margin-top: 8px; padding-top: 8px; border-top: 1px dashed #ebeef5; font-size: 11px; color: #c0c4cc; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>
