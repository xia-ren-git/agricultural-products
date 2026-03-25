<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: item.color }">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-label">{{ item.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
            </div>
          </template>
          <div ref="salesChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>分类分布</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>订单状态</span>
            </div>
          </template>
          <div ref="orderStatusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>用户增长</span>
            </div>
          </template>
          <div ref="userGrowthChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热销产品</span>
            </div>
          </template>
          <div ref="hotProductsChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>营收统计</span>
            </div>
          </template>
          <div class="revenue-container">
            <div class="revenue-item" v-for="item in revenueData" :key="item.label">
              <div class="revenue-label">{{ item.label }}</div>
              <div class="revenue-value">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import * as echarts from 'echarts'
import { User, Goods, List, Grid } from '@element-plus/icons-vue'
import { getOverview, getSalesTrend, getCategoryDistribution, getOrderStatus, getUserGrowth, getHotProducts, getRevenue } from '@/api/statistics'

const statCards = ref([
  { key: 'user', label: '用户总数', value: 0, icon: markRaw(User), color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { key: 'product', label: '产品总数', value: 0, icon: markRaw(Goods), color: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' },
  { key: 'order', label: '订单总数', value: 0, icon: markRaw(List), color: 'linear-gradient(135deg, #ee0979 0%, #ff6a00 100%)' },
  { key: 'category', label: '分类总数', value: 0, icon: markRaw(Grid), color: 'linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%)' }
])

const revenueData = ref([
  { label: '今日营收', value: '¥0' },
  { label: '本周营收', value: '¥0' },
  { label: '本月营收', value: '¥0' },
  { label: '本年营收', value: '¥0' }
])

const salesChartRef = ref(null)
const categoryChartRef = ref(null)
const orderStatusChartRef = ref(null)
const userGrowthChartRef = ref(null)
const hotProductsChartRef = ref(null)

let salesChart = null
let categoryChart = null
let orderStatusChart = null
let userGrowthChart = null
let hotProductsChart = null

const initSalesChart = (data) => {
  salesChart = echarts.init(salesChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '销售额',
      type: 'line',
      smooth: true,
      data: data.map(item => item.value),
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(45, 90, 135, 0.5)' },
          { offset: 1, color: 'rgba(45, 90, 135, 0.1)' }
        ])
      },
      lineStyle: {
        color: '#2d5a87',
        width: 2
      },
      itemStyle: {
        color: '#2d5a87'
      }
    }]
  }
  salesChart.setOption(option)
}

const initCategoryChart = (data) => {
  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: data.map((item, index) => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: ['#2d5a87', '#3a7ca5', '#4a90b8', '#5aa0c5', '#6ab0d2', '#7ac0df'][index % 6]
        }
      }))
    }]
  }
  categoryChart.setOption(option)
}

const initOrderStatusChart = (data) => {
  orderStatusChart = echarts.init(orderStatusChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: '70%',
      center: ['50%', '50%'],
      data: data.map(item => ({
        name: item.name,
        value: item.value,
        itemStyle: {
          color: item.color
        }
      })),
      label: {
        formatter: '{b}\n{d}%'
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  orderStatusChart.setOption(option)
}

const initUserGrowthChart = (data) => {
  userGrowthChart = echarts.init(userGrowthChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增用户', '累计用户']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.month)
    },
    yAxis: [
      {
        type: 'value',
        name: '新增用户'
      },
      {
        type: 'value',
        name: '累计用户'
      }
    ],
    series: [
      {
        name: '新增用户',
        type: 'bar',
        data: data.map(item => item.newUsers),
        itemStyle: {
          color: '#2d5a87'
        }
      },
      {
        name: '累计用户',
        type: 'line',
        yAxisIndex: 1,
        data: data.map(item => item.totalUsers),
        lineStyle: {
          color: '#3a7ca5'
        },
        itemStyle: {
          color: '#3a7ca5'
        }
      }
    ]
  }
  userGrowthChart.setOption(option)
}

const initHotProductsChart = (data) => {
  hotProductsChart = echarts.init(hotProductsChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.name).reverse()
    },
    series: [{
      type: 'bar',
      data: data.map(item => item.sales).reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#2d5a87' },
          { offset: 1, color: '#3a7ca5' }
        ])
      },
      barWidth: 20
    }]
  }
  hotProductsChart.setOption(option)
}

const loadOverview = async () => {
  try {
    const res = await getOverview()
    statCards.value[0].value = res.data.userCount
    statCards.value[1].value = res.data.productCount
    statCards.value[2].value = res.data.orderCount
    statCards.value[3].value = res.data.categoryCount
  } catch (error) {
    console.error(error)
  }
}

const loadCharts = async () => {
  try {
    const [salesRes, categoryRes, orderRes, userRes, hotRes, revenueRes] = await Promise.all([
      getSalesTrend(),
      getCategoryDistribution(),
      getOrderStatus(),
      getUserGrowth(),
      getHotProducts(),
      getRevenue()
    ])
    
    initSalesChart(salesRes.data)
    initCategoryChart(categoryRes.data)
    initOrderStatusChart(orderRes.data)
    initUserGrowthChart(userRes.data)
    initHotProductsChart(hotRes.data)
    
    revenueData.value = [
      { label: '今日营收', value: `¥${revenueRes.data.todayRevenue.toLocaleString()}` },
      { label: '本周营收', value: `¥${revenueRes.data.weekRevenue.toLocaleString()}` },
      { label: '本月营收', value: `¥${revenueRes.data.monthRevenue.toLocaleString()}` },
      { label: '本年营收', value: `¥${revenueRes.data.yearRevenue.toLocaleString()}` }
    ]
  } catch (error) {
    console.error(error)
  }
}

const handleResize = () => {
  salesChart?.resize()
  categoryChart?.resize()
  orderStatusChart?.resize()
  userGrowthChart?.resize()
  hotProductsChart?.resize()
}

onMounted(() => {
  loadOverview()
  loadCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  categoryChart?.dispose()
  orderStatusChart?.dispose()
  userGrowthChart?.dispose()
  hotProductsChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.card-header {
  font-size: 16px;
  font-weight: 500;
}

.revenue-container {
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  padding: 20px;
}

.revenue-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 8px;
}

.revenue-label {
  font-size: 14px;
  color: #666;
}

.revenue-value {
  font-size: 24px;
  font-weight: 600;
  color: #2d5a87;
}
</style>
