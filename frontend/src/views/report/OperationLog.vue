<template>
  <div class="page-card">
    <div class="page-header"><h2>操作日志</h2></div>
    <el-empty description="暂无数据" />
  </div>
</template>
<script setup>
const title = '操作日志'
</script>
<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
</style>
