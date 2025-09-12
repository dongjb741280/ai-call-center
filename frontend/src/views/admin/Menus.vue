<template>
  <div class="page-card">
    <div class="page-header">
      <h2>菜单管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加菜单
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="菜单名称">
        <el-input v-model="searchForm.name" placeholder="请输入菜单名称" clearable />
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

    <!-- 菜单表格 -->
    <el-table
      :data="menuList"
      v-loading="loading"
      style="width: 100%"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="菜单名称" width="200" />
      <el-table-column prop="path" label="路径" width="200" />
      <el-table-column prop="icon" label="图标" width="100">
        <template #default="{ row }">
          <el-icon v-if="row.icon">
            <component :is="row.icon" />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? 'primary' : 'success'">
            {{ row.type === 1 ? '菜单' : '按钮' }}
          </el-tag>
        </template>
      </el-table-column>
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
          <el-button type="success" size="small" @click="handleAddChild(row)">
            添加子菜单
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜单' : '添加菜单'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="100px"
      >
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuTreeData"
            :props="treeProps"
            placeholder="请选择父级菜单"
            check-strictly
            clearable
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单路径" prop="path">
          <el-input v-model="menuForm.path" placeholder="请输入菜单路径" />
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon">
          <el-input v-model="menuForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="type">
          <el-radio-group v-model="menuForm.type">
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="menuForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
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
import { getMenuList, addMenu, updateMenu, deleteMenu } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const menuFormRef = ref()

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 菜单列表
const menuList = ref([])
const menuTreeData = ref([])

// 菜单表单
const menuForm = reactive({
  id: null,
  parentId: null,
  name: '',
  path: '',
  icon: '',
  type: 1,
  sort: 0,
  status: 1
})

// 表单验证规则
const menuRules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入菜单路径', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择菜单类型', trigger: 'change' }
  ]
}

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'name',
  value: 'id'
}

// 加载菜单列表
const loadMenuList = async () => {
  loading.value = true
  try {
    const response = await getMenuList(searchForm)
    menuList.value = response.data || []
    
    // 构建树形数据
    buildMenuTree()
  } catch (error) {
    ElMessage.error('加载菜单列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 构建菜单树
const buildMenuTree = () => {
  const tree = []
  const map = {}
  
  // 创建映射
  menuList.value.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  
  // 构建树形结构
  menuList.value.forEach(item => {
    if (item.parentId && map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
    } else {
      tree.push(map[item.id])
    }
  })
  
  menuTreeData.value = tree
}

// 搜索
const handleSearch = () => {
  loadMenuList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: ''
  })
  loadMenuList()
}

// 添加菜单
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 添加子菜单
const handleAddChild = (row) => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
  menuForm.parentId = row.id
}

// 编辑菜单
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(menuForm, {
    id: row.id,
    parentId: row.parentId,
    name: row.name,
    path: row.path,
    icon: row.icon,
    type: row.type,
    sort: row.sort,
    status: row.status
  })
}

// 删除菜单
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadMenuList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!menuFormRef.value) return
  
  try {
    const valid = await menuFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateMenu(menuForm)
      ElMessage.success('更新成功')
    } else {
      await addMenu(menuForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadMenuList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(menuForm, {
    id: null,
    parentId: null,
    name: '',
    path: '',
    icon: '',
    type: 1,
    sort: 0,
    status: 1
  })
  menuFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  loadMenuList()
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
</style>


