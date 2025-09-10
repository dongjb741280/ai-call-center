<template>
  <div class="page-card">
    <div class="page-header">
      <h2>通话记录</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleExport">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="通话ID">
        <el-input v-model="searchForm.callId" placeholder="请输入通话ID" clearable />
      </el-form-item>
      <el-form-item label="坐席工号">
        <el-input v-model="searchForm.agentKey" placeholder="请输入坐席工号" clearable />
      </el-form-item>
      <el-form-item label="电话号码">
        <el-input v-model="searchForm.phoneNumber" placeholder="请输入电话号码" clearable />
      </el-form-item>
      <el-form-item label="通话类型">
        <el-select v-model="searchForm.callType" placeholder="请选择通话类型" clearable>
          <el-option label="呼入" value="INBOUND" />
          <el-option label="呼出" value="OUTBOUND" />
        </el-select>
      </el-form-item>
      <el-form-item label="通话状态">
        <el-select v-model="searchForm.callStatus" placeholder="请选择通话状态" clearable>
          <el-option label="已接通" value="ANSWERED" />
          <el-option label="未接通" value="NO_ANSWER" />
          <el-option label="忙线" value="BUSY" />
          <el-option label="失败" value="FAILED" />
        </el-select>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="searchForm.dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
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

    <!-- 通话记录表格 -->
    <el-table :data="callLogs" v-loading="loading" style="width: 100%">
      <el-table-column prop="callId" label="通话ID" width="120" />
      <el-table-column prop="agentKey" label="坐席工号" width="120" />
      <el-table-column prop="agentName" label="坐席姓名" width="120" />
      <el-table-column prop="phoneNumber" label="电话号码" width="130" />
      <el-table-column prop="callType" label="通话类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.callType === 'INBOUND' ? 'success' : 'primary'">
            {{ row.callType === 'INBOUND' ? '呼入' : '呼出' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="callStatus" label="通话状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.callStatus)">
            {{ getStatusText(row.callStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="通话时长" width="100">
        <template #default="{ row }">
          {{ formatDuration(row.duration) }}
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleView(row)">
            查看详情
          </el-button>
          <el-button type="success" size="small" @click="handlePlayback(row)">
            播放录音
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 通话详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="通话详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="通话ID">{{ currentCall.callId }}</el-descriptions-item>
        <el-descriptions-item label="坐席工号">{{ currentCall.agentKey }}</el-descriptions-item>
        <el-descriptions-item label="坐席姓名">{{ currentCall.agentName }}</el-descriptions-item>
        <el-descriptions-item label="电话号码">{{ currentCall.phoneNumber }}</el-descriptions-item>
        <el-descriptions-item label="通话类型">
          <el-tag :type="currentCall.callType === 'INBOUND' ? 'success' : 'primary'">
            {{ currentCall.callType === 'INBOUND' ? '呼入' : '呼出' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="通话状态">
          <el-tag :type="getStatusType(currentCall.callStatus)">
            {{ getStatusText(currentCall.callStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="通话时长">{{ formatDuration(currentCall.duration) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentCall.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentCall.endTime }}</el-descriptions-item>
        <el-descriptions-item label="录音文件" :span="2">
          <el-button v-if="currentCall.recordingFile" type="primary" size="small" @click="handlePlayback(currentCall)">
            播放录音
          </el-button>
          <span v-else>无录音</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 录音播放对话框 -->
    <el-dialog
      v-model="playbackDialogVisible"
      title="录音播放"
      width="600px"
    >
      <div class="playback-container">
        <audio
          ref="audioPlayer"
          controls
          style="width: 100%"
          :src="currentRecording"
        >
          您的浏览器不支持音频播放
        </audio>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCallLogs, getCallDetail } from '@/api/call'

const loading = ref(false)
const detailDialogVisible = ref(false)
const playbackDialogVisible = ref(false)
const audioPlayer = ref()
const currentCall = ref({})
const currentRecording = ref('')

// 搜索表单
const searchForm = reactive({
  callId: '',
  agentKey: '',
  phoneNumber: '',
  callType: '',
  callStatus: '',
  dateRange: []
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 通话记录列表
const callLogs = ref([])

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'ANSWERED': 'success',
    'NO_ANSWER': 'warning',
    'BUSY': 'danger',
    'FAILED': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ANSWERED': '已接通',
    'NO_ANSWER': '未接通',
    'BUSY': '忙线',
    'FAILED': '失败'
  }
  return statusMap[status] || '未知'
}

// 格式化通话时长
const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 加载通话记录
const loadCallLogs = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    
    // 处理时间范围
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startTime = searchForm.dateRange[0]
      params.endTime = searchForm.dateRange[1]
    }
    
    const response = await getCallLogs(params)
    callLogs.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载通话记录失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadCallLogs()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    callId: '',
    agentKey: '',
    phoneNumber: '',
    callType: '',
    callStatus: '',
    dateRange: []
  })
  handleSearch()
}

// 刷新
const handleRefresh = () => {
  loadCallLogs()
}

// 导出
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

// 查看详情
const handleView = async (row) => {
  try {
    const response = await getCallDetail(row.callId)
    currentCall.value = response.data || row
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取通话详情失败：' + error.message)
  }
}

// 播放录音
const handlePlayback = (row) => {
  if (!row.recordingFile) {
    ElMessage.warning('该通话没有录音文件')
    return
  }
  
  currentRecording.value = row.recordingFile
  playbackDialogVisible.value = true
  
  // 等待DOM更新后播放
  setTimeout(() => {
    if (audioPlayer.value) {
      audioPlayer.value.play()
    }
  }, 100)
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadCallLogs()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadCallLogs()
}

onMounted(() => {
  loadCallLogs()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.playback-container {
  padding: 20px;
  text-align: center;
}
</style>


