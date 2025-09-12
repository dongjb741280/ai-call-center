<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>AI引擎</span>
          <div class="actions">
            <el-button type="success" @click="openEdit()">新增</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border>
        <el-table-column prop="name" label="名称" width="200"/>
        <el-table-column prop="provider" label="提供商" width="160"/>
        <el-table-column prop="apiBase" label="API地址"/>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑引擎' : '新增引擎'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="名称"><el-input v-model="form.name"/></el-form-item>
        <el-form-item label="提供商"><el-input v-model="form.provider"/></el-form-item>
        <el-form-item label="API地址"><el-input v-model="form.apiBase"/></el-form-item>
        <el-form-item label="密钥"><el-input v-model="form.apiKey" type="password" show-password/></el-form-item>
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
const form = reactive({ id: null, name: '', provider: '', apiBase: '', apiKey: '' })

const loadData = async () => {
  list.value = [{ id: 1, name: 'OpenAI', provider: 'OpenAI', apiBase: 'https://api.openai.com' }]
}

const openEdit = (row) => {
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, name: '', provider: '', apiBase: '', apiKey: '' })
  dialogVisible.value = true
}

const save = async () => {
  ElMessage.success('已保存')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该引擎吗？', '提示', { type: 'warning' })
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.actions { display: flex; gap: 8px; align-items: center; }
</style>


