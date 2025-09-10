<template>
  <div class="page-card">
    <div class="page-header">
      <h2>企业管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加企业
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="企业名称">
        <el-input v-model="searchForm.name" placeholder="请输入企业名称" clearable />
      </el-form-item>
      <el-form-item label="企业编码">
        <el-input v-model="searchForm.companyCode" placeholder="请输入企业编码" clearable />
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

    <!-- 企业表格 -->
    <el-table :data="companyList" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="企业名称" width="200" />
      <el-table-column prop="companyCode" label="企业编码" width="150" />
      <el-table-column prop="contact" label="联系人" width="120" />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="address" label="地址" min-width="200" />
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
          <el-button type="warning" size="small" @click="handleView(row)">
            查看详情
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

    <!-- 添加/编辑企业对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑企业' : '添加企业'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="companyFormRef"
        :model="companyForm"
        :rules="companyRules"
        label-width="100px"
      >
        <el-form-item label="企业名称" prop="name">
          <el-input v-model="companyForm.name" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="企业编码" prop="companyCode">
          <el-input v-model="companyForm.companyCode" placeholder="请输入企业编码" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="companyForm.contact" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="companyForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="companyForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="companyForm.address"
            type="textarea"
            :rows="3"
            placeholder="请输入地址"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="companyForm.status">
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

    <!-- 企业详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="企业详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="企业名称">{{ currentCompany.name }}</el-descriptions-item>
        <el-descriptions-item label="企业编码">{{ currentCompany.companyCode }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ currentCompany.contact }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentCompany.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentCompany.email }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentCompany.status === 1 ? 'success' : 'danger'">
            {{ currentCompany.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentCompany.address }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentCompany.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ currentCompany.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCompanyList, addCompany, updateCompany, deleteCompany } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const companyFormRef = ref()

// 搜索表单
const searchForm = reactive({
  name: '',
  companyCode: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 企业列表
const companyList = ref([])
const currentCompany = ref({})

// 企业表单
const companyForm = reactive({
  id: null,
  name: '',
  companyCode: '',
  contact: '',
  phone: '',
  email: '',
  address: '',
  status: 1
})

// 表单验证规则
const companyRules = {
  name: [
    { required: true, message: '请输入企业名称', trigger: 'blur' }
  ],
  companyCode: [
    { required: true, message: '请输入企业编码', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 加载企业列表
const loadCompanyList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const response = await getCompanyList(params)
    companyList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载企业列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadCompanyList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    companyCode: ''
  })
  handleSearch()
}

// 添加企业
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑企业
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(companyForm, {
    id: row.id,
    name: row.name,
    companyCode: row.companyCode,
    contact: row.contact,
    phone: row.phone,
    email: row.email,
    address: row.address,
    status: row.status
  })
}

// 查看详情
const handleView = (row) => {
  currentCompany.value = { ...row }
  detailDialogVisible.value = true
}

// 删除企业
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该企业吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteCompany(row.id)
    ElMessage.success('删除成功')
    loadCompanyList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!companyFormRef.value) return
  
  try {
    const valid = await companyFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateCompany(companyForm)
      ElMessage.success('更新成功')
    } else {
      await addCompany(companyForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadCompanyList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(companyForm, {
    id: null,
    name: '',
    companyCode: '',
    contact: '',
    phone: '',
    email: '',
    address: '',
    status: 1
  })
  companyFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadCompanyList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadCompanyList()
}

onMounted(() => {
  loadCompanyList()
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


