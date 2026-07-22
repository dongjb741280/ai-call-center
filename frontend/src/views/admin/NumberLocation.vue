<template>
  <div class="page-card">
    <div class="page-header">
      <h2>号码归属地</h2>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="号码">
        <el-input v-model="searchForm.numPrefix" placeholder="输入号码查询" clearable @keyup.enter="handleSearch" />
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

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="id" width="80" />
      <el-table-column label="修改时间" min-width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column prop="numPrefix" label="号码" min-width="100" />
      <el-table-column label="运营商" min-width="100">
        <template #default="{ row }">{{ operatorMap[row.operator] || row.operator }}</template>
      </el-table-column>
      <el-table-column prop="numCode" label="区号" min-width="90" />
      <el-table-column prop="numProvince" label="省" min-width="120" show-overflow-tooltip />
      <el-table-column prop="numCity" label="市" min-width="120" show-overflow-tooltip />
    </el-table>

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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPhoneAreaList } from '@/api/config'

const operatorMap = {
  '1000': '移动', '2000': '联通', '3000': '电信',
  '4000': '铁通', '5000': '广电', '6000': '国际'
}

const loading = ref(false)
const list = ref([])
const searchForm = reactive({ numPrefix: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize }
    if (searchForm.numPrefix) params.query = JSON.stringify({ numPrefix: searchForm.numPrefix })
    else params.query = '{}'
    const res = await getPhoneAreaList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.numPrefix = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

onMounted(loadData)
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
</style>
