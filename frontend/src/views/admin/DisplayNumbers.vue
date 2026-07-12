<template>
  <div class="page-card">
    <div class="page-header">
      <h2>显号管理</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 显号列表 -->
      <el-tab-pane label="显号列表" name="phone">
        <div class="tab-actions">
          <el-button type="success" @click="handleAddPhone">新增显号</el-button>
        </div>
        <el-table :data="phoneList" v-loading="phoneLoading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="phone" label="号码" width="160" />
          <el-table-column prop="companyName" label="所属企业" min-width="160" />
          <el-table-column prop="type" label="类型" width="120">
            <template #default="{ row }">
              <el-tag>{{ row.type || '普通' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditPhone(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeletePhone(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="phonePagination.currentPage"
            v-model:page-size="phonePagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="phonePagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadPhoneList"
            @current-change="loadPhoneList"
          />
        </div>
      </el-tab-pane>

      <!-- 显号池 -->
      <el-tab-pane label="显号池" name="display">
        <div class="tab-actions">
          <el-button type="success" @click="handleAddDisplay">新增号码池</el-button>
        </div>
        <el-table :data="displayList" v-loading="displayLoading" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="池名称" min-width="160" />
          <el-table-column prop="companyName" label="所属企业" width="160" />
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditDisplay(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteDisplay(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="displayPagination.currentPage"
            v-model:page-size="displayPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="displayPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadDisplayList"
            @current-change="loadDisplayList"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 显号弹窗 -->
    <el-dialog v-model="phoneDialogVisible" :title="phoneIsEdit ? '编辑显号' : '新增显号'" width="480px" @close="resetPhoneForm">
      <el-form ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" label-width="100px">
        <el-form-item label="号码" prop="phone">
          <el-input v-model="phoneForm.phone" placeholder="外呼显示号码" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="phoneForm.type" style="width: 100%">
            <el-option label="普通" value="普通" />
            <el-option label="透传" value="透传" />
            <el-option label="随机" value="随机" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="phoneDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePhoneSubmit" :loading="phoneSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 显号池弹窗 -->
    <el-dialog v-model="displayDialogVisible" :title="displayIsEdit ? '编辑号码池' : '新增号码池'" width="480px" @close="resetDisplayForm">
      <el-form ref="displayFormRef" :model="displayForm" :rules="displayRules" label-width="100px">
        <el-form-item label="池名称" prop="name">
          <el-input v-model="displayForm.name" placeholder="号码池名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="displayForm.description" placeholder="号码池描述" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="displayDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDisplaySubmit" :loading="displaySubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPhoneList, addPhone, updatePhone, deletePhone,
  getDisplayList, addDisplay, updateDisplay, deleteDisplay
} from '@/api/config'

const activeTab = ref('phone')

// ---- 显号列表 ----
const phoneList = ref([])
const phoneLoading = ref(false)
const phoneDialogVisible = ref(false)
const phoneIsEdit = ref(false)
const phoneSubmitLoading = ref(false)
const phoneFormRef = ref()
const phonePagination = reactive({ currentPage: 1, pageSize: 20, total: 0 })
const phoneForm = reactive({ id: null, phone: '', type: '普通' })
const phoneRules = { phone: [{ required: true, message: '请输入号码', trigger: 'blur' }] }

const resetPhoneForm = () => {
  phoneFormRef.value?.resetFields()
  Object.assign(phoneForm, { id: null, phone: '', type: '普通' })
}

const loadPhoneList = async () => {
  phoneLoading.value = true
  try {
    const params = { pageNum: phonePagination.currentPage, pageSize: phonePagination.pageSize }
    const res = await getPhoneList(params)
    if (res.code === 0) {
      phoneList.value = res.data?.list || []
      phonePagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载显号失败') }
  finally { phoneLoading.value = false }
}

const handleAddPhone = () => { phoneIsEdit.value = false; resetPhoneForm(); phoneDialogVisible.value = true }
const handleEditPhone = (row) => { phoneIsEdit.value = true; Object.assign(phoneForm, row); phoneDialogVisible.value = true }

const handlePhoneSubmit = async () => {
  if (!phoneFormRef.value) return
  try { await phoneFormRef.value.validate() } catch { return }
  phoneSubmitLoading.value = true
  try {
    if (phoneIsEdit.value) {
      await updatePhone(phoneForm.id, { ...phoneForm })
    } else {
      await addPhone({ ...phoneForm })
    }
    ElMessage.success(phoneIsEdit.value ? '修改成功' : '新增成功')
    phoneDialogVisible.value = false
    loadPhoneList()
  } catch { ElMessage.error('操作失败') }
  finally { phoneSubmitLoading.value = false }
}

const handleDeletePhone = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除显号 "${row.phone}" 吗？`, '提示', { type: 'warning' })
    await deletePhone(row.id)
    ElMessage.success('删除成功')
    loadPhoneList()
  } catch { /* cancelled */ }
}

// ---- 显号池 ----
const displayList = ref([])
const displayLoading = ref(false)
const displayDialogVisible = ref(false)
const displayIsEdit = ref(false)
const displaySubmitLoading = ref(false)
const displayFormRef = ref()
const displayPagination = reactive({ currentPage: 1, pageSize: 20, total: 0 })
const displayForm = reactive({ id: null, name: '', description: '' })
const displayRules = { name: [{ required: true, message: '请输入池名称', trigger: 'blur' }] }

const resetDisplayForm = () => {
  displayFormRef.value?.resetFields()
  Object.assign(displayForm, { id: null, name: '', description: '' })
}

const loadDisplayList = async () => {
  displayLoading.value = true
  try {
    const params = { pageNum: displayPagination.currentPage, pageSize: displayPagination.pageSize }
    const res = await getDisplayList(params)
    if (res.code === 0) {
      displayList.value = res.data?.list || []
      displayPagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载显号池失败') }
  finally { displayLoading.value = false }
}

const handleAddDisplay = () => { displayIsEdit.value = false; resetDisplayForm(); displayDialogVisible.value = true }
const handleEditDisplay = (row) => { displayIsEdit.value = true; Object.assign(displayForm, row); displayDialogVisible.value = true }

const handleDisplaySubmit = async () => {
  if (!displayFormRef.value) return
  try { await displayFormRef.value.validate() } catch { return }
  displaySubmitLoading.value = true
  try {
    if (displayIsEdit.value) {
      await updateDisplay(displayForm.id, { ...displayForm })
    } else {
      await addDisplay({ ...displayForm })
    }
    ElMessage.success(displayIsEdit.value ? '修改成功' : '新增成功')
    displayDialogVisible.value = false
    loadDisplayList()
  } catch { ElMessage.error('操作失败') }
  finally { displaySubmitLoading.value = false }
}

const handleDeleteDisplay = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除号码池 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteDisplay(row.id)
    ElMessage.success('删除成功')
    loadDisplayList()
  } catch { /* cancelled */ }
}

const onTabChange = (tab) => {
  if (tab === 'phone') loadPhoneList()
  else loadDisplayList()
}

onMounted(loadPhoneList)
</script>

<style scoped>
.page-card { padding: 0; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; }
.tab-actions { display: flex; gap: 8px; align-items: center; margin-bottom: 12px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
