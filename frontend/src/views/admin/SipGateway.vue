<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>SIP网关</span>
          <div class="actions">
            <el-button type="success" @click="openEdit()">新增</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border>
        <el-table-column prop="name" label="名称" width="200"/>
        <el-table-column prop="sipUri" label="SIP URI"/>
        <el-table-column prop="register" label="注册" width="120">
          <template #default="{ row }">
            <el-tag :type="row.register ? 'success' : 'info'">{{ row.register ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑SIP网关' : '新增SIP网关'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="SIP URI"><el-input v-model="form.sipUri" placeholder="sip:xxx@host:port"/></el-form-item>
        <el-form-item label="需要注册"><el-switch v-model="form.register"/></el-form-item>
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

const list = ref([])
const dialogVisible = ref(false)
const form = reactive({ id: null, name: '', sipUri: '', register: true })

const loadData = async () => {
  list.value = [{ id: 1, name: 'SIP-1', sipUri: 'sip:1000@10.0.0.10:5060', register: true }]
}

const openEdit = (row) => {
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, name: '', sipUri: '', register: true })
  dialogVisible.value = true
}

const save = async () => {
  ElMessage.success('已保存')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该网关吗？', '提示', { type: 'warning' })
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.actions { display: flex; gap: 8px; align-items: center; }
</style>


