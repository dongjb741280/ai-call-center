<template>
  <div class="page-card">
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <!-- 媒体网关 -->
      <el-tab-pane label="媒体网关" name="gateway">
        <div class="tab-actions">
          <el-button type="primary" @click="handleAddGateway">新增</el-button>
        </div>
        <el-table :data="gatewayList" v-loading="gwLoading" border size="small">
          <el-table-column prop="id" label="ID" width="50" />
          <el-table-column prop="name" label="网关名称" width="130" show-overflow-tooltip />
          <el-table-column label="修改时间" width="160">
            <template #default="{ row }">{{ formatTime(row.uts) }}</template>
          </el-table-column>
          <el-table-column prop="mediaHost" label="网关地址" width="180" show-overflow-tooltip />
          <el-table-column prop="displaceNum" label="前缀去除" width="90" align="center" />
          <el-table-column prop="calledPrefix" label="号码前缀" width="100" />
          <el-table-column prop="routeArea" label="网关归属地" width="120" show-overflow-tooltip />
          <el-table-column prop="outNumberPrefix" label="外地号码前缀" width="120" />
          <el-table-column prop="display" label="显号" width="120" show-overflow-tooltip />
          <el-table-column prop="profile" label="Profile" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="row.profile === 'internal' ? 'success' : row.profile === 'external' ? '' : 'warning'">
                {{ row.profile }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="铃音识别" width="90" align="center">
            <template #default="{ row }">
              <el-tag size="small" :type="row.ringAsr === 1 ? 'success' : 'info'">
                {{ row.ringAsr === 1 ? '开启' : '关闭' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="charge" label="计费" width="70" align="center" />
          <el-table-column prop="codecs" label="编码" width="180" show-overflow-tooltip />
          <el-table-column prop="sipHeader1" label="sipHeader1" width="220" show-overflow-tooltip />
          <el-table-column prop="sipHeader2" label="sipHeader2" width="220" show-overflow-tooltip />
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditGateway(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteGateway(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="gwPagination.currentPage"
            v-model:page-size="gwPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="gwPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadGatewayList"
            @current-change="loadGatewayList"
          />
        </div>
      </el-tab-pane>

      <!-- 网关组 -->
      <el-tab-pane label="网关组" name="group">
        <div class="tab-actions">
          <el-button type="primary" @click="handleAddGroup">新增</el-button>
        </div>
        <el-table :data="groupList" v-loading="rgLoading" border size="small">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="routeGroup" label="组名称" min-width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
                {{ row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="handleEditGroup(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDeleteGroup(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="rgPagination.currentPage"
            v-model:page-size="rgPagination.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="rgPagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadGroupList"
            @current-change="loadGroupList"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 网关弹窗 -->
    <el-dialog v-model="gwDialogVisible" :title="gwIsEdit ? '编辑媒体网关' : '新增媒体网关'" width="640px" @close="resetGwForm">
      <el-form ref="gwFormRef" :model="gwForm" :rules="gwRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="14">
            <el-form-item label="网关名称" prop="name">
              <el-input v-model="gwForm.name" placeholder="2-16个字符" maxlength="16" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="铃音识别" prop="ringAsr" label-width="80px">
              <el-switch v-model="gwForm.ringAsr" :active-value="1" :inactive-value="0" active-text="开启" inactive-text="关闭" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="网关地址" prop="mediaHost">
              <el-input v-model="gwForm.mediaHost" placeholder="IP或域名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="端口" prop="mediaPort" label-width="60px">
              <el-input v-model.number="gwForm.mediaPort" placeholder="10000-65535" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="前缀去除">
              <el-input v-model.number="gwForm.displaceNum" placeholder="去除位数" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="主叫前缀" label-width="80px">
              <el-input v-model="gwForm.callerPrefix" placeholder="最多8位" maxlength="8" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="被叫前缀" label-width="80px">
              <el-input v-model="gwForm.calledPrefix" placeholder="最多8位" maxlength="8" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="网关归属地">
              <el-input v-model="gwForm.routeArea" placeholder="选填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外地号码前缀">
              <el-input v-model="gwForm.outNumberPrefix" placeholder="选填" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="显号">
              <el-input v-model="gwForm.display" placeholder="${display}" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Profile" prop="profile">
              <el-select v-model="gwForm.profile" style="width: 100%">
                <el-option label="internal" value="internal" />
                <el-option label="external" value="external" />
                <el-option label="register" value="register" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="计费">
              <el-input v-model.number="gwForm.charge" placeholder="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编码" label-width="60px">
              <el-input v-model="gwForm.codecs" placeholder="^^:G729:PCMA:PCMU" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="sipHeader1">
          <el-input v-model="gwForm.sipHeader1" placeholder="X-V9-CALL-ID=${callId}" maxlength="36" />
        </el-form-item>
        <el-form-item label="sipHeader2">
          <el-input v-model="gwForm.sipHeader2" placeholder="X-V9-DEVICE-ID=${deviceId}" maxlength="36" />
        </el-form-item>
        <el-form-item label="sipHeader3">
          <el-input v-model="gwForm.sipHeader3" placeholder="可选" maxlength="36" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="gwDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGwSubmit" :loading="gwSubmitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 网关组弹窗 -->
    <el-dialog v-model="rgDialogVisible" :title="rgIsEdit ? '编辑网关组' : '新增网关组'" width="500px" @close="resetRgForm">
      <el-form ref="rgFormRef" :model="rgForm" :rules="rgRules" label-width="100px">
        <el-form-item label="组名称" prop="routeGroup">
          <el-input v-model="rgForm.routeGroup" placeholder="2-16个字符" maxlength="16" />
        </el-form-item>
        <el-form-item label="包含网关">
          <el-select v-model="rgForm.routeGetways" multiple placeholder="选择网关" style="width: 100%">
            <el-option v-for="gw in gatewayList" :key="gw.id" :label="gw.name" :value="gw.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="rgForm.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rgDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRgSubmit" :loading="rgSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRouteGatewayList, addRouteGateway, updateRouteGateway, deleteRouteGateway,
  getRouteGroupList, getRouteGroupDetail, addRouteGroup, updateRouteGroup, deleteRouteGroup
} from '@/api/config'

const activeTab = ref('gateway')

const formatTime = (ts) => {
  if (!ts) return ''
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

// ========== 媒体网关 ==========
const gatewayList = ref([])
const gwLoading = ref(false)
const gwDialogVisible = ref(false)
const gwIsEdit = ref(false)
const gwSubmitLoading = ref(false)
const gwFormRef = ref()
const gwPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const gwForm = reactive({
  id: null, name: '', mediaHost: '', mediaPort: null,
  displaceNum: 0, callerPrefix: '', calledPrefix: '',
  routeArea: '', outNumberPrefix: '', display: '',
  profile: 'external', ringAsr: 1, charge: 0, codecs: '^^:G729:PCMA:PCMU',
  sipHeader1: 'X-V9-CALL-ID=${callId}',
  sipHeader2: 'X-V9-DEVICE-ID=${deviceId}',
  sipHeader3: ''
})
const gwRules = {
  name: [{ required: true, message: '请输入网关名称', trigger: 'blur' }],
  mediaHost: [{ required: true, message: '请输入网关地址', trigger: 'blur' }],
  mediaPort: [
    { required: true, message: '请输入媒体端口', trigger: 'blur' },
    { type: 'number', min: 10000, max: 65535, message: '端口范围10000-65535', trigger: 'blur' }
  ],
  profile: [{ required: true, message: '请选择Profile', trigger: 'change' }]
}

const gwDefault = () => ({
  id: null, name: '', mediaHost: '', mediaPort: null,
  displaceNum: 0, callerPrefix: '', calledPrefix: '',
  routeArea: '', outNumberPrefix: '', display: '',
  profile: 'external', ringAsr: 1, charge: 0, codecs: '^^:G729:PCMA:PCMU',
  sipHeader1: 'X-V9-CALL-ID=${callId}',
  sipHeader2: 'X-V9-DEVICE-ID=${deviceId}',
  sipHeader3: ''
})

const resetGwForm = () => {
  gwFormRef.value?.resetFields()
  Object.assign(gwForm, gwDefault())
}

const parseMediaHost = (hostPort) => {
  if (!hostPort) return { host: '', port: null }
  const idx = hostPort.lastIndexOf(':')
  if (idx > 0) {
    return { host: hostPort.substring(0, idx), port: parseInt(hostPort.substring(idx + 1)) || null }
  }
  return { host: hostPort, port: null }
}

const loadGatewayList = async () => {
  gwLoading.value = true
  try {
    const params = { pageNum: gwPagination.currentPage, pageSize: gwPagination.pageSize }
    const res = await getRouteGatewayList(params)
    if (res.code === 0) {
      gatewayList.value = res.data?.list || []
      gwPagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载网关列表失败') }
  finally { gwLoading.value = false }
}

const handleAddGateway = () => { gwIsEdit.value = false; resetGwForm(); gwDialogVisible.value = true }

const handleEditGateway = (row) => {
  gwIsEdit.value = true
  const { host, port } = parseMediaHost(row.mediaHost)
  Object.assign(gwForm, {
    id: row.id,
    name: row.name || '',
    mediaHost: host,
    mediaPort: port,
    displaceNum: row.displaceNum ?? 0,
    callerPrefix: row.callerPrefix || '',
    calledPrefix: row.calledPrefix || '',
    routeArea: row.routeArea || '',
    outNumberPrefix: row.outNumberPrefix || '',
    display: row.display || '',
    profile: row.profile || 'external',
    ringAsr: row.ringAsr ?? 1,
    charge: row.charge ?? 0,
    codecs: row.codecs || '^^:G729:PCMA:PCMU',
    sipHeader1: row.sipHeader1 || '',
    sipHeader2: row.sipHeader2 || '',
    sipHeader3: row.sipHeader3 || ''
  })
  gwDialogVisible.value = true
}

const handleGwSubmit = async () => {
  if (!gwFormRef.value) return
  try { await gwFormRef.value.validate() } catch { return }
  gwSubmitLoading.value = true
  try {
    if (gwIsEdit.value) {
      await updateRouteGateway(gwForm.id, { ...gwForm })
    } else {
      await addRouteGateway({ ...gwForm })
    }
    ElMessage.success(gwIsEdit.value ? '修改成功' : '新增成功')
    gwDialogVisible.value = false
    loadGatewayList()
  } catch { ElMessage.error('操作失败') }
  finally { gwSubmitLoading.value = false }
}

const handleDeleteGateway = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除网关 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteRouteGateway(row.id)
    ElMessage.success('删除成功')
    loadGatewayList()
  } catch { /* cancelled */ }
}

// ========== 网关组 ==========
const groupList = ref([])
const rgLoading = ref(false)
const rgDialogVisible = ref(false)
const rgIsEdit = ref(false)
const rgSubmitLoading = ref(false)
const rgFormRef = ref()
const rgPagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const rgForm = reactive({ id: null, routeGroup: '', routeGetways: [], status: 1 })
const rgRules = { routeGroup: [{ required: true, message: '请输入组名称', trigger: 'blur' }] }

const resetRgForm = () => {
  rgFormRef.value?.resetFields()
  Object.assign(rgForm, { id: null, routeGroup: '', routeGetways: [], status: 1 })
}

const loadGroupList = async () => {
  rgLoading.value = true
  try {
    const params = { pageNum: rgPagination.currentPage, pageSize: rgPagination.pageSize }
    const res = await getRouteGroupList(params)
    if (res.code === 0) {
      groupList.value = res.data?.list || []
      rgPagination.total = res.data?.total || 0
    }
  } catch { ElMessage.error('加载网关组失败') }
  finally { rgLoading.value = false }
}

const handleAddGroup = () => {
  rgIsEdit.value = false; resetRgForm()
  if (gatewayList.value.length === 0) loadGatewayList()
  rgDialogVisible.value = true
}

const handleEditGroup = async (row) => {
  rgIsEdit.value = true
  if (gatewayList.value.length === 0) loadGatewayList()
  rgSubmitLoading.value = true
  try {
    const res = await getRouteGroupDetail(row.id)
    if (res.code === 0) {
      const d = res.data
      Object.assign(rgForm, {
        id: d.id, routeGroup: d.routeGroup || '',
        routeGetways: d.routeGetways || [], status: d.status ?? 1
      })
    } else {
      Object.assign(rgForm, { id: row.id, routeGroup: row.routeGroup, routeGetways: [], status: row.status ?? 1 })
    }
  } catch {
    Object.assign(rgForm, { id: row.id, routeGroup: row.routeGroup, routeGetways: [], status: row.status ?? 1 })
  }
  finally { rgSubmitLoading.value = false }
  rgDialogVisible.value = true
}

const handleRgSubmit = async () => {
  if (!rgFormRef.value) return
  try { await rgFormRef.value.validate() } catch { return }
  rgSubmitLoading.value = true
  try {
    if (rgIsEdit.value) {
      await updateRouteGroup(rgForm.id, { ...rgForm })
    } else {
      await addRouteGroup({ ...rgForm })
    }
    ElMessage.success(rgIsEdit.value ? '修改成功' : '新增成功')
    rgDialogVisible.value = false
    loadGroupList()
  } catch { ElMessage.error('操作失败') }
  finally { rgSubmitLoading.value = false }
}

const handleDeleteGroup = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除网关组 "${row.routeGroup}" 吗？`, '提示', { type: 'warning' })
    await deleteRouteGroup(row.id)
    ElMessage.success('删除成功')
    loadGroupList()
  } catch { /* cancelled */ }
}

const onTabChange = (tab) => {
  if (tab === 'gateway') loadGatewayList()
  else loadGroupList()
}

onMounted(loadGatewayList)
</script>

<style scoped>
.page-card { padding: 0; }
.tab-actions { display: flex; gap: 8px; align-items: center; margin-bottom: 12px; }
.pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
