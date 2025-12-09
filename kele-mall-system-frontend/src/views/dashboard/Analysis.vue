<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as echarts from 'echarts'
import { TrendCharts, Wallet, ShoppingBag, User } from '@element-plus/icons-vue'
import { doGet } from '../../http/httpRequest'

const chartRef = ref(null)
let myChart: echarts.ECharts | null = null
const timeRange = ref('today') // 绑定单选框：today | week

// 默认占位数据，防止页面抖动
const stats = ref([
  { title: '总成交额 (GMV)', val: 'Loading...', icon: Wallet, color: 'bg-blue-500', trend: '0%' },
  { title: '今日订单', val: 'Loading...', icon: ShoppingBag, color: 'bg-purple-500', trend: '0%' },
  { title: '新增会员', val: 'Loading...', icon: User, color: 'bg-green-500', trend: '0%' },
  { title: '转化率', val: 'Loading...', icon: TrendCharts, color: 'bg-orange-500', trend: '0%' },
])

// 1. 获取统计卡片数据
const loadStats = () => {
  doGet('/admin/stats/summary').then((res: any) => {
    if (res.code === 200 && res.data) {
      console.log("统计卡片数据:",res.data)
      const d = res.data
      stats.value = [
        { title: '总成交额 (GMV)', val: `¥${d.totalGmv}`, icon: Wallet, color: 'bg-blue-500', trend: d.gmvTrend },
        { title: '今日订单', val: d.todayOrders, icon: ShoppingBag, color: 'bg-purple-500', trend: d.orderTrend },
        { title: '新增会员', val: d.newUsers, icon: User, color: 'bg-green-500', trend: d.userTrend },
        { title: '转化率', val: `${d.conversionRate}%`, icon: TrendCharts, color: 'bg-orange-500', trend: d.rateTrend },
      ]
    }
  })
}

// 2. 获取图表数据
const loadChartData = () => {
  if (!myChart) return
  myChart.showLoading({ text: '加载中...', color: '#3D5A80', textColor: '#fff', maskColor: 'rgba(31, 43, 62, 0.8)' })
  
  doGet('/admin/stats/traffic', { type: timeRange.value }).then((res: any) => {
    if (res.code === 200 && res.data) {
      renderChart(res.data.xAxis, res.data.series)
    }
  }).finally(() => {
    myChart?.hideLoading()
  })
}

// 渲染图表
const renderChart = (xAxisData: string[], seriesData: number[]) => {
  myChart?.setOption({
    tooltip: { 
      trigger: 'axis',
      backgroundColor: 'rgba(31, 43, 62, 0.9)', 
      borderColor: '#374357',
      textStyle: { color: '#fff' }
    },
    grid: { left: '2%', right: '2%', bottom: '2%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      data: xAxisData,
      axisLine: { lineStyle: { color: '#4d648d' } },
      axisLabel: { color: '#e0e0e0' }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#374357', type: 'dashed' } },
      axisLabel: { color: '#e0e0e0' }
    },
    series: [{
      name: '流量',
      data: seriesData,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      showSymbol: false,
      itemStyle: { color: '#3D5A80' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(61, 90, 128, 0.6)' },
          { offset: 1, color: 'rgba(61, 90, 128, 0)' }
        ])
      }
    }]
  })
}

// 监听单选框变化 -> 刷新图表
watch(timeRange, () => {
  loadChartData()
})

onMounted(() => {
  // 加载卡片数据
  loadStats()

  // 初始化图表
  if (chartRef.value) {
    myChart = echarts.init(chartRef.value)
    loadChartData() // 加载图表数据
    window.addEventListener('resize', resizeChart)
  }
})

onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose()
  }
  window.removeEventListener('resize', resizeChart)
})

const resizeChart = () => myChart?.resize()
</script>

<template>
  <div>
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
      <div v-for="(item, i) in stats" :key="i" class="bg-bg-200 p-6 rounded-xl border border-bg-300 flex items-center justify-between hover:border-accent-100 transition-colors cursor-pointer group">
        <div>
          <div class="text-text-200 text-sm mb-1">{{ item.title }}</div>
          <div class="text-2xl font-bold text-white font-mono mb-1">{{ item.val }}</div>
          <div :class="item.trend.includes('-') ? 'text-red-400' : 'text-green-400'" class="text-xs flex items-center gap-1">
             {{ item.trend }} 
             <span class="text-text-200 opacity-60">同比昨日</span>
          </div>
        </div>
        <div :class="`w-12 h-12 rounded-lg flex items-center justify-center text-white ${item.color} bg-opacity-20 group-hover:scale-110 transition-transform`">
          <el-icon :size="24" :class="`text-${item.color.split('-')[1]}-400`"><component :is="item.icon" /></el-icon>
        </div>
      </div>
    </div>

    <div class="bg-bg-200 p-6 rounded-xl border border-bg-300">
      <div class="flex justify-between items-center mb-6">
        <div>
           <h3 class="text-lg font-bold text-white">全渠道流量趋势</h3>
           <p class="text-xs text-text-200 mt-1">统计全站 UV (访客数) 与 PV (浏览量) 走势</p>
        </div>
        
        <el-radio-group v-model="timeRange" size="small">
          <el-radio-button label="today">今日</el-radio-button>
          <el-radio-button label="week">本周</el-radio-button>
        </el-radio-group>
      </div>
      
      <div ref="chartRef" class="w-full h-[350px]"></div>
    </div>
  </div>
</template>

<style scoped>
/* 局部适配 Radio Button 深色样式 */
:deep(.el-radio-button__inner) {
  background-color: #1f2b3e;
  border-color: #374357;
  color: #e0e0e0;
}
:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #3D5A80;
  border-color: #3D5A80;
  color: white;
  box-shadow: -1px 0 0 0 #3D5A80;
}
</style>