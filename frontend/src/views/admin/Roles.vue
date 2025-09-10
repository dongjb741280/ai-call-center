<template>
  <div class="page-card">
    <div class="page-header">
      <h2>角色管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加角色
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="角色名称">
        <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
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

    <!-- 角色表格 -->
    <el-table :data="roleList" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="150" />
      <el-table-column prop="roleCode" label="角色编码" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleBindMenus(row)">
            绑定菜单
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

    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '添加角色'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="roleForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 绑定菜单对话框 -->
    <el-dialog
      v-model="menuDialogVisible"
      title="绑定菜单"
      width="800px"
      @close="handleMenuDialogClose"
    >
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        :props="treeProps"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenus"
        check-strictly
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindSubmit" :loading="bindLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, getRoleMenus, bindRoleMenus, getMenuList } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const bindLoading = ref(false)
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const isEdit = ref(false)
const roleFormRef = ref()
const menuTreeRef = ref()

// 搜索表单
const searchForm = reactive({
  roleName: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 角色列表
const roleList = ref([])

// 角色表单
const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  description: ''
})

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ]
}

// 菜单树数据
const menuTreeData = ref([])
const checkedMenus = ref([])
const currentRoleId = ref(null)

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 加载角色列表
const loadRoleList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.currentPage,
      size: pagination.pageSize,
      ...searchForm
    }
    const response = await getRoleList(params)
    roleList.value = response.data.records || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载角色列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载菜单树
const loadMenuTree = async () => {
  try {
    const response = await getMenuList()
    menuTreeData.value = response.data || []
  } catch (error) {
    ElMessage.error('加载菜单列表失败：' + error.message)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadRoleList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    roleName: ''
  })
  handleSearch()
}

// 添加角色
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑角色
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(roleForm, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description
  })
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 绑定菜单
const handleBindMenus = async (row) => {
  currentRoleId.value = row.id
  menuDialogVisible.value = true
  
  // 加载菜单树
  await loadMenuTree()
  
  // 加载当前角色的菜单
  try {
    const response = await getRoleMenus(row.id)
    checkedMenus.value = response.data.menuIds || []
  } catch (error) {
    ElMessage.error('加载角色菜单失败：' + error.message)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!roleFormRef.value) return
  
  try {
    const valid = await roleFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateRole(roleForm)
      ElMessage.success('更新成功')
    } else {
      await addRole(roleForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadRoleList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 提交菜单绑定
const handleBindSubmit = async () => {
  try {
    bindLoading.value = true
    
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]
    
    await bindRoleMenus({
      roleId: currentRoleId.value,
      menuIds
    })
    
    ElMessage.success('绑定成功')
    menuDialogVisible.value = false
  } catch (error) {
    ElMessage.error('绑定失败：' + error.message)
  } finally {
    bindLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(roleForm, {
    id: null,
    roleName: '',
    roleCode: '',
    description: ''
  })
  roleFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

const handleMenuDialogClose = () => {
  checkedMenus.value = []
  currentRoleId.value = null
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadRoleList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadRoleList()
}

onMounted(() => {
  loadRoleList()
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


