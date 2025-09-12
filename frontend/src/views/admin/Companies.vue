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
      <el-table-column prop="name" label="企业名称" width="180" />
      <el-table-column prop="companyCode" label="企业编码" width="140" />
      <el-table-column prop="parentName" label="父企业" width="180">
        <template #default="{ row }">
          <span v-if="row.pid && row.pid !== 0">{{ getParentCompanyName(row.pid) }}</span>
          <span v-else class="text-gray-400">顶级企业</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column prop="updateTime" label="修改时间" width="160" />
      <el-table-column prop="seatCount" label="坐席数" width="100" />
      <el-table-column prop="skillGroupCount" label="技能组数" width="100" />
      <el-table-column prop="ivrConcurrent" label="IVR并发" width="110" />
      <el-table-column prop="outboundConcurrent" label="外呼并发" width="110" />
      <el-table-column prop="displayNumber" label="外显号码" width="140" />
      <el-table-column prop="openedSeatCount" label="开通坐席数" width="120" />
      <el-table-column prop="expireTime" label="有效期" width="140" />
      <el-table-column prop="hideNumber" label="隐藏号码" width="100">
        <template #default="{ row }">
          <el-tag :type="row.hideNumber ? 'info' : 'success'">
            {{ row.hideNumber ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="recordRetention" label="录音保留" width="110" />
      <el-table-column prop="cdrCallback" label="话单回调" min-width="140" />
      <el-table-column prop="ossProduct" label="录音OSS产品" width="140" />
      <el-table-column prop="recordFileType" label="录音文件类型" width="120" />
      <el-table-column prop="recordEncrypt" label="录音文件加密" width="120" />
      <el-table-column prop="cdrEncryptPush" label="话单加密推送" width="130" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="warning" size="small" @click="handleView(row)">
            详情
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
      width="920px"
      @close="handleDialogClose"
    >
      <el-form
        ref="companyFormRef"
        :model="companyForm"
        :rules="companyRules"
        label-width="110px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="父企业" prop="pid">
              <el-select v-model="companyForm.pid" placeholder="请选择父企业" style="width: 100%" clearable>
                <el-option :value="0" label="无父企业（顶级企业）" />
                <el-option
                  v-for="company in parentCompanyOptions"
                  :key="company.id"
                  :value="company.id"
                  :label="company.name"
                  :disabled="company.id === companyForm.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="name">
              <el-input v-model="companyForm.name" placeholder="请输入企业名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="企业编码" prop="companyCode">
              <el-input v-model="companyForm.companyCode" placeholder="请输入企业编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外显号码">
              <el-input v-model="companyForm.displayNumber" placeholder="可选，外显号码" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="联系人" prop="contact">
              <el-input v-model="companyForm.contact" placeholder="请输入联系人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="companyForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="金额" prop="balance">
              <el-input-number v-model="companyForm.balance" :min="0" :step="0.01" :precision="2" style="width: 100%" placeholder="请输入金额" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前坐席数量" prop="agentSize">
              <el-input-number v-model="companyForm.agentSize" :min="0" :step="1" style="width: 100%" placeholder="当前坐席数量" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="隐藏号码" prop="hiddenCustomer">
              <el-select v-model="companyForm.hiddenCustomer" placeholder="请选择">
                <el-option :value="0" label="不隐藏" />
                <el-option :value="1" label="隐藏" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码等级" prop="secretType">
              <el-select v-model="companyForm.secretType">
                <el-option :value="1" label="低" />
                <el-option :value="2" label="中" />
                <el-option :value="3" label="高" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="外呼并发" prop="outboundLimit">
              <el-input-number v-model="companyForm.outboundLimit" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IVR并发" prop="ivrLimit">
              <el-input-number v-model="companyForm.ivrLimit" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="开通坐席数" prop="agentLimit">
              <el-input-number v-model="companyForm.agentLimit" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开通技能组数" prop="groupLimit">
              <el-input-number v-model="companyForm.groupLimit" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="技能组坐席" prop="groupAgentLimit">
              <el-input-number v-model="companyForm.groupAgentLimit" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="录音保留(月)" prop="recordStorage">
              <el-input-number v-model="companyForm.recordStorage" :min="0" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="录音文件类型">
              <el-select v-model="companyForm.recordFileType" placeholder="请选择">
                <el-option label="wav" value="wav" />
                <el-option label="mp3" value="mp3" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="录音文件加密">
              <el-select v-model="companyForm.recordEncrypt" placeholder="不加密">
                <el-option label="不加密" value="不加密" />
                <el-option label="AES256" value="aes256" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="话单加密推送">
              <el-select v-model="companyForm.cdrEncryptPush" placeholder="不加密">
                <el-option label="不加密" value="不加密" />
                <el-option label="AES256" value="aes256" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="话单回调" prop="notifyUrl">
              <el-input v-model="companyForm.notifyUrl" placeholder="http(s)://..." />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期" prop="expireTime">
              <el-date-picker
                v-model="companyForm.expireTime"
                type="date"
                placeholder="请选择日期"
                format="YYYY-MM-DD"
                value-format="x"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="付费方式" prop="payType">
              <el-select v-model="companyForm.payType">
                <el-option :value="0" label="预付费" />
                <el-option :value="1" label="后付费" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="companyForm.status">
                <el-option :value="0" label="禁用" />
                <el-option :value="1" label="免费" />
                <el-option :value="2" label="试用" />
                <el-option :value="3" label="付费" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <div style="margin: 8px 0 12px; font-weight: 600; font-size: 16px;">录音推送配置</div>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="录音OSS产品">
              <el-select v-model="companyForm.ossProduct" placeholder="请选择">
                <el-option label="阿里云 OSS" value="aliyun" />
                <el-option label="腾讯云 COS" value="tencent" />
                <el-option label="华为云 OBS" value="huawei" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
      width="980px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="父企业">
          <span v-if="currentCompany.pid && currentCompany.pid !== 0">{{ getParentCompanyName(currentCompany.pid) }}</span>
          <span v-else class="text-gray-400">顶级企业</span>
        </el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ currentCompany.name }}</el-descriptions-item>
        <el-descriptions-item label="企业编码">{{ currentCompany.companyCode }}</el-descriptions-item>
        <el-descriptions-item label="外显号码">{{ currentCompany.displayNumber || '-' }}</el-descriptions-item>

        <el-descriptions-item label="联系人">{{ currentCompany.contact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentCompany.phone || '-' }}</el-descriptions-item>

        <el-descriptions-item label="金额">{{ currentCompany.balance ? '¥' + currentCompany.balance.toFixed(2) : '¥0.00' }}</el-descriptions-item>
        <el-descriptions-item label="当前坐席数量">{{ currentCompany.agentSize ?? 0 }}</el-descriptions-item>

        <el-descriptions-item label="隐藏号码">{{ currentCompany.hiddenCustomer === 1 ? '隐藏' : '不隐藏' }}</el-descriptions-item>
        <el-descriptions-item label="密码等级">
          {{ ({1:'低',2:'中',3:'高'})[currentCompany.secretType] || currentCompany.secretType || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="外呼并发">{{ currentCompany.outboundLimit ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="IVR并发">{{ currentCompany.ivrLimit ?? 0 }}</el-descriptions-item>

        <el-descriptions-item label="开通坐席数">{{ currentCompany.agentLimit ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="开通技能组数">{{ currentCompany.groupLimit ?? 0 }}</el-descriptions-item>

        <el-descriptions-item label="技能组坐席">{{ currentCompany.groupAgentLimit ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="录音保留(月)">{{ currentCompany.recordStorage ?? 0 }}</el-descriptions-item>
        <el-descriptions-item label="录音文件类型">{{ currentCompany.recordFileType || '-' }}</el-descriptions-item>

        <el-descriptions-item label="录音文件加密">{{ currentCompany.recordEncrypt || '不加密' }}</el-descriptions-item>
        <el-descriptions-item label="话单加密推送">{{ currentCompany.cdrEncryptPush || '不加密' }}</el-descriptions-item>

        <el-descriptions-item label="录音OSS产品">{{ currentCompany.ossProduct || '-' }}</el-descriptions-item>
        <el-descriptions-item label="话单回调" :span="1">{{ currentCompany.notifyUrl || '-' }}</el-descriptions-item>

        <el-descriptions-item label="有效期">{{ currentCompany.expireTime ? formatTimestamp(currentCompany.expireTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="付费方式">{{ currentCompany.payType === 1 ? '后付费' : (currentCompany.payType === 0 ? '预付费' : currentCompany.payType) }}</el-descriptions-item>

        <el-descriptions-item label="状态">
          <el-tag :type="currentCompany.status === 1 || currentCompany.status === 3 ? 'success' : (currentCompany.status === 2 ? 'warning' : 'danger')">
            {{ ({0:'禁用',1:'免费',2:'试用',3:'付费'})[currentCompany.status] ?? '-' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="1">{{ currentCompany.address }}</el-descriptions-item>

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
// 父企业选项列表
const parentCompanyOptions = ref([])

// 企业表单
const companyForm = reactive({
  id: null,
  pid: 0,
  name: '',
  companyCode: '',
  hiddenCustomer: 0,
  secretType: 1,
  outboundLimit: 5,
  ivrLimit: 5,
  agentLimit: 5,
  groupLimit: 5,
  groupAgentLimit: 10,
  recordStorage: 3,
  notifyUrl: '',
  expireTime: undefined,
  payType: 1,
  status: 3,
  // 金额和坐席数量字段
  balance: 0,
  agentSize: 0,
  // 其他展示/扩展字段
  displayNumber: '',
  ossProduct: '',
  recordFileType: '',
  recordEncrypt: '不加密',
  cdrEncryptPush: '不加密',
  // 兼容旧字段
  contact: '',
  phone: '',
  email: '',
  address: '',
  // 仅前端选择项（已废弃保留）
})

// 表单验证规则
const companyRules = {
  name: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  companyCode: [{ required: true, message: '请输入企业编码', trigger: 'blur' }],
  contact: [{ required: false, message: '请输入联系人姓名', trigger: 'blur' }],
  phone: [{ required: false, message: '请输入联系电话', trigger: 'blur' }],
  balance: [{ required: false, message: '请输入金额', trigger: 'change' }],
  agentSize: [{ required: false, message: '请输入当前坐席数量', trigger: 'change' }],
  outboundLimit: [{ required: true, message: '请输入外呼并发', trigger: 'change' }],
  ivrLimit: [{ required: true, message: '请输入IVR并发', trigger: 'change' }],
  agentLimit: [{ required: true, message: '请输入开通坐席数', trigger: 'change' }],
  groupLimit: [{ required: true, message: '请输入技能组数', trigger: 'change' }],
  groupAgentLimit: [{ required: true, message: '请输入技能组坐席', trigger: 'change' }],
  recordStorage: [{ required: true, message: '请输入录音保留月数', trigger: 'change' }],
  expireTime: [{ required: false, message: '请选择有效期', trigger: 'change' }],
  ossProduct: [{ required: false, message: '请选择录音OSS产品', trigger: 'change' }],
  recordFileType: [{ required: false, message: '请选择录音文件类型', trigger: 'change' }],
  recordEncrypt: [{ required: false, message: '请选择录音加密', trigger: 'change' }],
  cdrEncryptPush: [{ required: false, message: '请选择话单加密推送', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
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
    const records = response.data?.list || []
    companyList.value = records.map((item) => ({
      ...item,
      createTime: formatTimestamp(item.cts),
      updateTime: formatTimestamp(item.uts),
      seatCount: (item.agentSize ?? item.agent_size ?? 0),
      skillGroupCount: item.groupLimit, // 开通技能组数量
      ivrConcurrent: item.ivrLimit,

      outboundConcurrent: item.outboundLimit,
      openedSeatCount: item.agentLimit,
      expireTime: item.expireTime,
      hideNumber: item.hiddenCustomer === 1,
      recordRetention: item.recordStorage,
      cdrCallback: item.notifyUrl,
      // 金额和坐席数量字段
      balance: item.balance ?? 0,
      agentSize: item.agentSize ?? 0,
      // 新增显示字段
      displayNumber: item.displayNumber,
      ossProduct: item.ossProduct,
      recordFileType: item.recordFileType,
      recordEncrypt: item.recordEncrypt,
      cdrEncryptPush: item.cdrEncryptPush
    }))
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载企业列表失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 加载父企业选项列表
const loadParentCompanyOptions = async () => {
  try {
    const params = {
      page: 1,
      size: 1000, // 获取所有企业用于选择
      status: 1 // 只获取启用的企业
    }
    const response = await getCompanyList(params)
    const records = response.data?.list || []
    parentCompanyOptions.value = records.map((item) => ({
      id: item.id,
      name: item.name,
      pid: item.pid
    }))
  } catch (error) {
    console.error('加载父企业选项失败：', error)
  }
}

// 根据父企业ID获取父企业名称
const getParentCompanyName = (pid) => {
  const parentCompany = parentCompanyOptions.value.find(company => company.id === pid)
  return parentCompany ? parentCompany.name : `ID: ${pid}`
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
const handleAdd = async () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
  await loadParentCompanyOptions()
}

// 编辑企业
const handleEdit = async (row) => {
  isEdit.value = true
  dialogVisible.value = true
  await loadParentCompanyOptions()
  Object.assign(companyForm, {
    id: row.id,
    pid: row.pid ?? 0,
    name: row.name,
    companyCode: row.companyCode,
    hiddenCustomer: row.hiddenCustomer ?? 0,
    secretType: row.secretType ?? 1,
    outboundLimit: row.outboundLimit ?? 0,
    ivrLimit: row.ivrLimit ?? 0,
    agentLimit: row.agentLimit ?? 0,
    groupLimit: row.groupLimit ?? 0,
    groupAgentLimit: row.groupAgentLimit ?? 10,
    recordStorage: row.recordStorage ?? 0,
    notifyUrl: row.notifyUrl ?? '',
    expireTime: row.expireTime ?? undefined,
    payType: row.payType ?? 1,
    status: row.status ?? 1,
    // 金额和坐席数量字段
    balance: row.balance ?? 0,
    agentSize: row.agentSize ?? 0,
    displayNumber: row.displayNumber ?? '',
    ossProduct: row.ossProduct ?? '',
    recordFileType: row.recordFileType ?? '',
    recordEncrypt: row.recordEncrypt ?? '不加密',
    cdrEncryptPush: row.cdrEncryptPush ?? '不加密',
    contact: row.contact ?? '',
    phone: row.phone ?? '',
    email: row.email ?? '',
    address: row.address ?? ''
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
      await updateCompany(companyForm.id, companyForm)
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
    pid: 0,
    name: '',
    companyCode: '',
    hiddenCustomer: 0,
    secretType: 1,
    outboundLimit: 5,
    ivrLimit: 5,
    agentLimit: 5,
    groupLimit: 5,
    groupAgentLimit: 10,
    recordStorage: 3,
    notifyUrl: '',
    expireTime: undefined,
    payType: 1,
    status: 3,
    displayNumber: '',
    ossProduct: '',
    recordFileType: '',
    recordEncrypt: '不加密',
    cdrEncryptPush: '不加密',
    contact: '',
    phone: '',
    email: '',
    address: ''
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

onMounted(async () => {
  await loadParentCompanyOptions()
  loadCompanyList()
})

// 简单的时间格式化（毫秒/秒时间戳均兼容）
function formatTimestamp(val) {
  if (!val) return ''
  // 兼容三类：秒时间戳、毫秒时间戳、yyyyMMddHHmmss（14位数字）
  const str = String(val)
  let ms
  if (/^\d{14}$/.test(str)) {
    // 解析 yyyyMMddHHmmss
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
    ms = ts < 10_000_000_000 ? ts * 1000 : ts
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
</style>


