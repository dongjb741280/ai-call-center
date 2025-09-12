<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>号码归属地</span>
          <div class="actions">
            <el-input v-model="query.number" placeholder="输入号码查询" clearable style="width: 220px" @keyup.enter="loadData" />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-upload :show-file-list="false" accept=".csv,.xlsx"><el-button>导入</el-button></el-upload>
          </div>
        </div>
      </template>
      <el-table :data="list" border>
        <el-table-column prop="number" label="号段/号码" width="200" />
        <el-table-column prop="province" label="省份" width="120" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="operator" label="运营商" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const query = reactive({ number: '' })
const list = ref([])

const loadData = async () => {
  list.value = [ { number: '138', province: '北京', city: '北京', operator: '移动' } ]
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.actions { display: flex; gap: 8px; align-items: center; }
</style>


