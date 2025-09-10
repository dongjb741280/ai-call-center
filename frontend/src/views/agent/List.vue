<template>
  <div class="page-card">
    <div class="page-header">
      <h2>坐席管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加坐席
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="坐席工号">
        <el-input v-model="searchForm.agentKey" placeholder="请输入坐席工号" clearable />
      </el-form-item>
      <el-form-item label="坐席姓名">
        <el-input v-model="searchForm.agentName" placeholder="请输入坐席姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
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

    <!-- 坐席表格 -->
    <el-table :data="agentList" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="agentKey" label="坐席工号" width="120" />
      <el-table-column prop="agentName" label="坐席姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="groupName" label="技能组" width="120" />
      <el-table-column prop="agentState" label="坐席状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStateType(row.agentState)">
            {{ getStateText(row.agentState) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="callId" label="通话ID" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button 
            v-if="row.agentState === 'READY'" 
            type="warning" size="small" 
            @click="handleNotReady(row)"
          >
            忙碌
          </el-button>
          <el-button 
            v-if="row.agentState === 'NOT_READY'" 
            type="success" size="small" 
            @click="handleReady(row)"
          >
            空闲
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">
            删除
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

    <!-- 添加/编辑坐席对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑坐席' : '添加坐席'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="agentFormRef"
        :model="agentForm"
        :rules="agentRules"
        label-width="100px"
      >
        <el-form-item label="坐席工号" prop="agentKey">
          <el-input v-model="agentForm.agentKey" placeholder="请输入坐席工号" />
        </el-form-item>
        <el-form-item label="坐席姓名" prop="agentName">
          <el-input v-model="agentForm.agentName" placeholder="请输入坐席姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="agentForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="技能组" prop="groupId">
          <el-select v-model="agentForm.groupId" placeholder="请选择技能组" style="width: 100%">
            <el-option
              v-for="group in groupList"
              :key="group.id"
              :label="group.groupName"
              :value="group.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="agentForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAgentList, addAgent, updateAgent, deleteAgent, agentReady, agentNotReady } from '@/api/agent'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const agentFormRef = ref()

// 搜索表单
const searchForm = reactive({
  agentKey: '',
  agentName: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 坐席列表
const agentList = ref([])
const groupList = ref([])

// 坐席表单
const agentForm = reactive({
  id: null,
  agentKey: '',
  agentName: '',
  phone: '',
  groupId: null,
  status: 1
})

// 表单验证规则
const agentRules = {
  agentKey: [
    { required: true, message: '请输入坐席工号', trigger: 'blur' }
  ],
  agentName: [
    { required: true, message: '请输入坐席姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  groupId: [
    { required: true, message: '请选择技能组', trigger: 'change' }
  ]
}

// 获取状态类型
const getStateType = (state) => {
  const stateMap = {
    'READY': 'success',
    'NOT_READY': 'warning',
    'TALKING': 'primary',
    'LOGOUT': 'info'
  }
  return stateMap[state] || 'info'
}

// 获取状态文本
const getStateText = (state) => {
  const stateMap = {
    'READY': '空闲',
    'NOT_READY': '忙碌',
    'TALKING': '通话中',
    'LOGOUT': '离线'
  }
  return stateMap[state] || '未知'
}

// 加载坐席列表
const loadAgentList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const response = await getAgentList(params)
    agentList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载坐席列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载技能组列表
const loadGroupList = async () => {
  try {
    const response = await getGroupList()
    groupList.value = response.data || []
  } catch (error) {
    ElMessage.error('加载技能组列表失败：' + error.message)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadAgentList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    agentKey: '',
    agentName: '',
    status: ''
  })
  handleSearch()
}

// 添加坐席
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑坐席
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(agentForm, {
    id: row.id,
    agentKey: row.agentKey,
    agentName: row.agentName,
    phone: row.phone,
    groupId: row.groupId,
    status: row.status
  })
}

// 删除坐席
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该坐席吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteAgent(row.id)
    ElMessage.success('删除成功')
    loadAgentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 坐席空闲
const handleReady = async (row) => {
  try {
    await agentReady()
    ElMessage.success('操作成功')
    loadAgentList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 坐席忙碌
const handleNotReady = async (row) => {
  try {
    await agentNotReady()
    ElMessage.success('操作成功')
    loadAgentList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!agentFormRef.value) return
  
  try {
    const valid = await agentFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateAgent(agentForm)
      ElMessage.success('更新成功')
    } else {
      await addAgent(agentForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadAgentList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(agentForm, {
    id: null,
    agentKey: '',
    agentName: '',
    phone: '',
    groupId: null,
    status: 1
  })
  agentFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadAgentList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadAgentList()
}

onMounted(() => {
  loadAgentList()
  loadGroupList()
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


