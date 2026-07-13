<template>
  <div class="page-card">
    <div class="page-header">
      <h2>任务管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新建任务
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="任务名称">
        <el-input v-model="searchForm.name" placeholder="任务名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="转接类型">
        <el-select v-model="searchForm.transferType" placeholder="全部" clearable style="width:130px">
          <el-option label="技能组" value="技能组" />
          <el-option label="IVR" value="ivr" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部" clearable style="width:100px">
          <el-option label="运行" :value="1" />
          <el-option label="暂停" :value="2" />
          <el-option label="关闭" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="searchForm.dateRange" type="daterange"
          range-separator="至" start-placeholder="开始" end-placeholder="结束"
          value-format="x" style="width:240px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
        <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" style="width: 100%" size="small">
      <el-table-column label="任务id" width="80" align="center" prop="id" />
      <el-table-column prop="name" label="任务名称" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" width="165">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="165">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="呼叫开始" width="95" align="center">
        <template #default="{ row }">{{ row.callStartTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="呼叫结束" width="95" align="center">
        <template #default="{ row }">{{ row.callEndTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="关闭时间" width="165">
        <template #default="{ row }">{{ formatTime(row.closeTime) }}</template>
      </el-table-column>
      <el-table-column label="外显号码" width="120" align="center">
        <template #default="{ row }">{{ row.displayNumber || '-' }}</template>
      </el-table-column>
      <el-table-column label="呼叫轮次" width="90" align="center">
        <template #default="{ row }">{{ row.callRounds ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="当前轮次" width="90" align="center">
        <template #default="{ row }">{{ row.currentRound ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="转接类型" width="90" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.transferType === '技能组' ? 'success' : 'primary'">
            {{ row.transferType || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="任务进度" width="120" align="center">
        <template #default="{ row }">
          <el-progress
            :percentage="progressPct(row)"
            :stroke-width="16"
            :text-inside="true"
            :status="row.status === 3 ? 'success' : ''"
          >
            <span style="font-size:11px">{{ row.completedCount || 0 }}/{{ row.totalCount || 0 }}</span>
          </el-progress>
        </template>
      </el-table-column>
      <el-table-column label="接通数" width="80" align="center">
        <template #default="{ row }">{{ row.answeredCount ?? 0 }}</template>
      </el-table-column>
      <el-table-column label="接通率" width="80" align="center">
        <template #default="{ row }">
          <span :class="answerRateClass(row)">{{ answerRate(row) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="任务状态" width="90" align="center" fixed="right">
        <template #default="{ row }">
          <el-tag size="small" :type="statusTag(row.status)">{{ statusMap[row.status] || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="380" fixed="right" align="center">
        <template #default="{ row }">
          <template v-if="row.status === 1">
            <el-button type="warning" size="small" @click="handlePause(row)">暂停</el-button>
          </template>
          <template v-if="row.status === 2 || row.status === 3">
            <el-button type="success" size="small" @click="handleStart(row)">开始</el-button>
          </template>
          <template v-if="row.status === 1 || row.status === 2">
            <el-button type="danger" size="small" @click="handleComplete(row)">完成</el-button>
          </template>
          <el-button type="info" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="success" size="small" @click="handleContact(row)">联系人</el-button>
          <el-button v-if="row.status !== 3" type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status !== 2" type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- ==================== 编辑弹窗 ==================== -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新建任务'" width="680px" @close="resetForm" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="name">
              <el-input v-model="form.name" size="small" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据源">
              <el-input v-model="form.dataSource" size="small" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="呼叫开始">
              <el-time-picker v-model="form.callStartTime" value-format="HH:mm:ss" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="呼叫结束">
              <el-time-picker v-model="form.callEndTime" value-format="HH:mm:ss" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="修改时间">
              <el-input :value="formatTime(form.uts)" size="small" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关闭时间">
              <el-date-picker v-model="form.closeTimeDate" type="datetime" value-format="x" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="转接类型">
              <el-select v-model="form.transferType" size="small" style="width:100%">
                <el-option label="技能组" value="技能组" /><el-option label="IVR" value="ivr" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转接名称">
              <el-input v-model="form.transferName" size="small" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名单数量">
              <el-input-number v-model="form.totalCount" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已拨数">
              <el-input-number v-model="form.dialedCount" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="接听数">
              <el-input-number v-model="form.answeredCount" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="巡航方式">
              <el-input v-model="form.cruiseType" size="small" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务并发">
              <el-input-number v-model="form.concurrency" :min="1" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外显号码">
              <el-input v-model="form.displayNumber" size="small" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="号码池名称">
              <el-input v-model="form.numberPoolName" size="small" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="话后时长">
              <el-input-number v-model="form.afterCallDuration" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="外呼超时">
              <el-input-number v-model="form.callTimeout" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务状态通知">
              <el-input v-model="form.statusNotify" size="small" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="话务通知">
              <el-input v-model="form.trafficNotify" size="small" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务状态">
              <el-select v-model="form.status" size="small" style="width:100%">
                <el-option label="运行" :value="1" /><el-option label="暂停" :value="2" /><el-option label="关闭" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="呼叫轮次">
              <el-input-number v-model="form.callRounds" :min="1" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前轮次">
              <el-input-number v-model="form.currentRound" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="列表总数">
              <el-input-number v-model="form.listTotal" :min="0" size="small" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div class="dialog-section" style="margin-top:16px">
        <div class="dialog-section-title">任务暂停时间列表</div>
        <el-table :data="editPauseList" size="small" max-height="200">
          <el-table-column label="序号" width="60" align="center">
            <template #default="{ $index }">{{ $index + 1 }}</template>
          </el-table-column>
          <el-table-column label="暂停开始时间" width="200">
            <template #default="{ row }">{{ formatTime(row.pauseStartTime) }}</template>
          </el-table-column>
          <el-table-column label="暂停结束时间" width="200">
            <template #default="{ row }">{{ formatTime(row.pauseEndTime) }}</template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 联系人弹窗 ==================== -->
    <el-dialog v-model="contactVisible" title="任务联系人" width="960px" @close="contactVisible = false">
      <el-form :model="contactSearch" inline class="contact-search-form">
        <el-form-item label="号码">
          <el-input v-model="contactSearch.phone" placeholder="号码" clearable size="small" style="width:150px" />
        </el-form-item>
        <el-form-item label="拨打状态">
          <el-select v-model="contactSearch.callStatus" placeholder="全部" clearable size="small" style="width:120px">
            <el-option label="已拨打" value="已拨打" />
            <el-option label="未拨打" value="未拨打" />
            <el-option label="已接通" value="已接通" />
          </el-select>
        </el-form-item>
        <el-form-item label="呼叫结果">
          <el-select v-model="contactSearch.callResult" placeholder="全部" clearable size="small" style="width:120px">
            <el-option label="成功" value="成功" />
            <el-option label="失败" value="失败" />
            <el-option label="未接" value="未接" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="small" @click="handleContactSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button size="small" @click="handleContactReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
      <el-table :data="filteredContactList" size="small" max-height="400" style="margin-top:12px">
        <el-table-column prop="id" label="id" width="80" />
        <el-table-column prop="name" label="name" min-width="100" />
        <el-table-column prop="phone" label="号码" width="140" />
        <el-table-column prop="currentRound" label="当前轮次" width="90" align="center" />
        <el-table-column prop="agentCode" label="坐席编号" width="110" align="center" />
        <el-table-column prop="uuid" label="uuid1" min-width="140" show-overflow-tooltip />
        <el-table-column label="拨打时间" width="170">
          <template #default="{ row }">{{ formatTime(row.callTime) }}</template>
        </el-table-column>
        <el-table-column label="应答时间" width="170">
          <template #default="{ row }">{{ formatTime(row.answerTime) }}</template>
        </el-table-column>
        <el-table-column label="桥接时间" width="170">
          <template #default="{ row }">{{ formatTime(row.bridgeTime) }}</template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button type="primary" @click="handleExportContacts">导出</el-button>
        <el-button @click="contactVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- ==================== 详情弹窗 ==================== -->
    <el-dialog v-model="detailVisible" title="任务详情" width="680px" @close="detailVisible = false">
      <template v-if="detailData.id">
        <el-descriptions :column="2" border size="small" class="task-detail-desc">
          <el-descriptions-item label="任务名称">{{ detailData.name || '-' }}</el-descriptions-item>
          <el-descriptions-item label="数据源">{{ detailData.dataSource || '-' }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ detailData.callStartTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ detailData.callEndTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="修改时间">{{ formatTime(detailData.uts) }}</el-descriptions-item>
          <el-descriptions-item label="关闭时间">{{ formatTime(detailData.closeTime) }}</el-descriptions-item>
          <el-descriptions-item label="转接类型">{{ detailData.transferType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转接名称">{{ detailData.transferName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="名单数量">{{ detailData.totalCount ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="已拨数">{{ detailData.dialedCount ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="接听数">{{ detailData.answeredCount ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="巡航方式">{{ detailData.cruiseType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务并发">{{ detailData.concurrency ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="外显号码">{{ detailData.displayNumber || '-' }}</el-descriptions-item>
          <el-descriptions-item label="号码池名称">{{ detailData.numberPoolName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="话后时长">{{ detailData.afterCallDuration ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="外呼超时">{{ detailData.callTimeout ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务状态通知">{{ detailData.statusNotify || '-' }}</el-descriptions-item>
          <el-descriptions-item label="话务通知">{{ detailData.trafficNotify || '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务状态">
            <el-tag size="small" :type="statusTag(detailData.status)">{{ statusMap[detailData.status] || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="列表总数">{{ detailData.listTotal ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="任务暂停时间">{{ formatTime(detailData.pauseStartTime) }}</el-descriptions-item>
          <el-descriptions-item label="暂停开始时间">{{ formatTime(detailData.pauseStartTime) }}</el-descriptions-item>
          <el-descriptions-item label="暂停结束时间">{{ formatTime(detailData.pauseEndTime) }}</el-descriptions-item>
        </el-descriptions>
        <div class="dialog-section" style="margin-top:16px">
          <div class="dialog-section-title">任务暂停时间列表</div>
          <el-table :data="pauseList" size="small" max-height="200">
            <el-table-column label="序号" width="60" align="center">
              <template #default="{ $index }">{{ $index + 1 }}</template>
            </el-table-column>
            <el-table-column label="暂停开始时间" width="200">
              <template #default="{ row }">{{ formatTime(row.pauseStartTime) }}</template>
            </el-table-column>
            <el-table-column label="暂停结束时间" width="200">
              <template #default="{ row }">{{ formatTime(row.pauseEndTime) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTaskList, saveTask, updateTask, deleteTask, getTaskContactList, getTaskPauseList } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', transferType: '', status: '', dateRange: null })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', dataSource: '', callStartTime: '', callEndTime: '',
  uts: null, closeTimeDate: null, transferType: '技能组', transferName: '',
  totalCount: 0, dialedCount: 0, answeredCount: 0, cruiseType: '',
  concurrency: 5, displayNumber: '', numberPoolName: '',
  afterCallDuration: 0, callTimeout: 60, statusNotify: '', trafficNotify: '',
  status: 1, callRounds: 1, currentRound: 0, completedCount: 0, listTotal: 0
})

const statusMap = { 1: '运行', 2: '暂停', 3: '关闭' }
const statusTag = (v) => v === 1 ? 'success' : v === 2 ? 'warning' : 'danger'

const rules = { name: [{ required: true, message: '请输入任务名称', trigger: 'blur' }] }

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const progressPct = (row) => {
  if (!row.totalCount) return 0
  return Math.round((row.completedCount || 0) / row.totalCount * 100)
}

const answerRate = (row) => {
  if (!row.totalCount) return 0
  return Math.round((row.answeredCount || 0) / row.totalCount * 100)
}

const answerRateClass = (row) => {
  const r = answerRate(row)
  return r >= 80 ? 'rate-high' : r >= 40 ? 'rate-mid' : 'rate-low'
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null, name: '', dataSource: '', callStartTime: '', callEndTime: '',
    uts: null, closeTimeDate: null, transferType: '技能组', transferName: '',
    totalCount: 0, dialedCount: 0, answeredCount: 0, cruiseType: '',
    concurrency: 5, displayNumber: '', numberPoolName: '',
    afterCallDuration: 0, callTimeout: 60, statusNotify: '', trafficNotify: '',
    status: 1, callRounds: 1, currentRound: 0, completedCount: 0, listTotal: 0
  })
  editPauseList.value = []
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({
        name: searchForm.name || undefined,
        transferType: searchForm.transferType || undefined,
        status: searchForm.status || undefined,
        startTime: searchForm.dateRange ? Math.floor(Number(searchForm.dateRange[0]) / 1000) : undefined,
        endTime: searchForm.dateRange ? Math.floor(Number(searchForm.dateRange[1]) / 1000) : undefined
      })
    }
    const res = await getTaskList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.transferType = ''; searchForm.status = ''; searchForm.dateRange = null; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleStart = async (row) => {
  try { await updateTask(row.id, { status: 1 }); ElMessage.success('任务已开始'); loadData() } catch { ElMessage.error('操作失败') }
}
const handlePause = async (row) => {
  try { await updateTask(row.id, { status: 2 }); ElMessage.success('任务已暂停'); loadData() } catch { ElMessage.error('操作失败') }
}
const handleComplete = async (row) => {
  try { await ElMessageBox.confirm(`确认完成任务 "${row.name}" 吗？`, '提示', { type: 'warning' }); await updateTask(row.id, { status: 3 }); ElMessage.success('任务已完成'); loadData() } catch { /* cancelled */ }
}

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const editPauseList = ref([])

const handleEdit = async (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, name: row.name || '',
    dataSource: row.dataSource || '', callStartTime: row.callStartTime || '',
    callEndTime: row.callEndTime || '', uts: row.uts || null,
    closeTimeDate: row.closeTime ? Number(row.closeTime) : null,
    transferType: row.transferType || '技能组', transferName: row.transferName || '',
    totalCount: row.totalCount ?? 0, dialedCount: row.dialedCount ?? 0,
    answeredCount: row.answeredCount ?? 0, cruiseType: row.cruiseType || '',
    concurrency: row.concurrency ?? 5, displayNumber: row.displayNumber || '',
    numberPoolName: row.numberPoolName || '', afterCallDuration: row.afterCallDuration ?? 0,
    callTimeout: row.callTimeout ?? 60, statusNotify: row.statusNotify || '',
    trafficNotify: row.trafficNotify || '', status: row.status ?? 1,
    callRounds: row.callRounds ?? 1, currentRound: row.currentRound ?? 0,
    completedCount: row.completedCount ?? 0, listTotal: row.listTotal ?? 0
  })
  dialogVisible.value = true
  try {
    const res = await getTaskPauseList(row.id)
    editPauseList.value = res.code === 0 ? (res.data || []) : []
  } catch { editPauseList.value = [] }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const data = {
      name: form.name, dataSource: form.dataSource || null,
      callStartTime: form.callStartTime || null,
      callEndTime: form.callEndTime || null,
      closeTime: form.closeTimeDate ? Math.floor(Number(form.closeTimeDate) / 1000) : null,
      transferType: form.transferType, transferName: form.transferName || null,
      totalCount: form.totalCount, dialedCount: form.dialedCount,
      answeredCount: form.answeredCount, cruiseType: form.cruiseType || null,
      concurrency: form.concurrency, displayNumber: form.displayNumber || null,
      numberPoolName: form.numberPoolName || null,
      afterCallDuration: form.afterCallDuration, callTimeout: form.callTimeout,
      statusNotify: form.statusNotify || null, trafficNotify: form.trafficNotify || null,
      status: form.status, callRounds: form.callRounds, currentRound: form.currentRound,
      completedCount: form.completedCount, listTotal: form.listTotal
    }
    if (isEdit.value) {
      await updateTask(form.id, data)
    } else {
      await saveTask(data)
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

// ==================== 联系人弹窗 ====================
const contactVisible = ref(false)
const contactTask = ref({})
const contactList = ref([])
const contactSearch = reactive({ phone: '', callStatus: '', callResult: '' })

const filteredContactList = computed(() => {
  let list = contactList.value
  if (contactSearch.phone) {
    const kw = contactSearch.phone.toLowerCase()
    list = list.filter(c => (c.phone || '').toLowerCase().includes(kw))
  }
  if (contactSearch.callStatus) {
    list = list.filter(c => c.callStatus === contactSearch.callStatus)
  }
  if (contactSearch.callResult) {
    list = list.filter(c => c.callResult === contactSearch.callResult)
  }
  return list
})

const handleContactSearch = () => { /* filteredContactList computed reacts to contactSearch */ }
const handleContactReset = () => { contactSearch.phone = ''; contactSearch.callStatus = ''; contactSearch.callResult = '' }

// ==================== 详情弹窗 ====================
const detailVisible = ref(false)
const detailData = ref({})
const pauseList = ref([])

const handleDetail = async (row) => {
  detailData.value = row
  detailVisible.value = true
  try {
    const res = await getTaskPauseList(row.id)
    pauseList.value = res.code === 0 ? (res.data || []) : []
  } catch { pauseList.value = [] }
}

const handleExportContacts = () => {
  const data = filteredContactList.value
  if (!data.length) { ElMessage.warning('无数据可导出'); return }
  const headers = ['id', 'name', '号码', '当前轮次', '坐席编号', 'uuid1', '拨打时间', '应答时间', '桥接时间']
  const keys = ['id', 'name', 'phone', 'currentRound', 'agentCode', 'uuid', 'callTime', 'answerTime', 'bridgeTime']
  const csv = [headers.join(',')]
  data.forEach(row => {
    csv.push(keys.map(k => {
      const v = k === 'callTime' || k === 'answerTime' || k === 'bridgeTime'
        ? (row[k] ? new Date(row[k] * 1000).toLocaleString('zh-CN', { hour12: false }) : '-')
        : (row[k] ?? '-')
      return `"${String(v).replace(/"/g, '""')}"`
    }).join(','))
  })
  const blob = new Blob(['﻿' + csv.join('\n')], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url; a.download = `任务联系人_${contactTask.value.name || contactTask.value.id}.csv`
  a.click(); URL.revokeObjectURL(url)
  ElMessage.success('导出成功')
}

const handleContact = async (row) => {
  contactTask.value = row
  contactSearch.phone = ''
  contactSearch.callStatus = ''
  contactSearch.callResult = ''
  contactVisible.value = true
  try {
    const res = await getTaskContactList(row.id)
    contactList.value = res.code === 0 ? (res.data || []) : []
  } catch { contactList.value = [] }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除任务 "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteTask(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.rate-high { color: #67c23a; font-weight: 600; }
.rate-mid { color: #e6a23c; font-weight: 600; }
.rate-low { color: #f56c6c; font-weight: 600; }
.contact-search-form { padding: 12px 16px; background: #f8f9fa; border-radius: 6px; margin-bottom: 4px; }
.task-detail-desc { margin-top: 4px; }
.dialog-section-title { font-size: 14px; font-weight: 600; color: #303133; padding-left: 10px; border-left: 3px solid #409eff; margin-bottom: 12px; line-height: 1.3; }
</style>
