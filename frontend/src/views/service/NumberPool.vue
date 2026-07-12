<template>
  <div class="page-card">
    <div class="page-header">
      <h2>号码池</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="号码池名称">
        <el-input v-model="searchForm.name" placeholder="请输入" clearable />
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
      <el-table-column prop="name" label="号码池名称" min-width="160" />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="180">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="号码策略" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small">{{ row.strategy || row.displayStrategy || '随机' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="归属地" width="100">
        <template #default="{ row }">{{ row.location || row.province || '-' }}</template>
      </el-table-column>
      <el-table-column label="号码数量" width="100" align="center">
        <template #default="{ row }">{{ row.phoneCount || row.count || 0 }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑号码池' : '新增号码池'" width="800px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="号码池名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入号码池名称" />
        </el-form-item>
        <el-form-item label="号码策略" prop="strategy">
          <el-select v-model="form.strategy" placeholder="请选择号码策略" style="width: 100%">
            <el-option label="随机" value="随机" />
            <el-option label="轮训" value="轮训" />
          </el-select>
        </el-form-item>
        <el-form-item label="号码池" prop="phoneIds">
          <div class="custom-transfer">
            <!-- Left Panel -->
            <div class="transfer-panel">
              <div class="transfer-header">
                <el-checkbox
                  v-model="leftAllChecked"
                  :indeterminate="leftIndeterminate"
                />
                <span class="panel-title">待选号码</span>
                <span class="panel-count">{{ leftChecked.length }}/{{ filteredLeftPhones.length }}</span>
              </div>
              <div class="transfer-search">
                <el-input
                  v-model="leftSearch"
                  placeholder="请输入号码搜索"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </div>
              <div class="transfer-list-wrapper">
                <el-scrollbar max-height="240px">
                  <div class="transfer-list">
                    <el-checkbox-group v-model="leftChecked">
                      <div v-for="p in filteredLeftPhones" :key="p.id" class="transfer-item">
                        <el-checkbox :label="p.id">{{ p.phone }}</el-checkbox>
                      </div>
                    </el-checkbox-group>
                    <div v-if="filteredLeftPhones.length === 0" class="empty-text">暂无数据</div>
                  </div>
                </el-scrollbar>
              </div>
            </div>

            <!-- Middle Buttons -->
            <div class="transfer-buttons">
              <el-button
                type="primary"
                class="transfer-btn"
                :disabled="rightChecked.length === 0"
                @click="moveToLeft"
              >
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
              <el-button
                type="primary"
                class="transfer-btn"
                :disabled="leftChecked.length === 0"
                @click="moveToRight"
              >
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>

            <!-- Right Panel -->
            <div class="transfer-panel">
              <div class="transfer-header">
                <el-checkbox
                  v-model="rightAllChecked"
                  :indeterminate="rightIndeterminate"
                />
                <span class="panel-title">已选号码</span>
                <span class="panel-count">{{ rightChecked.length }}/{{ filteredRightPhones.length }}</span>
              </div>
              <div class="transfer-search">
                <el-input
                  v-model="rightSearch"
                  placeholder="请输入号码搜索"
                  clearable
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
              </div>
              <div class="transfer-list-wrapper">
                <el-scrollbar max-height="240px">
                  <div class="transfer-list">
                    <el-checkbox-group v-model="rightChecked">
                      <div v-for="p in filteredRightPhones" :key="p.id" class="transfer-item">
                        <el-checkbox :label="p.id">{{ p.phone }}</el-checkbox>
                      </div>
                    </el-checkbox-group>
                    <div v-if="filteredRightPhones.length === 0" class="empty-text">暂无数据</div>
                  </div>
                </el-scrollbar>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDisplayList, addDisplay, updateDisplay, deleteDisplay, getPhoneList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const phoneList = ref([])
