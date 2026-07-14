<template>
  <div class="dashboard">
    <!-- 欢迎栏 -->
    <div class="welcome-bar">
      <div class="welcome-greeting">
        <h2>{{ welcomeText }}</h2>
        <p>欢迎回到管理平台，祝您工作愉快</p>
      </div>
      <div class="license-info">
        <div class="license-item">
          <span class="license-label">许可证日期</span>
          <span class="license-value">{{ license.licenseDate }}</span>
        </div>
        <div class="license-divider"></div>
        <div class="license-item">
          <span class="license-label">授权数量</span>
          <span class="license-value">{{ license.licenseNum }}</span>
        </div>
        <div class="license-divider"></div>
        <div class="license-item">
          <span class="license-label">IP 地址</span>
          <span class="license-value">{{ license.ip }}</span>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: #eef2ff; color: #6366f1;">
          <el-icon size="24"><PhoneFilled /></el-icon>
        </div>
        <div class="stat-body">
          <p>外呼总数</p>
          <h3>{{ stats.calloutTotal }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #ecfdf5; color: #10b981;">
          <el-icon size="24"><PhoneFilled /></el-icon>
        </div>
        <div class="stat-body">
          <p>呼入总数</p>
          <h3>{{ stats.callinTotal }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #eff6ff; color: #3b82f6;">
          <el-icon size="24"><User /></el-icon>
        </div>
        <div class="stat-body">
          <p>坐席总数</p>
          <h3>{{ stats.totalAgents }}</h3>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: #fffbeb; color: #f59e0b;">
          <el-icon size="24"><UserFilled /></el-icon>
        </div>
        <div class="stat-body">
          <p>坐席在线数</p>
          <h3>{{ stats.onlineAgents }}</h3>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h3>坐席维度统计</h3>
          <span class="chart-subtitle">各坐席通话数据分析</span>
        </div>
        <div class="chart-content">
          <v-chart :option="agentStackBarOption" style="height: 320px;" />
        </div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <h3>企业时段统计</h3>
          <span class="chart-subtitle">按小时区间的话务分布</span>
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
  legend: { bottom: 0, textStyle: { color: '#94a3b8' } },
  grid: { left: '3%', right: '4%', bottom: '12%', top: '3%', containLabel: true },
  xAxis: { type: 'value', axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' } },
  yAxis: { type: 'category', data: ['1001@test'], axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' } },
  series: [
    { name: 'loginTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [726], itemStyle: { color: '#6366f1', borderRadius: [0, 4, 4, 0] } },
    { name: 'readyTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [94], itemStyle: { color: '#10b981' } },
    { name: 'talkTime', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [2], itemStyle: { color: '#f59e0b' } },
    { name: 'calloutCnt', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [1], itemStyle: { color: '#ef4444' } },
    { name: 'callinAnswerCnt', type: 'bar', stack: 'total', emphasis: { focus: 'series' }, data: [1], itemStyle: { color: '#3b82f6' } }
  ]
})

// 企业每小时区间折线图
const companyHourlyLineOption = ref({
  tooltip: { trigger: 'axis' },
  legend: { bottom: 0, textStyle: { color: '#94a3b8' } },
  grid: { left: '3%', right: '4%', bottom: '12%', top: '3%', containLabel: true },
  xAxis: { type: 'category', boundaryGap: false, data: ['03:00','04:00','05:00','06:00','07:00','08:00','09:00','10:00','11:00','12:00','13:00','14:00'], axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' } },
  yAxis: { type: 'value', axisLine: { lineStyle: { color: '#e2e8f0' } }, axisLabel: { color: '#94a3b8' }, splitLine: { lineStyle: { color: '#f1f5f9' } } },
  series: [
    { name: 'agentCnt', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, data: [1,1,1,1,1,1,1,1,1,1,1,1], itemStyle: { color: '#6366f1' }, lineStyle: { width: 3 }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(99,102,241,0.15)' }, { offset: 1, color: 'rgba(99,102,241,0)' }] } } },
    { name: 'callinCnt', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, data: [0,0,0,0,0,0,2,0,0,3,0,0], itemStyle: { color: '#10b981' }, lineStyle: { width: 3 }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(16,185,129,0.15)' }, { offset: 1, color: 'rgba(16,185,129,0)' }] } } },
    { name: 'calloutCnt', type: 'line', smooth: true, symbol: 'circle', symbolSize: 6, data: [0,0,0,0,0,0,0,0,0,2,0,1], itemStyle: { color: '#f59e0b' }, lineStyle: { width: 3 }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(245,158,11,0.15)' }, { offset: 1, color: 'rgba(245,158,11,0)' }] } } }
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
}

/* ---- Welcome Bar ---- */
.welcome-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px 24px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color-light);
}

.welcome-greeting h2 {
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
  margin: 0;
}

.welcome-greeting p {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 4px 0 0 0;
}

.license-info {
  display: flex;
  align-items: center;
  gap: 0;
  background: var(--bg-body);
  border-radius: var(--radius-md);
  padding: 10px 20px;
}

.license-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 16px;
}

.license-label {
  font-size: 11px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.license-value {
  font-size: var(--font-size-sm);
  font-weight: 600;
  color: var(--text-primary);
  margin-top: 2px;
}

.license-divider {
  width: 1px;
  height: 28px;
  background: var(--border-color);
}

/* ---- Stats Grid ---- */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color-light);
  transition: all var(--transition-base);
  cursor: default;
}

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-body p {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  margin: 0 0 4px 0;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.stat-body h3 {
  font-size: var(--font-size-4xl);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

/* ---- Charts Grid ---- */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color-light);
}

.chart-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 16px;
}

.chart-header h3 {
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.chart-subtitle {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* ---- Responsive ---- */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .welcome-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .license-info {
    flex-wrap: wrap;
    gap: 8px;
  }

  .license-divider {
    display: none;
  }
}
</style>


