<template>
  <div class="page-card">
    <div class="page-header">
      <h2>话单推送</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="callId">
        <el-input v-model="searchForm.callId" placeholder="callId" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width:130px">
          <el-option label="推送成功" :value="2" />
          <el-option label="推送失败" :value="1" />
        </el-select>
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
        <el-table-column prop="callId" label="callId" min-width="180" show-overflow-tooltip />
        <el-table-column label="结束时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.cts) }}</template>
        </el-table-column>
        <el-table-column label="推送时间" min-width="160">
          <template #default="{ row }">{{ formatTime(row.uts) }}</template>
        </el-table-column>
        <el-table-column prop="cdrNotifyUrl" label="推送地址" min-width="260" show-overflow-tooltip />
        <el-table-column prop="pushTimes" label="推送次数" min-width="80" align="center" />
        <el-table-column label="加密" min-width="70" align="center">
          <template #default="{ row }">{{ row.content ? '不加密' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="pushResponse" label="返回值" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" min-width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 2 ? 'success' : 'danger'">
              {{ row.status === 2 ? '推送成功' : '推送失败' }}
            </el-tag>
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
import { getPushLogList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ callId: '', status: null, timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.callId) query.callId = searchForm.callId
    if (searchForm.status) query.status = searchForm.status
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.startTime = Math.floor(searchForm.timeRange[0] / 1000)
      query.endTime = Math.floor(searchForm.timeRange[1] / 1000)
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getPushLogList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { Object.assign(searchForm, { callId: '', status: null, timeRange: null }); pagination.currentPage = 1; loadData() }
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
