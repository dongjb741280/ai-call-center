<template>
  <div class="page-card">
    <div class="page-header">
      <h2>IVR管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
    </div>

    <el-form :model="searchForm" inline class="search-form">
      <el-form-item label="IVR名称">
        <el-input v-model="searchForm.name" placeholder="IVR名称" clearable @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
          <el-option label="转接" :value="1" />
          <el-option label="咨询" :value="2" />
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

    <el-table :data="list" v-loading="loading" style="width: 100%">
      <el-table-column label="序号" width="60" align="center">
        <template #default="{ $index }">{{ (pagination.currentPage - 1) * pagination.pageSize + $index + 1 }}</template>
      </el-table-column>
      <el-table-column prop="name" label="IVR名称" min-width="200" show-overflow-tooltip />
      <el-table-column label="IVR类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="row.type === 1 ? 'primary' : 'warning'">
            {{ row.type === 1 ? '转接' : row.type === 2 ? '咨询' : '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template #default="{ row }">
          <el-tag size="small" :type="statusTag(row.status)">{{ statusMap[row.status] || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createUser" label="发布人" width="120" show-overflow-tooltip />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.cts) }}</template>
      </el-table-column>
      <el-table-column label="修改时间" width="170">
        <template #default="{ row }">{{ formatTime(row.uts) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right" align="center">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- ==================== 编辑弹窗（IVR流程设计器） ==================== -->
    <el-drawer v-model="dialogVisible" :title="isEdit ? '编辑IVR' : '新增IVR'" direction="rtl" size="90%" @close="resetForm" :close-on-click-modal="false">
      <div class="ivr-detail-wrap">
        <div class="detail-topbar">
          <el-form ref="formRef" :model="form" :rules="rules" inline class="edit-top-form">
            <el-form-item label="名称" prop="name"><el-input v-model="form.name" placeholder="IVR名称" size="small" style="width:180px" /></el-form-item>
            <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" size="small" style="width:100px"><el-option label="转接" :value="1" /><el-option label="咨询" :value="2" /></el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="form.status" size="small" style="width:110px">
                <el-option v-for="(l,v) in statusMap" :key="v" :label="l" :value="Number(v)" />
              </el-select>
            </el-form-item>
            <el-form-item label="文件名"><el-input v-model="form.ossId" size="small" style="width:160px" /></el-form-item>
            <el-form-item label="语音文件"><el-input v-model="form.voiceItem" size="small" style="width:160px" /></el-form-item>
            <el-form-item label="启动参数"><el-input v-model="form.initParams" size="small" style="width:200px" placeholder="JSON" /></el-form-item>
          </el-form>
        </div>

        <div class="detail-body">
          <div class="stencil-panel" :class="{ 'is-collapsed': editStencilCollapsed }">
            <div class="stencil-toggle" @click="editStencilCollapsed = !editStencilCollapsed">{{ editStencilCollapsed ? '▶' : '◀' }}</div>
            <div class="stencil-wrap" v-show="!editStencilCollapsed">
              <div class="stencil-title">节点类型</div>
              <div class="stencil-items">
                <div class="stencil-item" v-for="n in nodeTypes" :key="n.type" draggable="true" @dragstart="onEditDragStart($event, n)">
                  <div class="stencil-icon" :style="{ background: n.color }"><el-icon size="16"><component :is="n.icon" /></el-icon></div>
                  <span>{{ n.label }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="flow-canvas" ref="editCanvasRef" @drop="onEditDrop" @dragover.prevent @click.self="editSelectedNode = null">
            <div class="canvas-toolbar">
              <el-button size="small" @click="editAddStart" v-if="!editHasStart">开始</el-button>
              <el-button size="small" @click="editAddNode">+ 节点</el-button>
              <el-button size="small" @click="editAutoLayout">自动布局</el-button>
              <span class="node-count">{{ editFlowNodes.length }} 个节点</span>
            </div>
            <div class="canvas-area">
              <div v-for="node in editFlowNodes" :key="node.id" class="flow-node-wrap"
                :style="{ left: node.x + 'px', top: node.y + 'px' }" @click.stop="editSelectNode(node)">
                <div v-if="node.type === 'start'" class="node-view start-node"><div class="node-icon"><el-icon><VideoPlay /></el-icon></div><div class="node-content"><span>开始</span></div></div>
                <div v-else-if="node.type === 'play'" class="node-view play-node" :class="{ active: editSelectedNode?.id === node.id }"><div class="node-icon"><el-icon><Headset /></el-icon></div><div class="node-content"><span>{{ node.label || '放音' }}</span></div></div>
                <div v-else-if="node.type === 'menu'" class="node-view menu-node" :class="{ active: editSelectedNode?.id === node.id }"><div class="node-icon"><el-icon><Menu /></el-icon></div><div class="node-content"><span>{{ node.label || '菜单' }}</span></div></div>
                <div v-else-if="node.type === 'condition'" class="node-view condition-node" :class="{ active: editSelectedNode?.id === node.id }"><div class="node-icon"><el-icon><Switch /></el-icon></div><div class="node-content"><span>{{ node.label || '条件' }}</span></div></div>
                <div v-else-if="node.type === 'assign'" class="node-view assign-node" :class="{ active: editSelectedNode?.id === node.id }"><div class="node-icon"><el-icon><EditPen /></el-icon></div><div class="node-content"><span>{{ node.label || '赋值' }}</span></div></div>
                <div v-else-if="node.type === 'http'" class="node-view http-node" :class="{ active: editSelectedNode?.id === node.id }"><div class="node-icon"><el-icon><Connection /></el-icon></div><div class="node-content"><span>{{ node.label || 'HTTP' }}</span></div></div>
                <div v-else-if="node.type === 'end'" class="node-view end-node"><div class="node-icon"><el-icon><CloseBold /></el-icon></div><div class="node-content"><span>结束</span></div></div>
                <div class="node-delete" v-if="node.type !== 'start' && node.type !== 'end'" @click.stop="editRemoveNode(node)"><el-icon size="12"><Close /></el-icon></div>
              </div>
              <div v-if="!editFlowNodes.length" class="canvas-empty">拖拽左侧节点或点击上方按钮添加流程节点</div>
            </div>
          </div>

          <div class="props-panel" :class="{ 'is-collapsed': editPropsCollapsed }" v-if="editSelectedNode">
            <div class="props-toggle" @click="editPropsCollapsed = !editPropsCollapsed">{{ editPropsCollapsed ? '◀' : '▶' }}</div>
            <div class="props-wrap" v-show="!editPropsCollapsed">
              <div class="props-header"><span>节点属性</span><el-button text size="small" @click="editSelectedNode = null"><el-icon><Close /></el-icon></el-button></div>
              <el-form :model="editSelectedNode" label-width="80px" size="small">
                <el-form-item label="名称"><el-input v-model="editSelectedNode.label" /></el-form-item>
                <el-form-item label="类型"><el-tag size="small">{{ nodeTypeLabel(editSelectedNode.type) }}</el-tag></el-form-item>
                <template v-if="editSelectedNode.type === 'play'">
                  <el-form-item label="语音文件"><el-select v-model="editSelectedNode.fileId" style="width:100%"><el-option label="欢迎语" :value="1" /><el-option label="等待音乐" :value="2" /></el-select></el-form-item>
                  <el-form-item label="重复次数"><el-input-number v-model="editSelectedNode.repeat" :min="0" :max="9" style="width:100%" /></el-form-item>
                </template>
                <template v-if="editSelectedNode.type === 'menu'">
                  <el-form-item label="按键"><el-input v-model="editSelectedNode.dtmf" maxlength="1" /></el-form-item>
                  <el-form-item label="超时(秒)"><el-input-number v-model="editSelectedNode.timeout" :min="1" :max="60" style="width:100%" /></el-form-item>
                </template>
                <template v-if="editSelectedNode.type === 'condition'">
                  <el-form-item label="表达式"><el-input v-model="editSelectedNode.expression" type="textarea" :rows="2" /></el-form-item>
                </template>
                <template v-if="editSelectedNode.type === 'assign'">
                  <el-form-item label="变量名"><el-input v-model="editSelectedNode.varName" /></el-form-item>
                  <el-form-item label="变量值"><el-input v-model="editSelectedNode.varValue" /></el-form-item>
                </template>
                <template v-if="editSelectedNode.type === 'http'">
                  <el-form-item label="URL"><el-input v-model="editSelectedNode.url" /></el-form-item>
                  <el-form-item label="方法"><el-select v-model="editSelectedNode.method" style="width:100%"><el-option label="GET" value="GET" /><el-option label="POST" value="POST" /></el-select></el-form-item>
                </template>
              </el-form>
            </div>
          </div>
        </div>

        <div class="zoom-controls">
          <el-button size="small" circle @click="editZoom = Math.min(2, editZoom + 0.1)">+</el-button>
          <span class="zoom-level">{{ Math.round(editZoom * 100) }}%</span>
          <el-button size="small" circle @click="editZoom = Math.max(0.3, editZoom - 0.1)">-</el-button>
          <el-button size="small" circle @click="editZoom = 1">⊡</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-drawer>

    <!-- ==================== 详情抽屉（IVR流程设计器） ==================== -->
    <el-drawer v-model="detailVisible" :title="detail.name || 'IVR详情'" direction="rtl" size="90%" @close="detailVisible = false" :close-on-click-modal="false">
      <div class="ivr-detail-wrap">
        <div class="detail-topbar">
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="名称">{{ detail.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag size="small" :type="detail.type === 1 ? 'primary' : 'warning'">
                {{ detail.type === 1 ? '转接' : detail.type === 2 ? '咨询' : '-' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-select v-model="detail.status" size="small" style="width:120px" @change="v => updateIvr(detail.id, { status: v })">
                <el-option v-for="(label, val) in statusMap" :key="val" :label="label" :value="Number(val)" />
              </el-select>
            </el-descriptions-item>
            <el-descriptions-item label="文件名">{{ detail.ossId || '-' }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="detail-body">
          <div class="stencil-panel" :class="{ 'is-collapsed': stencilCollapsed }">
            <div class="stencil-toggle" @click="stencilCollapsed = !stencilCollapsed">{{ stencilCollapsed ? '▶' : '◀' }}</div>
            <div class="stencil-wrap" v-show="!stencilCollapsed">
              <div class="stencil-title">节点类型</div>
              <div class="stencil-items">
                <div class="stencil-item" v-for="n in nodeTypes" :key="n.type" draggable="true" @dragstart="onDragStart($event, n)">
                  <div class="stencil-icon" :style="{ background: n.color }"><el-icon size="16"><component :is="n.icon" /></el-icon></div>
                  <span>{{ n.label }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="flow-canvas" ref="canvasRef" @drop="onDrop" @dragover.prevent @click.self="selectedNode = null">
            <div class="canvas-toolbar">
              <el-button size="small" @click="handleAddStart" v-if="!hasStartNode"><el-icon><VideoPlay /></el-icon> 开始</el-button>
              <el-button size="small" @click="handleAddNode">+ 节点</el-button>
              <el-button size="small" @click="handleAutoLayout">自动布局</el-button>
              <span class="node-count">{{ flowNodes.length }} 个节点</span>
            </div>
            <div class="canvas-area">
              <div v-for="(node, index) in flowNodes" :key="node.id" class="flow-node-wrap"
                :style="{ left: node.x + 'px', top: node.y + 'px' }" @click.stop="selectNode(node)">
                <div v-if="node.type === 'start'" class="node-view start-node"><div class="node-icon"><el-icon><VideoPlay /></el-icon></div><div class="node-content"><span>开始</span></div></div>
                <div v-else-if="node.type === 'play'" class="node-view play-node" :class="{ active: selectedNode?.id === node.id }"><div class="node-icon"><el-icon><Headset /></el-icon></div><div class="node-content"><span>{{ node.label || '放音' }}</span></div></div>
                <div v-else-if="node.type === 'menu'" class="node-view menu-node" :class="{ active: selectedNode?.id === node.id }"><div class="node-icon"><el-icon><Menu /></el-icon></div><div class="node-content"><span>{{ node.label || '菜单' }}</span><span class="node-badge" v-if="node.children">{{ node.children }}</span></div></div>
                <div v-else-if="node.type === 'condition'" class="node-view condition-node" :class="{ active: selectedNode?.id === node.id }"><div class="node-icon"><el-icon><Switch /></el-icon></div><div class="node-content"><span>{{ node.label || '条件' }}</span></div></div>
                <div v-else-if="node.type === 'assign'" class="node-view assign-node" :class="{ active: selectedNode?.id === node.id }"><div class="node-icon"><el-icon><EditPen /></el-icon></div><div class="node-content"><span>{{ node.label || '赋值' }}</span></div></div>
                <div v-else-if="node.type === 'http'" class="node-view http-node" :class="{ active: selectedNode?.id === node.id }"><div class="node-icon"><el-icon><Connection /></el-icon></div><div class="node-content"><span>{{ node.label || 'HTTP' }}</span></div></div>
                <div v-else-if="node.type === 'end'" class="node-view end-node"><div class="node-icon"><el-icon><CloseBold /></el-icon></div><div class="node-content"><span>结束</span></div></div>
                <div class="node-delete" v-if="node.type !== 'start' && node.type !== 'end'" @click.stop="removeNode(node)"><el-icon size="12"><Close /></el-icon></div>
              </div>
              <div v-if="!flowNodes.length" class="canvas-empty">拖拽左侧节点或点击上方按钮添加流程节点</div>
            </div>
          </div>

          <div class="props-panel" :class="{ 'is-collapsed': propsCollapsed }" v-if="selectedNode">
            <div class="props-toggle" @click="propsCollapsed = !propsCollapsed">{{ propsCollapsed ? '◀' : '▶' }}</div>
            <div class="props-wrap" v-show="!propsCollapsed">
              <div class="props-header"><span>节点属性</span><el-button text size="small" @click="selectedNode = null"><el-icon><Close /></el-icon></el-button></div>
              <el-form :model="selectedNode" label-width="80px" size="small">
                <el-form-item label="名称"><el-input v-model="selectedNode.label" /></el-form-item>
                <el-form-item label="类型"><el-tag size="small">{{ nodeTypeLabel(selectedNode.type) }}</el-tag></el-form-item>
                <template v-if="selectedNode.type === 'play'">
                  <el-form-item label="语音文件"><el-select v-model="selectedNode.fileId" placeholder="选择" style="width:100%"><el-option label="欢迎语" :value="1" /><el-option label="等待音乐" :value="2" /></el-select></el-form-item>
                  <el-form-item label="重复次数"><el-input-number v-model="selectedNode.repeat" :min="0" :max="9" style="width:100%" /></el-form-item>
                </template>
                <template v-if="selectedNode.type === 'menu'">
                  <el-form-item label="按键"><el-input v-model="selectedNode.dtmf" placeholder="按键值" maxlength="1" /></el-form-item>
                  <el-form-item label="超时(秒)"><el-input-number v-model="selectedNode.timeout" :min="1" :max="60" style="width:100%" /></el-form-item>
                </template>
                <template v-if="selectedNode.type === 'condition'">
                  <el-form-item label="表达式"><el-input v-model="selectedNode.expression" type="textarea" :rows="2" /></el-form-item>
                </template>
                <template v-if="selectedNode.type === 'assign'">
                  <el-form-item label="变量名"><el-input v-model="selectedNode.varName" /></el-form-item>
                  <el-form-item label="变量值"><el-input v-model="selectedNode.varValue" /></el-form-item>
                </template>
                <template v-if="selectedNode.type === 'http'">
                  <el-form-item label="URL"><el-input v-model="selectedNode.url" /></el-form-item>
                  <el-form-item label="方法"><el-select v-model="selectedNode.method" style="width:100%"><el-option label="GET" value="GET" /><el-option label="POST" value="POST" /></el-select></el-form-item>
                </template>
              </el-form>
            </div>
          </div>
        </div>

        <div class="zoom-controls">
          <el-button size="small" circle @click="zoomIn">+</el-button>
          <span class="zoom-level">{{ Math.round(zoom * 100) }}%</span>
          <el-button size="small" circle @click="zoomOut">-</el-button>
          <el-button size="small" circle @click="zoom = 1">⊡</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSaveFlow" :loading="saving">保存流程</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getIvrList, saveIvr, updateIvr, deleteIvr } from '@/api/config'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const list = ref([])
const searchForm = reactive({ name: '', type: '' })
const pagination = reactive({ currentPage: 1, pageSize: 10, total: 0 })
const form = reactive({
  id: null, name: '', type: 1, status: 1,
  ossId: '', voiceItem: '', initParams: '', content: ''
})

const statusMap = { 1: '待发布', 2: '审核中', 3: '审核未通过', 4: '审核通过', 5: '已上线' }
const statusTag = (v) => v === 5 ? 'success' : v === 4 ? 'primary' : v === 2 ? 'warning' : v === 3 ? 'danger' : 'info'

const editFlowNodes = ref([])
const editSelectedNode = ref(null)
const editStencilCollapsed = ref(false)
const editPropsCollapsed = ref(false)
const editZoom = ref(1)
const editCanvasRef = ref(null)
const editHasStart = computed(() => editFlowNodes.value.some(n => n.type === 'start'))

const editAddNodeFn = (type, x, y) => {
  const labels = { play: '放音', menu: '菜单', condition: '条件', assign: '赋值', http: 'HTTP' }
  const node = { id: newId(), type, label: labels[type] || type, x, y }
  if (type === 'start') { editFlowNodes.value.unshift(node); return }
  const ei = editFlowNodes.value.findIndex(n => n.type === 'end')
  ei > -1 ? editFlowNodes.value.splice(ei, 0, node) : editFlowNodes.value.push(node)
  editAutoLayout()
}
const onEditDragStart = (e, n) => { e.dataTransfer.setData('nodeType', n.type) }
const onEditDrop = (e) => {
  const t = e.dataTransfer.getData('nodeType')
  if (!t) return
  const r = editCanvasRef.value?.getBoundingClientRect()
  if (!r) return
  editAddNodeFn(t, e.clientX - r.left, e.clientY - r.top)
}
const editAddStart = () => { if (!editHasStart.value) { editFlowNodes.value.unshift({ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }); editAutoLayout() } }
const editAddNode = () => { editAddNodeFn('play', 300, (editFlowNodes.value.length + 1) * 100); editAutoLayout() }
const editAutoLayout = () => editFlowNodes.value.forEach((n, i) => { n.x = 300; n.y = 30 + i * 110 })
const editSelectNode = (n) => { editSelectedNode.value = n; editPropsCollapsed.value = false }
const editRemoveNode = (n) => { editFlowNodes.value = editFlowNodes.value.filter(x => x.id !== n.id); if (editSelectedNode.value?.id === n.id) editSelectedNode.value = null }

const rules = {
  name: [{ required: true, message: '请输入IVR名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const formatTime = (ts) => {
  if (!ts) return '-'
  return new Date(ts * 1000).toLocaleString('zh-CN', { hour12: false })
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, name: '', type: 1, status: 1, ossId: '', voiceItem: '', initParams: '', content: '' })
  editFlowNodes.value = [{ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }, { id: newId(), type: 'end', label: '结束', x: 300, y: 360 }]
  editSelectedNode.value = null
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize,
      query: JSON.stringify({
        name: searchForm.name || undefined,
        type: searchForm.type || undefined
      })
    }
    const res = await getIvrList(params)
    if (res.code === 0) {
      list.value = res.data?.list || []
      pagination.total = res.data?.total || 0
    }
  } catch { /* empty */ }
  finally { loading.value = false }
}

const handleSearch = () => { pagination.currentPage = 1; loadData() }
const handleReset = () => { searchForm.name = ''; searchForm.type = ''; pagination.currentPage = 1; loadData() }
const handleSizeChange = () => loadData()
const handleCurrentChange = () => loadData()

const handleAdd = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, name: row.name, type: row.type ?? 1, status: row.status ?? 1,
    ossId: row.ossId || '', voiceItem: row.voiceItem || '',
    initParams: row.initParams || '', content: row.content || ''
  })
  if (row.content) {
    try {
      const parsed = JSON.parse(row.content)
      if (Array.isArray(parsed) && parsed.length) {
        editFlowNodes.value = parsed.map(n => ({ ...n, id: n.id || newId() }))
      } else { editFlowNodes.value = [{ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }, { id: newId(), type: 'end', label: '结束', x: 300, y: 360 }] }
    } catch { editFlowNodes.value = [{ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }, { id: newId(), type: 'end', label: '结束', x: 300, y: 360 }] }
  } else {
    editFlowNodes.value = [{ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }, { id: newId(), type: 'end', label: '结束', x: 300, y: 360 }]
  }
  editSelectedNode.value = null
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    const content = JSON.stringify(editFlowNodes.value.map(n => ({
      type: n.type, label: n.label, x: n.x, y: n.y,
      fileId: n.fileId, dtmf: n.dtmf, repeat: n.repeat,
      timeout: n.timeout, expression: n.expression,
      varName: n.varName, varValue: n.varValue, url: n.url, method: n.method
    })))
    const data = {
      name: form.name, type: form.type, status: form.status,
      ossId: form.ossId || null, voiceItem: form.voiceItem || null,
      initParams: form.initParams || null, content
    }
    if (isEdit.value) {
      await updateIvr(form.id, data)
    } else {
      await saveIvr(data)
    }
    ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitLoading.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除IVR "${row.name}" 吗？`, '提示', { type: 'warning' })
    await deleteIvr(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

// ==================== 详情弹窗（IVR流程设计器） ====================
const detailVisible = ref(false)
const detail = ref({})
const saving = ref(false)
const flowNodes = ref([])
const selectedNode = ref(null)
const stencilCollapsed = ref(false)
const propsCollapsed = ref(false)
const zoom = ref(1)
const canvasRef = ref(null)

const nodeTypes = [
  { type: 'play', label: '放音', color: '#409eff', icon: 'Headset' },
  { type: 'menu', label: '菜单', color: '#667eea', icon: 'Menu' },
  { type: 'condition', label: '条件', color: '#e6a23c', icon: 'Switch' },
  { type: 'assign', label: '赋值', color: '#909399', icon: 'EditPen' },
  { type: 'http', label: 'HTTP', color: '#67c23a', icon: 'Connection' },
]
const hasStartNode = computed(() => flowNodes.value.some(n => n.type === 'start'))
const nodeTypeLabel = (t) => {
  const m = { start: '开始', end: '结束', play: '放音', menu: '菜单', condition: '条件', assign: '赋值', http: 'HTTP' }
  return m[t] || t
}

let nodeIdCtr = 0
const newId = () => `n_${Date.now()}_${++nodeIdCtr}`

const initFlow = () => {
  flowNodes.value = [
    { id: newId(), type: 'start', label: '开始', x: 300, y: 30 },
    { id: newId(), type: 'end', label: '结束', x: 300, y: 360 },
  ]
}

const handleDetail = async (row) => {
  detail.value = row
  detailVisible.value = true
  const res = await getIvrList({ pageNum: 1, pageSize: 1, query: JSON.stringify({ id: row.id }) })
  if (res.code === 0 && res.data?.list?.[0]?.content) {
    try {
      const parsed = JSON.parse(res.data.list[0].content)
      if (Array.isArray(parsed) && parsed.length) {
        flowNodes.value = parsed.map(n => ({ ...n, id: n.id || newId() }))
        return
      }
    } catch { /* use default */ }
  }
  initFlow()
}

const onDragStart = (e, node) => { e.dataTransfer.setData('nodeType', node.type) }
const onDrop = (e) => {
  const type = e.dataTransfer.getData('nodeType')
  if (!type) return
  const rect = canvasRef.value?.getBoundingClientRect()
  if (!rect) return
  addNode(type, e.clientX - rect.left, e.clientY - rect.top)
}
const addNode = (type, x, y) => {
  const labels = { play: '放音', menu: '菜单', condition: '条件', assign: '赋值', http: 'HTTP' }
  const node = { id: newId(), type, label: labels[type] || type, x, y }
  if (type === 'start') { flowNodes.value.unshift(node); return }
  const ei = flowNodes.value.findIndex(n => n.type === 'end')
  ei > -1 ? flowNodes.value.splice(ei, 0, node) : flowNodes.value.push(node)
  handleAutoLayout()
}
const handleAddStart = () => { if (!hasStartNode.value) { flowNodes.value.unshift({ id: newId(), type: 'start', label: '开始', x: 300, y: 30 }); handleAutoLayout() } }
const handleAddNode = () => { addNode('play', 300, (flowNodes.value.length + 1) * 100); handleAutoLayout() }
const handleAutoLayout = () => flowNodes.value.forEach((n, i) => { n.x = 300; n.y = 30 + i * 110 })
const selectNode = (n) => { selectedNode.value = n; propsCollapsed.value = false }
const removeNode = (n) => { flowNodes.value = flowNodes.value.filter(x => x.id !== n.id); if (selectedNode.value?.id === n.id) selectedNode.value = null }
const zoomIn = () => { if (zoom.value < 2) zoom.value += 0.1 }
const zoomOut = () => { if (zoom.value > 0.3) zoom.value -= 0.1 }

const handleSaveFlow = async () => {
  saving.value = true
  try {
    const content = JSON.stringify(flowNodes.value.map(n => ({
      id: n.id, type: n.type, label: n.label, x: n.x, y: n.y,
      fileId: n.fileId, dtmf: n.dtmf, repeat: n.repeat,
      timeout: n.timeout, expression: n.expression,
      varName: n.varName, varValue: n.varValue, url: n.url, method: n.method
    })))
    await updateIvr(detail.value.id, { content })
    ElMessage.success('流程已保存')
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
.search-form { margin-bottom: 20px; padding: 20px; background: #f8f9fa; border-radius: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }

/* ====== IVR 流程设计器 ====== */
.ivr-detail-wrap { display: flex; flex-direction: column; height: calc(100vh - 110px); }
.detail-topbar { padding: 12px 16px; background: #fff; border-bottom: 1px solid #e4e7ed; }
.detail-body { display: flex; flex: 1; overflow: hidden; }
.stencil-panel { position: relative; width: 160px; background: #fff; border-right: 1px solid #e4e7ed; transition: width .25s; flex-shrink: 0; }
.stencil-panel.is-collapsed { width: 24px; }
.stencil-toggle { position: absolute; right: 4px; top: 8px; cursor: pointer; font-size: 12px; color: #909399; z-index: 1; }
.stencil-wrap { padding: 12px 10px; }
.stencil-title { font-size: 13px; font-weight: 600; color: #303133; margin-bottom: 10px; }
.stencil-items { display: flex; flex-direction: column; gap: 5px; }
.stencil-item { display: flex; align-items: center; gap: 6px; padding: 6px 8px; border-radius: 4px; cursor: grab; border: 1px solid #e4e7ed; font-size: 12px; }
.stencil-item:hover { border-color: #409eff; background: #ecf5ff; }
.stencil-icon { width: 24px; height: 24px; border-radius: 4px; display: flex; align-items: center; justify-content: center; color: #fff; flex-shrink: 0; }

.flow-canvas { flex: 1; overflow: auto; background: #f5f6f8; position: relative; }
.canvas-toolbar { display: flex; align-items: center; gap: 6px; padding: 8px 12px; background: #fff; border-bottom: 1px solid #ebeef5; position: sticky; top: 0; z-index: 10; }
.node-count { font-size: 12px; color: #909399; margin-left: auto; }
.canvas-area { position: relative; min-height: calc(100% - 40px); }
.canvas-empty { display: flex; align-items: center; justify-content: center; height: 300px; color: #c0c4cc; font-size: 14px; }

.flow-node-wrap { position: absolute; cursor: pointer; user-select: none; }
.node-view { display: flex; align-items: center; background: #fff; box-shadow: 0 2px 4px rgba(20,26,40,.12); border-radius: 6px; width: 170px; height: 40px; overflow: hidden; transition: all .25s; position: relative; }
.node-view:hover { box-shadow: 0 4px 12px rgba(0,0,0,.15); }
.node-view::before { content: ""; position: absolute; left: 0; top: 0; bottom: 0; width: 4px; }
.start-node::before { background: linear-gradient(180deg, #67c23a, #43e97b); }
.end-node::before { background: linear-gradient(180deg, #f56c6c, #fa709a); }
.play-node::before { background: linear-gradient(180deg, #409eff, #36a3f7); }
.menu-node::before { background: linear-gradient(180deg, #667eea, #764ba2); }
.condition-node::before { background: linear-gradient(180deg, #e6a23c, #f39c12); }
.assign-node::before { background: linear-gradient(180deg, #909399, #b0b3b8); }
.http-node::before { background: linear-gradient(180deg, #67c23a, #85ce61); }
.node-view.active { box-shadow: 0 0 0 2px #409eff; }
.node-icon { width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; color: #606266; flex-shrink: 0; }
.node-content { flex: 1; display: flex; justify-content: space-between; align-items: center; padding: 0 10px; font-size: 12px; color: #303133; }
.node-badge { background: #409eff; color: #fff; border-radius: 10px; padding: 0 6px; font-size: 10px; }
.node-delete { position: absolute; top: -8px; right: -8px; width: 18px; height: 18px; border-radius: 50%; background: #f56c6c; color: #fff; display: none; align-items: center; justify-content: center; cursor: pointer; }
.flow-node-wrap:hover .node-delete { display: flex; }

.props-panel { position: relative; width: 260px; background: #fff; border-left: 1px solid #e4e7ed; transition: width .25s; flex-shrink: 0; overflow-y: auto; }
.props-panel.is-collapsed { width: 24px; }
.props-toggle { position: absolute; left: 4px; top: 8px; cursor: pointer; font-size: 12px; color: #909399; z-index: 1; }
.props-wrap { padding: 12px 10px; }
.props-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; font-size: 14px; font-weight: 600; color: #303133; }

.zoom-controls { display: flex; align-items: center; justify-content: center; gap: 6px; padding: 6px; background: #fff; border-top: 1px solid #e4e7ed; }
.zoom-level { font-size: 12px; color: #909399; min-width: 36px; text-align: center; }
</style>
