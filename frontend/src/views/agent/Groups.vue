<template>
  <div class="page-card">
    <div class="page-header">
      <h2>技能组管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加技能组
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="技能组名称">
        <el-input v-model="searchForm.groupName" placeholder="请输入技能组名称" clearable />
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

    <!-- 技能组表格 -->
    <el-table :data="groupList" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="groupName" label="技能组名称" width="200" />
      <el-table-column prop="groupCode" label="技能组编码" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="agentCount" label="坐席数量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleManageAgents(row)">
            管理坐席
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

    <!-- 添加/编辑技能组对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑技能组' : '添加技能组'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="groupFormRef"
        :model="groupForm"
        :rules="groupRules"
        label-width="100px"
      >
        <el-form-item label="技能组名称" prop="groupName">
          <el-input v-model="groupForm.groupName" placeholder="请输入技能组名称" />
        </el-form-item>
        <el-form-item label="技能组编码" prop="groupCode">
          <el-input v-model="groupForm.groupCode" placeholder="请输入技能组编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="groupForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入技能组描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="groupForm.status">
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

    <!-- 管理坐席对话框 -->
    <el-dialog
      v-model="agentDialogVisible"
      title="管理坐席"
      width="800px"
      @close="handleAgentDialogClose"
    >
      <div class="agent-management">
        <div class="agent-list">
          <h4>技能组坐席</h4>
          <el-table :data="groupAgents" style="width: 100%">
            <el-table-column prop="agentKey" label="坐席工号" width="120" />
            <el-table-column prop="agentName" label="坐席姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="handleRemoveAgent(row)">
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="available-agents">
          <h4>可用坐席</h4>
          <el-table :data="availableAgents" style="width: 100%">
            <el-table-column prop="agentKey" label="坐席工号" width="120" />
            <el-table-column prop="agentName" label="坐席姓名" width="120" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleAddAgent(row)">
                  添加
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGroupList, addGroup, updateGroup, deleteGroup } from '@/api/agent'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const agentDialogVisible = ref(false)
const isEdit = ref(false)
const groupFormRef = ref()

// 搜索表单
const searchForm = reactive({
  groupName: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 技能组列表
const groupList = ref([])
const groupAgents = ref([])
const availableAgents = ref([])
const currentGroupId = ref(null)

// 技能组表单
const groupForm = reactive({
  id: null,
  groupName: '',
  groupCode: '',
  description: '',
  status: 1
})

// 表单验证规则
const groupRules = {
  groupName: [
    { required: true, message: '请输入技能组名称', trigger: 'blur' }
  ],
  groupCode: [
    { required: true, message: '请输入技能组编码', trigger: 'blur' }
  ]
}

// 加载技能组列表
const loadGroupList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const response = await getGroupList(params)
    groupList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载技能组列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadGroupList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    groupName: ''
  })
  handleSearch()
}

// 添加技能组
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑技能组
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(groupForm, {
    id: row.id,
    groupName: row.groupName,
    groupCode: row.groupCode,
    description: row.description,
    status: row.status
  })
}

// 删除技能组
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该技能组吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteGroup(row.id)
    ElMessage.success('删除成功')
    loadGroupList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 管理坐席
const handleManageAgents = async (row) => {
  currentGroupId.value = row.id
  agentDialogVisible.value = true
  
  // 加载技能组坐席
  loadGroupAgents()
  // 加载可用坐席
  loadAvailableAgents()
}

// 加载技能组坐席
const loadGroupAgents = async () => {
  // 模拟数据
  groupAgents.value = [
    {
      id: 1,
      agentKey: 'A001',
      agentName: '张三',
      phone: '138****1234'
    },
    {
      id: 2,
      agentKey: 'A002',
      agentName: '李四',
      phone: '139****5678'
    }
  ]
}

// 加载可用坐席
const loadAvailableAgents = async () => {
  // 模拟数据
  availableAgents.value = [
    {
      id: 3,
      agentKey: 'A003',
      agentName: '王五',
      phone: '137****9012'
    },
    {
      id: 4,
      agentKey: 'A004',
      agentName: '赵六',
      phone: '136****3456'
    }
  ]
}

// 添加坐席到技能组
const handleAddAgent = async (row) => {
  try {
    // 这里调用添加坐席到技能组的API
    ElMessage.success('添加成功')
    loadGroupAgents()
    loadAvailableAgents()
  } catch (error) {
    ElMessage.error('添加失败：' + error.message)
  }
}

// 从技能组移除坐席
const handleRemoveAgent = async (row) => {
  try {
    await ElMessageBox.confirm('确定要移除该坐席吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里调用移除坐席的API
    ElMessage.success('移除成功')
    loadGroupAgents()
    loadAvailableAgents()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败：' + error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!groupFormRef.value) return
  
  try {
    const valid = await groupFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateGroup(groupForm)
      ElMessage.success('更新成功')
    } else {
      await addGroup(groupForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadGroupList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(groupForm, {
    id: null,
    groupName: '',
    groupCode: '',
    description: '',
    status: 1
  })
  groupFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

const handleAgentDialogClose = () => {
  groupAgents.value = []
  availableAgents.value = []
  currentGroupId.value = null
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadGroupList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadGroupList()
}

onMounted(() => {
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

.agent-management {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.agent-list,
.available-agents {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
}

.agent-list h4,
.available-agents h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 响应式 */
@media (max-width: 768px) {
  .agent-management {
    grid-template-columns: 1fr;
  }
}
</style>


