<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>网关管理</span>
          <div class="actions">
            <el-input v-model="query.keyword" placeholder="搜索名称/IP" clearable style="width: 220px" @keyup.enter="loadData" />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" @click="openEdit()">新增</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border style="width: 100%">
        <el-table-column prop="name" label="名称" width="180" />
        <el-table-column prop="ip" label="IP地址" width="160" />
        <el-table-column prop="port" label="端口" width="100" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ONLINE' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pager">
        <el-pagination
          background
          layout="prev, pager, next, jumper"
          :total="total"
          :page-size="query.pageSize"
          v-model:current-page="query.pageNum"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑网关' : '新增网关'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="IP地址"><el-input v-model="form.ip" /></el-form-item>
        <el-form-item label="端口"><el-input v-model.number="form.port" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
  
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 预留API：替换为实际后端接口
const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })

const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', ip: '', port: 5060 })

const loadData = async () => {
  // TODO: 调用后端接口获取列表
  // 临时数据占位
  list.value = [
    { id: 1, name: 'GW-1', ip: '10.0.0.10', port: 5060, status: 'ONLINE' }
  ]
  total.value = 1
}

const openEdit = (row) => {
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, name: '', ip: '', port: 5060 })
  dialogVisible.value = true
}

const save = async () => {
  // TODO: 调用保存接口
  ElMessage.success('已保存')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确认删除网关 ${row.name} 吗？`, '提示', { type: 'warning' })
  // TODO: 调用删除接口
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.actions { display: flex; gap: 8px; align-items: center; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>


