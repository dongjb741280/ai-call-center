<template>
  <div class="page-card">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        添加用户
      </el-button>
    </div>

    <!-- 搜索表单 -->
    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="min-width: 120px">
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

    <!-- 用户表格 -->
    <el-table :data="userList" v-loading="loading" style="width: 100%">
      <el-table-column prop="id" label="id" width="80" />
      <el-table-column prop="username" label="账号" width="140" />
      <el-table-column prop="nickname" label="昵称" width="140" />
      <el-table-column prop="phone" label="电话" width="160" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column prop="updateTime" label="修改时间" width="180" />
      <el-table-column prop="companyName" label="企业名称" width="160" />
      <el-table-column prop="roleName" label="角色" width="240" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleResetPassword(row)">
            重置密码
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

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="700px"
      @close="handleDialogClose"
      class="user-dialog"
    >
      <div class="user-form-container">
        <el-form
          ref="userFormRef"
          :model="userForm"
          :rules="userRules"
          label-width="100px"
          class="user-form"
        >
          <el-form-item label="企业名称" prop="companyId" required>
            <el-select v-model="userForm.companyId" placeholder="请选择企业" style="width: 100%">
              <el-option
                v-for="company in companyList"
                :key="company.id"
                :label="company.name"
                :value="company.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="账号" prop="username" required>
            <el-input v-model="userForm.username" placeholder="请输入账号" />
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="userForm.passwd"
              type="password"
              placeholder="请输入密码"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          
          <el-form-item label="电话" prop="phone">
            <el-input v-model="userForm.phone" placeholder="请输入电话" />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          
          <el-form-item label="头像" prop="avatar">
            <div style="display: flex; align-items: center; gap: 12px;">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-button type="primary">上传图片</el-button>
              </el-upload>
              <el-image
                v-if="userForm.avatar"
                :src="userForm.avatar"
                style="width: 48px; height: 48px; border-radius: 8px;"
                fit="cover"
              />
            </div>
          </el-form-item>
          
          <el-form-item label="状态" prop="status">
            <el-switch
              v-model="userForm.status"
              :active-value="1"
              :inactive-value="0"
              active-text="启用"
              inactive-text="禁用"
            />
          </el-form-item>
          
          <el-form-item label="角色" prop="roleIds">
            <el-select
              v-model="userForm.roleIds"
              multiple
              placeholder="请选择角色"
              style="width: 100%"
            >
              <el-option
                v-for="role in roleList"
                :key="role.id"
                :label="role.roleName"
                :value="role.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
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
import { getUserList, addUser, updateUser, deleteUser, getCompanyList, getRoleList, uploadUserAvatar } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const userFormRef = ref()

// 搜索表单
const searchForm = reactive({
  username: '',
  status: ''
})

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

// 用户列表
const userList = ref([])
const roleList = ref([])
const companyList = ref([])

// 用户表单
const userForm = reactive({
  id: null,
  companyId: '',
  username: '',
  passwd: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  status: 1,
  roleIds: []
})

