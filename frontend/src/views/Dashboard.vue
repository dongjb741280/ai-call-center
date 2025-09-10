<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h2>仪表板</h2>
      <p>欢迎使用AI呼叫中心管理系统</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalAgents }}</h3>
          <p>总坐席数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Phone /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.totalCalls }}</h3>
          <p>今日通话</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.avgCallDuration }}</h3>
          <p>平均通话时长</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-content">
          <h3>{{ stats.callSuccessRate }}%</h3>
          <p>通话成功率</p>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <h3>通话趋势</h3>
          <el-radio-group v-model="callTrendPeriod" size="small">
            <el-radio-button label="today">今日</el-radio-button>
            <el-radio-button label="week">本周</el-radio-button>
            <el-radio-button label="month">本月</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-content">
          <v-chart :option="callTrendOption" style="height: 300px;" />
        </div>
      </div>
      
      <div class="chart-card">
        <div class="chart-header">
          <h3>坐席状态分布</h3>
        </div>
        <div class="chart-content">
          <v-chart :option="agentStatusOption" style="height: 300px;" />
        </div>
      </div>
    </div>

    <!-- 最近通话记录 -->
    <div class="recent-calls">
      <div class="section-header">
        <h3>最近通话记录</h3>
        <el-button type="primary" @click="$router.push('/call/logs')">
          查看全部
        </el-button>
      </div>
      <el-table :data="recentCalls" style="width: 100%">
        <el-table-column prop="callId" label="通话ID" width="120" />
        <el-table-column prop="agentName" label="坐席" width="100" />
        <el-table-column prop="phoneNumber" label="电话号码" width="130" />
        <el-table-column prop="callType" label="通话类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.callType === '呼入' ? 'success' : 'primary'">
              {{ row.callType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="通话时长" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
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
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

const callTrendPeriod = ref('today')

// 统计数据
const stats = reactive({
  totalAgents: 0,
  totalCalls: 0,
  avgCallDuration: '0:00',
  callSuccessRate: 0
})

// 最近通话记录
const recentCalls = ref([])

// 通话趋势图表配置
const callTrendOption = ref({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['呼入', '呼出']
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
    data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      name: '呼入',
      type: 'line',
      data: [12, 8, 15, 25, 18, 22],
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      }
    },
    {
      name: '呼出',
      type: 'line',
      data: [8, 12, 18, 20, 15, 10],
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      }
    }
  ]
})

// 坐席状态分布图表配置
const agentStatusOption = ref({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '坐席状态',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 35, name: '空闲' },
        { value: 20, name: '忙碌' },
        { value: 15, name: '通话中' },
        { value: 10, name: '离线' }
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

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    '已接通': 'success',
    '未接通': 'warning',
    '通话中': 'primary',
    '已挂断': 'info'
  }
  return statusMap[status] || 'info'
}

// 加载数据
const loadData = async () => {
  // 模拟数据
  stats.totalAgents = 80
  stats.totalCalls = 156
  stats.avgCallDuration = '3:45'
  stats.callSuccessRate = 85
  
  recentCalls.value = [
    {
      callId: 'C20231201001',
      agentName: '张三',
      phoneNumber: '138****1234',
      callType: '呼入',
      duration: '2:30',
      status: '已接通',
      createTime: '2023-12-01 14:30:25'
    },
    {
      callId: 'C20231201002',
      agentName: '李四',
      phoneNumber: '139****5678',
      callType: '呼出',
      duration: '1:45',
      status: '已接通',
      createTime: '2023-12-01 14:25:10'
    },
    {
      callId: 'C20231201003',
      agentName: '王五',
      phoneNumber: '137****9012',
      callType: '呼入',
      duration: '0:00',
      status: '未接通',
      createTime: '2023-12-01 14:20:15'
    }
  ]
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.dashboard-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.stats-grid {
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

.recent-calls {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  .charts-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>


