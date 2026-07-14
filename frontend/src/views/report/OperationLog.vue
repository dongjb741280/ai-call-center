<template>
  <div class="page-card">
    <div class="page-header">
      <h2>操作日志</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="账号">
        <el-input v-model="searchForm.account" placeholder="账号" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="用户IP">
        <el-input v-model="searchForm.userIp" placeholder="用户IP" clearable @keyup.enter="handleSearch" />
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
      <el-table-column prop="account" label="账号" width="90" />
      <el-table-column label="操作时间" width="180">
        <template #default="{ row }">{{ formatTime(row.opTime) }}</template>
      </el-table-column>
      <el-table-column prop="enterpriseId" label="企业ID" width="90" align="center" />
      <el-table-column prop="requestUrl" label="请求地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="duration" label="耗时" width="80" align="center">
        <template #default="{ row }">{{ row.duration }}ms</template>
      </el-table-column>
      <el-table-column prop="userIp" label="用户IP" width="140" />
      <el-table-column prop="region" label="地域" min-width="160" show-overflow-tooltip />
      <el-table-column prop="isp" label="运营商" width="100" />
      <el-table-column prop="serverAddr" label="服务地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="requestParams" label="请求参数" min-width="150" show-overflow-tooltip />
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
import { getAdminLogList } from '@/api/config'

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ account: '', userIp: '', timeRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.account) query.account = searchForm.account
    if (searchForm.userIp) query.userIp = searchForm.userIp
    if (searchForm.timeRange && searchForm.timeRange.length === 2) {
      query.startTime = Math.floor(searchForm.timeRange[0] / 1000)
      query.endTime = Math.floor(searchForm.timeRange[1] / 1000)
    }
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize,
      query: JSON.stringify(query) }
    const res = await getAdminLogList(params)
    if (res.code === 0) { list.value = res.data?.list || []; pagination.total = res.data?.total || 0 }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.account = ''; searchForm.userIp = ''; searchForm.timeRange = null; pagination.currentPage = 1; loadData() }
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
