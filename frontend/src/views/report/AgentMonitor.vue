<template>
  <div class="page-card">
    <div class="page-header">
      <h2>坐席监控</h2>
      <div class="header-right">
        <span class="refresh-time">{{ refreshTime }}</span>
        <el-button :icon="Refresh" circle size="small" @click="loadData" :loading="loading" />
      </div>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="坐席">
        <el-input v-model="searchForm.agentKey" placeholder="坐席账号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <div class="stats-bar" v-if="list.length > 0">
      <span class="stat-item">坐席总数 <strong>{{ list.length }}</strong></span>
      <span class="stat-sep">|</span>
      <span class="stat-item ready">空闲 <strong>{{ list.filter(a => a.state === 'READY').length }}</strong></span>
      <span class="stat-item notready">忙碌 <strong>{{ list.filter(a => a.state === 'NOT_READY').length }}</strong></span>
      <span class="stat-item outcall">通话中 <strong>{{ list.filter(a => a.state === 'OUT_CALL').length }}</strong></span>
      <span class="stat-item logout">离线 <strong>{{ list.filter(a => a.state === 'LOGOUT').length }}</strong></span>
    </div>

    <div class="card-grid" v-loading="loading">
      <div class="agent-card" v-for="agent in list" :key="agent.agentKey" :class="cardClass(agent.state)">
        <div class="card-top">
          <span class="card-agent-key">{{ agent.agentKey }}</span>
          <span class="card-type" :class="agent.loginType === 1 ? 'webrtc' : 'sip'">
            {{ agent.loginType === 1 ? 'webRTC' : 'SIP' }}
          </span>
        </div>
        <div class="card-state">
          <span class="state-dot" :class="stateDot(agent.state)"></span>
          <span class="state-label">{{ agent.state || 'LOGOUT' }}</span>
        </div>
        <div class="card-time">{{ formatTime(agent.stateTime) }}</div>
        <div class="card-name">{{ agent.agentName }}</div>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无坐席数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getAgentMonitorList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ agentKey: '' })
const refreshTime = ref('')
let timer = null

const cardClass = (s) => {
  const m = { READY: 'card-ready', NOT_READY: 'card-notready', LOGIN: 'card-login', LOGOUT: 'card-logout', OUT_CALL: 'card-outcall' }
  return m[s] || 'card-default'
}
const stateDot = (s) => {
  const m = { READY: 'dot-ready', NOT_READY: 'dot-notready', LOGIN: 'dot-login', LOGOUT: 'dot-logout', OUT_CALL: 'dot-outcall' }
  return m[s] || 'dot-default'
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.agentKey) params.agentKey = searchForm.agentKey
    const res = await getAgentMonitorList(params)
    list.value = res.code === 0 ? (res.data || []) : []
    refreshTime.value = new Date().toLocaleString('zh-CN', { hour12: false })
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => loadData()
const handleReset = () => { searchForm.agentKey = ''; loadData() }

onMounted(() => { loadData(); timer = setInterval(loadData, 10000) })
onUnmounted(() => { clearInterval(timer) })
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.header-right { display: flex; align-items: center; gap: 12px; }
.refresh-time { font-size: 13px; color: #909399; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }

.stats-bar { display: flex; align-items: center; gap: 16px; padding: 12px 20px; background: #fff; border-radius: 8px; border: 1px solid #ebeef5; margin-bottom: 16px; font-size: 14px; color: #606266; }
.stat-item strong { font-size: 18px; margin-left: 4px; }
.stat-item.ready strong { color: #67c23a; }
.stat-item.notready strong { color: #f56c6c; }
.stat-item.outcall strong { color: #e6a23c; }
.stat-item.logout strong { color: #909399; }
.stat-sep { color: #dcdfe6; }

.card-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 16px; min-height: 200px; }

.agent-card {
  padding: 16px 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background: #fff;
  transition: box-shadow 0.2s;
}
.agent-card:hover { box-shadow: 0 2px 12px rgba(0,0,0,0.08); }

.card-ready { border-left: 4px solid #67c23a; }
.card-notready { border-left: 4px solid #f56c6c; }
.card-login { border-left: 4px solid #409eff; }
.card-logout { border-left: 4px solid #909399; }
.card-outcall { border-left: 4px solid #e6a23c; }
.card-default { border-left: 4px solid #dcdfe6; }

.card-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.card-agent-key { font-size: 15px; font-weight: 600; color: #303133; }
.card-type { font-size: 12px; padding: 1px 8px; border-radius: 4px; color: #fff; }
.card-type.webrtc { background: #409eff; }
.card-type.sip { background: #909399; }

.card-state { display: flex; align-items: center; gap: 6px; margin-bottom: 8px; }
.state-dot { width: 8px; height: 8px; border-radius: 50%; display: inline-block; }
.dot-ready { background: #67c23a; box-shadow: 0 0 6px rgba(103,194,58,0.5); }
.dot-notready { background: #f56c6c; box-shadow: 0 0 6px rgba(245,108,108,0.5); }
.dot-login { background: #409eff; box-shadow: 0 0 6px rgba(64,158,255,0.5); }
.dot-logout { background: #909399; }
.dot-outcall { background: #e6a23c; box-shadow: 0 0 6px rgba(230,162,60,0.5); }
.dot-default { background: #c0c4cc; }
.state-label { font-size: 14px; color: #606266; font-weight: 500; }

.card-time { font-size: 12px; color: #909399; margin-bottom: 4px; }
.card-name { font-size: 13px; color: #606266; }
</style>