const searchForm = reactive({ name: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({ id: null, name: '', type: 2, phoneIds: [], strategy: '随机', location: '' })

// Custom transfer reactive states
const leftSearch = ref('')
const rightSearch = ref('')
const leftChecked = ref([])
const rightChecked = ref([])

const rules = {
  name: [{ required: true, message: '请输入号码池名称', trigger: 'blur' }],
  strategy: [{ required: true, message: '请选择号码策略', trigger: 'change' }],
  phoneIds: [{ required: true, message: '请选择号码', trigger: 'change' }]
}

// Computed source lists for panels
const filteredLeftPhones = computed(() => {
  const selectedSet = new Set(form.phoneIds)
  return phoneList.value.filter(p => !selectedSet.has(p.id) && (!leftSearch.value || p.phone.includes(leftSearch.value)))
})

const filteredRightPhones = computed(() => {
  const selectedSet = new Set(form.phoneIds)
  return phoneList.value.filter(p => selectedSet.has(p.id) && (!rightSearch.value || p.phone.includes(rightSearch.value)))
})

// Left panel checkbox computed properties
const leftAllChecked = computed({
  get() {
    const currentList = filteredLeftPhones.value
    if (currentList.length === 0) return false
    return currentList.every(p => leftChecked.value.includes(p.id))
  },
  set(val) {
    const currentList = filteredLeftPhones.value
    if (val) {
      const currentSet = new Set(leftChecked.value)
      currentList.forEach(p => currentSet.add(p.id))
      leftChecked.value = Array.from(currentSet)
    } else {
      const filterIds = new Set(currentList.map(p => p.id))
      leftChecked.value = leftChecked.value.filter(id => !filterIds.has(id))
    }
  }
})

const leftIndeterminate = computed(() => {
  const checkedCount = leftChecked.value.filter(id => filteredLeftPhones.value.some(p => p.id === id)).length
  return checkedCount > 0 && checkedCount < filteredLeftPhones.value.length
})

// Right panel checkbox computed properties
const rightAllChecked = computed({
  get() {
    const currentList = filteredRightPhones.value
    if (currentList.length === 0) return false
    return currentList.every(p => rightChecked.value.includes(p.id))
  },
  set(val) {
    const currentList = filteredRightPhones.value
    if (val) {
      const currentSet = new Set(rightChecked.value)
      currentList.forEach(p => currentSet.add(p.id))
      rightChecked.value = Array.from(currentSet)
    } else {
      const filterIds = new Set(currentList.map(p => p.id))
      rightChecked.value = rightChecked.value.filter(id => !filterIds.has(id))
    }
  }
})

const rightIndeterminate = computed(() => {
  const checkedCount = rightChecked.value.filter(id => filteredRightPhones.value.some(p => p.id === id)).length
  return checkedCount > 0 && checkedCount < filteredRightPhones.value.length
})

const moveToRight = () => {
  const newPhoneIds = [...form.phoneIds, ...leftChecked.value]
  form.phoneIds = Array.from(new Set(newPhoneIds))
  leftChecked.value = []
}

const moveToLeft = () => {
  const toRemove = new Set(rightChecked.value)
  form.phoneIds = form.phoneIds.filter(id => !toRemove.has(id))
  rightChecked.value = []
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', type: 2, phoneIds: [], strategy: '随机', location: '' })
  leftSearch.value = ''
  rightSearch.value = ''
  leftChecked.value = []
  rightChecked.value = []
}

const loadPhoneList = async () => {
  try {
    const res = await getPhoneList({ pageNum: 1, pageSize: 1000, query: '{}' })
    if (res.code === 0) phoneList.value = res.data?.list || []
  } catch { /* empty */ }
}

const loadData = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.name) query.name = searchForm.name
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize, query: JSON.stringify(query) }
    const res = await getDisplayList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); if (phoneList.value.length === 0) loadPhoneList(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  const ids = row.phoneIds || (row.companyPhoneList ? row.companyPhoneList.map(p => p.id) : [])
  Object.assign(form, {
    id: row.id, name: row.name || '', type: row.type ?? 2,
    phoneIds: ids, strategy: row.strategy || '随机',
    location: row.location || ''
  })
  leftSearch.value = ''
  rightSearch.value = ''
  leftChecked.value = []
  rightChecked.value = []
  if (phoneList.value.length === 0) loadPhoneList()
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) await updateDisplay(form.id, { ...form })
    else await addDisplay({ ...form })
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除号码池 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteDisplay(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }

/* Custom Transfer Styles */
.custom-transfer {
  display: flex;
  gap: 16px;
  align-items: center;
  width: 100%;
}
.transfer-panel {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  width: 260px;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.transfer-header {
  background-color: #f5f7fa;
  padding: 8px 12px;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  gap: 8px;
}
.panel-title {
  font-weight: 500;
  font-size: 14px;
  color: #303133;
  flex-grow: 1;
}
.panel-count {
  font-size: 12px;
  color: #909399;
}
.transfer-search {
  padding: 10px 12px 6px;
}
.transfer-list-wrapper {
  height: 240px;
  padding: 0 12px;
}
.transfer-list {
  padding: 4px 0;
}
.transfer-item {
  padding: 4px 0;
  display: flex;
  align-items: center;
}
.empty-text {
  text-align: center;
  color: #c0c4cc;
  font-size: 13px;
  padding-top: 40px;
}
.transfer-buttons {
  display: flex;
  flex-direction: row;
  gap: 8px;
  align-items: center;
  justify-content: center;
}
.transfer-btn {
  width: 36px;
  height: 36px;
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
  background-color: #a0cfff;
  border-color: #a0cfff;
  color: #fff;
}
.transfer-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}
.transfer-btn.is-disabled, .transfer-btn.is-disabled:hover {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
}
</style>

