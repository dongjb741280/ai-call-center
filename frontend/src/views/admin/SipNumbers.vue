<template>
  <div class="page-card">
    <div class="page-header">
      <h2>SIP号码管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增SIP号
      </el-button>
    </div>

    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="sip" label="SIP号码" width="160" />
      <el-table-column prop="agentKey" label="绑定坐席" width="160" />
      <el-table-column prop="companyName" label="所属企业" min-width="160" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑SIP号' : '新增SIP号'" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="SIP号码" prop="sip">
          <el-input v-model="form.sip" placeholder="如：1001" />
        </el-form-item>
        <el-form-item label="SIP密码" prop="sipPwd">
          <el-input v-model="form.sipPwd" placeholder="SIP注册密码" show-password />
        </el-form-item>
        <el-form-item label="绑定坐席">
          <el-input v-model="form.agentKey" placeholder="绑定坐席账号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSipList, addSip, updateSip, deleteSip } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])

const pagination = reactive({ currentPage: 1, pageSize: 20, total: 0 })
const form = reactive({ id: null, sip: '', sipPwd: '', agentKey: '', status: 1 })

const rules = {
  sip: [{ required: true, message: '请输入SIP号码', trigger: 'blur' }],
  sipPwd: [{ required: true, message: '请输入SIP密码', trigger: 'blur' }]
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, sip: '', sipPwd: '', agentKey: '', status: 1 })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.currentPage, pageSize: pagination.pageSize }
    const res = await getSipList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, { id: row.id, sip: row.sip, sipPwd: '', agentKey: row.agentKey || '', status: row.status })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateSip(form.id, { ...form })
    } else {
      await addSip({ ...form })
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除SIP号 "${row.sip}" 吗？`, '提示', { type: 'warning' })
    await deleteSip(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-card { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
