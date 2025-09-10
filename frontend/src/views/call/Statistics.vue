<template>
  <div class="statistics">
    <div class="page-header">
      <h2>呼叫统计</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Phone /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ overview.totalCalls }}</h3>
          <p>总通话数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ overview.answeredCalls }}</h3>
          <p>已接通数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ overview.avgDuration }}</h3>
          <p>平均通话时长</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ overview.answerRate }}%</h3>
          <p>接通率</p>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-card">
        <div class="chart-header">
          <h3>通话趋势</h3>
          <el-radio-group v-model="trendPeriod" size="small" @change="handleTrendChange">
            <el-radio-button label="hour">按小时</el-radio-button>
            <el-radio-button label="day">按天</el-radio-button>
            <el-radio-button label="week">按周</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-content">
          <v-chart :option="trendOption" style="height: 400px;" />
        </div>
      </div>
      
      <div class="chart-card">
        <div class="chart-header">
          <h3>通话类型分布</h3>
        </div>
        <div class="chart-content">
          <v-chart :option="typeDistributionOption" style="height: 400px;" />
        </div>
      </div>
    </div>

    <!-- 坐席统计 -->
    <div class="agent-stats">
      <div class="section-header">
        <h3>坐席统计</h3>
      </div>
      <el-table :data="agentStats" v-loading="agentLoading" style="width: 100%">
        <el-table-column prop="agentKey" label="坐席工号" width="120" />
        <el-table-column prop="agentName" label="坐席姓名" width="120" />
        <el-table-column prop="totalCalls" label="总通话数" width="100" />
        <el-table-column prop="answeredCalls" label="已接通数" width="100" />
        <el-table-column prop="answerRate" label="接通率" width="100">
          <template #default="{ row }">
            {{ row.answerRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="avgDuration" label="平均时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.avgDuration) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalDuration" label="总时长" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.totalDuration) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { getCallStatistics } from '@/api/call'

use([
  CanvasRenderer,
  LineChart,
  BarChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const dateRange = ref([])
const trendPeriod = ref('day')
const agentLoading = ref(false)

// 统计概览
const overview = reactive({
  totalCalls: 0,
  answeredCalls: 0,
  avgDuration: '0:00',
  answerRate: 0
})

// 坐席统计
const agentStats = ref([])

// 通话趋势图表配置
const trendOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['呼入', '呼出', '接通数']
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: []
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '呼入',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      }
    },
    {
      name: '呼出',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      }
    },
    {
      name: '接通数',
      type: 'line',
      data: [],
      smooth: true,
      itemStyle: {
        color: '#E6A23C'
      }
    }
  ]
})

// 通话类型分布图表配置
const typeDistributionOption = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '通话类型',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 335, name: '呼入' },
        { value: 310, name: '呼出' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 格式化通话时长
const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    const params = {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      period: trendPeriod.value
    }
    
    const response = await getCallStatistics(params)
    const data = response.data || {}
    
    // 更新概览数据
    Object.assign(overview, {
      totalCalls: data.totalCalls || 0,
      answeredCalls: data.answeredCalls || 0,
      avgDuration: formatDuration(data.avgDuration || 0),
      answerRate: data.answerRate || 0
    })
    
    // 更新趋势图表
    updateTrendChart(data.trendData || [])
    
    // 更新类型分布图表
    updateTypeDistributionChart(data.typeDistribution || [])
    
    // 更新坐席统计
    agentStats.value = data.agentStats || []
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}

// 更新趋势图表
const updateTrendChart = (data) => {
  const xData = data.map(item => item.time)
  const inboundData = data.map(item => item.inbound || 0)
  const outboundData = data.map(item => item.outbound || 0)
  const answeredData = data.map(item => item.answered || 0)
  
  trendOption.value.xAxis.data = xData
  trendOption.value.series[0].data = inboundData
  trendOption.value.series[1].data = outboundData
  trendOption.value.series[2].data = answeredData
}

// 更新类型分布图表
const updateTypeDistributionChart = (data) => {
  typeDistributionOption.value.series[0].data = data
}

// 日期变化
const handleDateChange = () => {
  loadStatistics()
}

// 趋势周期变化
const handleTrendChange = () => {
  loadStatistics()
}

// 刷新
const handleRefresh = () => {
  loadStatistics()
}

// 初始化日期范围（默认最近7天）
const initDateRange = () => {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - 7)
  
  dateRange.value = [
    start.toISOString().split('T')[0],
    end.toISOString().split('T')[0]
  ]
}

onMounted(() => {
  initDateRange()
  loadStatistics()
})
</script>

<style scoped>
.statistics {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
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

.charts-section {
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

.agent-stats {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>


