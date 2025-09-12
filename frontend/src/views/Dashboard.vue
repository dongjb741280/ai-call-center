<template>
  <div class="dashboard">
    <!-- 顶部欢迎与许可证信息（合并为一个卡片） -->
    <div class="top-card">
      <div class="welcome-text">{{ welcomeText }}</div>
      <div class="top-meta">
        <span class="meta-item">licenseDate：{{ license.licenseDate }}</span>
        <span class="meta-item">licenseNum：{{ license.licenseNum }}</span>
        <span class="meta-item">ip地址：{{ license.ip }}</span>
      </div>
    </div>

    <!-- 统计卡片（与截图一致） -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-content only">
          <p>外呼总数</p>
          <h3>{{ stats.calloutTotal }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-content only">
          <p>呼入总数</p>
          <h3>{{ stats.callinTotal }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-content only">
          <p>坐席总数</p>
          <h3>{{ stats.totalAgents }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-content only">
          <p>坐席在线数</p>
          <h3>{{ stats.onlineAgents }}</h3>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h3>坐席维度的统计</h3>
        </div>
        <div class="chart-content">
          <v-chart :option="agentStackBarOption" style="height: 320px;" />
        </div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <h3>企业每个小时区间的统计</h3>
        </div>
        <div class="chart-content">
          <v-chart :option="companyHourlyLineOption" style="height: 320px;" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 顶部信息
const welcomeText = ref('admin[test]欢迎回来!')
const license = reactive({
  licenseDate: '2026-08-31',
  licenseNum: 20000,
  ip: '140.99.255.94'
})

// 统计数据（与四个卡片对应）
const stats = reactive({
  calloutTotal: 4,
  callinTotal: 6,
  totalAgents: 109,
  onlineAgents: 1
})

// 坐席维度堆叠柱状图
const agentStackBarOption = ref({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  legend: { data: ['loginTime', 'readyTime', 'talkTime', 'calloutCnt', 'callinAnswerCnt'] },
  grid: { left: '3%', right: '3%', bottom: '3%', containLabel: true },
  xAxis: { type: 'value' },
  yAxis: { type: 'category', data: ['1001@test'] },
  series: [
    { name: 'loginTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [726], itemStyle: { color: '#5470C6' } },
    { name: 'readyTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [94], itemStyle: { color: '#91CC75' } },
    { name: 'talkTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [2], itemStyle: { color: '#FAC858' } },
    { name: 'calloutCnt', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [1], itemStyle: { color: '#EE6666' } },
    { name: 'callinAnswerCnt', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [1], itemStyle: { color: '#73C0DE' } }
  ]
})

// 企业每小时区间折线图
const companyHourlyLineOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { data: ['agentCnt', 'callinCnt', 'calloutCnt'] },
  grid: { left: '3%', right: '3%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', boundaryGap: false, data: ['03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00'] },
  yAxis: { type: 'value' },
  series: [
    { name: 'agentCnt', type: 'line', smooth: true, data: [1,1,1,1,1,1,1,1,1,1,1,1], itemStyle: { color: '#5470C6' } },
    { name: 'callinCnt', type: 'line', smooth: true, data: [0,0,0,0,0,0,2,0,0,3,0,0], itemStyle: { color: '#91CC75' } },
    { name: 'calloutCnt', type: 'line', smooth: true, data: [0,0,0,0,0,0,0,0,0,2,0,1], itemStyle: { color: '#FAC858' } }
  ]
})

const loadData = async () => {
  // TODO: 接入后端接口替换为真实数据
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 12px;
}

.top-card {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px 16px;
  background: #e1e7f0;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 16px;
}

.welcome-text {
  font-weight: 500;
  color: #303133;
}

.top-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 20px;
  color: #606266;
}

.meta-item { white-space: nowrap; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #e9eef5;
  border-radius: 8px;
  padding: 20px;
}

.stat-content.only p {
  font-size: 14px;
  color: #909399;
  margin: 0 0 8px 0;
}

.stat-content h3 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 4px 0;
}

.stat-content p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 删除最近通话区域相关样式 */

/* 响应式 */
@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .top-card { flex-direction: column; align-items: flex-start; }
}
</style>