// 表单验证规则
const userRules = {
  companyId: [
    { required: true, message: '请选择企业', trigger: 'change' }
  ],
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    { min: 3, max: 20, message: '账号长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  password: [
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 上传头像
const handleAvatarUpload = async ({ file, onSuccess, onError }) => {
  try {
    const res = await uploadUserAvatar(file)
    if ((res && res.code === 0) || res.message === 'success') {
      userForm.avatar = res.data
      onSuccess && onSuccess(res)
      ElMessage.success('头像上传成功')
    } else {
      throw new Error(res?.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
    onError && onError(e)
  }
}

// 加载企业列表
const loadCompanyList = async () => {
  try {
    const response = await getCompanyList({ page: 1, size: 1000 })
    companyList.value = response.data?.list || []
  } catch (error) {
    ElMessage.error('加载企业列表失败：' + error.message)
  }
}

// 加载角色列表
const loadRoleList = async () => {
  try {
    const response = await getRoleList({ page: 1, size: 1000 })
    roleList.value = response.data?.list || []
  } catch (error) {
    ElMessage.error('加载角色列表失败：' + error.message)
  }
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const query = {}
    if (searchForm.username) query.username = searchForm.username
    if (searchForm.status !== '' && searchForm.status !== null && searchForm.status !== undefined) {
      query.status = searchForm.status
    }
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: Object.keys(query).length ? JSON.stringify(query) : undefined
    }
    const response = await getUserList(params)
    const records = response.data?.list || []
    userList.value = records.map((item) => ({
      ...item,
      createTime: formatTimestamp(item.cts),
      updateTime: formatTimestamp(item.uts),
      companyName: item.companyName || '',
      roleName: item.roleName || ''
    }))
    pagination.total = response.data?.total || 0
  } catch (error) {
    ElMessage.error('加载用户列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    status: ''
  })
  handleSearch()
}

// 添加用户
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

// 编辑用户
const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(userForm, {
    id: row.id,
    companyId: row.companyId || '',
    username: row.username,
    passwd: '',
    nickname: row.nickname || row.realName || '',
    phone: row.phone,
    email: row.email,
    avatar: row.avatar || '',
    status: row.status,
    roleIds: row.roleIds || (row.roleId ? [row.roleId] : [])
  })
}

// 删除用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 重置密码
const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 实际调用更新接口重置密码（后端字段为 passwd）
    await updateUser({ id: row.id, companyId: row.companyId, passwd: '123456' })
    ElMessage.success('密码重置成功，新密码为：123456')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('重置密码失败：' + error.message)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!userFormRef.value) return
  
  try {
    const valid = await userFormRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    if (isEdit.value) {
      await updateUser(userForm)
      ElMessage.success('更新成功')
    } else {
      await addUser(userForm)
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadUserList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  Object.assign(userForm, {
    id: null,
    companyId: '',
    username: '',
    passwd: '',
    nickname: '',
    phone: '',
    email: '',
    avatar: '',
    status: 1,
    roleIds: []
  })
  userFormRef.value?.resetFields()
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

// 分页变化
const handleSizeChange = (val) => {
  pagination.pageSize = val
  loadUserList()
}

const handleCurrentChange = (val) => {
  pagination.currentPage = val
  loadUserList()
}

onMounted(() => {
  loadUserList()
  loadCompanyList()
  loadRoleList()
})
</script>

<script>
// 独立导出以避免与 <script setup> 冲突，这里仅提供工具函数给上方使用
export default {}

// 简单的时间格式化（毫秒/秒时间戳均兼容）
function formatTimestamp(val) {
  if (!val) return ''
  const str = String(val)
  let ms
  if (/^\d{14}$/.test(str)) {
    const yyyy = Number(str.slice(0, 4))
    const mm = Number(str.slice(4, 6)) - 1
    const dd = Number(str.slice(6, 8))
    const hh = Number(str.slice(8, 10))
    const mi = Number(str.slice(10, 12))
    const ss = Number(str.slice(12, 14))
    ms = new Date(yyyy, mm, dd, hh, mi, ss).getTime()
  } else {
    const ts = Number(val)
    if (!Number.isFinite(ts)) return String(val)
    ms = ts < 10000000000 ? ts * 1000 : ts
  }
  try {
    const d = new Date(ms)
    if (Number.isNaN(d.getTime())) return ''
    const yyyy = d.getFullYear()
    const mm = String(d.getMonth() + 1).padStart(2, '0')
    const dd = String(d.getDate()).padStart(2, '0')
    const hh = String(d.getHours()).padStart(2, '0')
    const mi = String(d.getMinutes()).padStart(2, '0')
    const ss = String(d.getSeconds()).padStart(2, '0')
    return `${yyyy}-${mm}-${dd} ${hh}:${mi}:${ss}`
  } catch (e) {
    return ''
  }
}
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

/* 用户对话框样式 */
.user-dialog {
  .el-dialog__body {
    padding: 20px;
  }
}

.user-form-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.user-form {
  .el-form-item {
    margin-bottom: 20px;
    
    .el-form-item__label {
      font-weight: 500;
      color: #303133;
    }
    
    .el-input {
      .el-input__wrapper {
        border-radius: 8px;
        box-shadow: 0 0 0 1px #dcdfe6;
        transition: all 0.3s;
        
        &:hover {
          box-shadow: 0 0 0 1px #c0c4cc;
        }
        
        &.is-focus {
          box-shadow: 0 0 0 1px #409eff;
        }
      }
    }
    
    .el-select {
      .el-input__wrapper {
        border-radius: 8px;
      }
    }
    
    .el-switch {
      .el-switch__core {
        border-radius: 20px;
      }
    }
  }
  
  .el-form-item--required .el-form-item__label::before {
    content: '*';
    color: #f56c6c;
    margin-right: 4px;
  }
}
</style>


