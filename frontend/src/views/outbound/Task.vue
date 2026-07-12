<template>
  <div class="page-card">
    <div class="page-header">
      <h2>任务管理</h2>
      <el-button type="primary"><el-icon><Plus /></el-icon> 新建任务</el-button>
    </div>
    <el-empty description="暂无外呼任务" />
  </div>
</template>
<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
</style>
