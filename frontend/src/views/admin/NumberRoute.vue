<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>号码路由</span>
          <div class="actions">
            <el-input v-model="query.keyword" placeholder="输入号码前缀" clearable style="width: 220px" @keyup.enter="loadData" />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" @click="openEdit()">新增</el-button>
          </div>
        </div>
      </template>
      <el-table :data="list" border>
        <el-table-column prop="prefix" label="前缀" width="140"/>
        <el-table-column prop="route" label="路由目标"/>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑路由' : '新增路由'">
      <el-form :model="form" label-width="90px">
        <el-form-item label="前缀"><el-input v-model="form.prefix"/></el-form-item>
        <el-form-item label="路由目标"><el-input v-model="form.route"/></el-form-item>
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
const query = reactive({ keyword: '' })
const dialogVisible = ref(false)
const form = reactive({ id: null, prefix: '', route: '' })

const loadData = async () => {
  list.value = [{ id: 1, prefix: '010', route: 'GW-1' }]
}

const openEdit = (row) => {
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: null, prefix: '', route: '' })
  dialogVisible.value = true
}

const save = async () => {
  ElMessage.success('已保存')
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该路由吗？', '提示', { type: 'warning' })
  ElMessage.success('已删除')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.actions { display: flex; gap: 8px; align-items: center; }
</style>


