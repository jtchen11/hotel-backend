<template>
  <div class="gm-dashboard">
    <!-- 顶部：时间筛选器 -->
    <el-card class="filter-card">
      <div class="filter-bar">
        <el-radio-group v-model="timeRange" @change="onTimeChange">
          <el-radio-button label="本月" />
          <el-radio-button label="本季度" />
          <el-radio-button label="本年" />
        </el-radio-group>
        <el-date-picker
            v-model="customDate"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="onCustomDateChange"
        />
        <el-button type="primary" @click="refresh">刷新</el-button>
      </div>
    </el-card>

    <!-- KPI 卡片 -->
    <el-row :gutter="20" class="kpi-row">
      <el-col :span="6"><el-card shadow="hover" class="kpi-card">
        <div class="kpi-label">今日营收</div>
        <div class="kpi-value">¥{{ kpi.todayRevenue || 0 }}</div>
      </el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="kpi-card">
        <div class="kpi-label">本月入住率</div>
        <div class="kpi-value">{{ kpi.monthlyOccupancyRate || 0 }}%</div>
      </el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="kpi-card">
        <div class="kpi-label">当前在住</div>
        <div class="kpi-value">{{ kpi.livingCount || 0 }}</div>
      </el-card></el-col>
      <el-col :span="6"><el-card shadow="hover" class="kpi-card">
        <div class="kpi-label">在职员工</div>
        <div class="kpi-value">{{ kpi.staffCount || 0 }}</div>
      </el-card></el-col>
    </el-row>

    <!-- 图表区 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>统计收入构成</template>
          <div ref="revenueChart" class="chart-container"></div>
          <div class="insight-box">{{ revenueSummary }}</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span>分析客源分布</span>
              <el-select v-model="sourceDimension" size="small" style="width: 110px;" @change="fetchAll">
                <el-option label="按人数" value="count" />
                <el-option label="按间夜数" value="nights" />
              </el-select>
            </div>
          </template>
          <!-- 添加图表容器 -->
          <div ref="sourceChart" class="chart-container"></div>
          <div class="insight-box">{{ sourceSummary }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>统计入住率趋势</template>
          <div ref="occupancyChart" class="chart-container"></div>
          <div class="insight-box">{{ occupancySummary }}</div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>计算员工房间配比</template>
          <div class="ratio-container">
            <div class="ratio-number">{{ ratioData.ratio }}</div>
            <div class="ratio-detail">
              <p>员工：{{ ratioData.staffCount }} 人</p>
              <p>房间：{{ ratioData.roomCount }} 间</p>
              <p>{{ ratioData.status }}</p>
              <p style="font-size:12px;color:#909399;">{{ ratioData.reference }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const sourceDimension = ref('count')
// 新增：存储地图数据
const chinaMapData = ref(null)
const mapLoaded = ref(false)

// 时间筛选
const timeRange = ref('本月')
const customDate = ref([])
const startDate = ref('')
const endDate = ref('')

// 数据
const kpi = ref({})
const revenueData = ref({})
const sourceData = ref({})
const occupancyData = ref({})
const ratioData = ref({})
const revenueSummary = ref('')
const sourceSummary = ref('')
const occupancySummary = ref('')

// 图表实例
const revenueChart = ref(null)
const sourceChart = ref(null)
const occupancyChart = ref(null)

// 初始化时间
const initTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const firstDay = `${year}-${month}-01`
  const today = `${year}-${month}-${day}`
  startDate.value = firstDay
  endDate.value = today
  customDate.value = [firstDay, today]
}

// 获取数据
const fetchAll = async () => {
  try {
    // KPI
    const kpiRes = await request.get('/gm/kpi')
    kpi.value = kpiRes.data

    // 收入构成
    const revRes = await request.get('/gm/revenueByType', { params: { startDate: startDate.value, endDate: endDate.value } })
    revenueData.value = revRes.data.data || {}
    revenueSummary.value = revRes.data.summary || ''

    // 客源分布
    const srcRes = await request.get('/gm/guestSource', {
      params: { dimension: sourceDimension.value }
    })
    sourceData.value = srcRes.data.data || []
    sourceSummary.value = srcRes.data.summary || ''

    // 入住率
    let occGranularity = 'daily'
    const diffDays = Math.ceil((new Date(endDate.value) - new Date(startDate.value)) / (1000 * 60 * 60 * 24))
    if (timeRange.value === '本月' || diffDays < 30) {
      occGranularity = 'daily'
    } else {
      occGranularity = 'monthly'
    }
    const occRes = await request.get('/gm/occupancyRate', {
      params: {
        startDate: startDate.value,
        endDate: endDate.value,
        granularity: occGranularity
      }
    })
    occupancyData.value = occRes.data.data || []
    occupancySummary.value = occRes.data.summary || ''
    // 配比
    const ratioRes = await request.get('/gm/staffRoomRatio')
    ratioData.value = ratioRes.data

    // 渲染图表
    await nextTick()
    renderRevenueChart()
    renderSourceChart()
    renderOccupancyChart()
    if (!mapLoaded.value) {
      await loadChinaMap()
    }
    // 如果地图已加载但客源数据存在，渲染地图
    if (mapLoaded.value && sourceData.value.length > 0) {
      renderSourceChart()
    }
  } catch (err) {
    ElMessage.error('数据加载失败')
  }
}

// 加载中国地图数据
const loadChinaMap = async () => {
  try {
    // 推荐使用这个 CDN（包含南海诸岛，标准版）
    const res = await fetch('https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json')
    const data = await res.json()
    chinaMapData.value = data
    mapLoaded.value = true
    // 注册地图
    echarts.registerMap('china', data)
    // 如果客源数据已加载，则渲染地图
    if (sourceData.value.length > 0) {
      renderSourceChart()
    }
  } catch (err) {
    console.error('加载地图数据失败，将使用饼图备选', err)
    // 加载失败时回退到饼图，后续渲染逻辑自动处理
  }
}
// 渲染收入构成饼图
const renderRevenueChart = () => {
  const dom = revenueChart.value
  if (!dom) return
  const chart = echarts.init(dom)
  const data = Object.entries(revenueData.value).map(([name, value]) => ({ name, value }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: data,
      label: { show: true, formatter: '{b}: {d}%' },
      emphasis: { scale: false }
    }]
  })
  chart.resize()
}

const renderSourceChart = () => {
  const dom = sourceChart.value
  if (!dom) return
  let chart = echarts.getInstanceByDom(dom)
  if (!chart) {
    chart = echarts.init(dom)
  }

  // 若地图未加载，回退到饼图（保持不变）
  if (!mapLoaded.value) {
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: sourceData.value,
        label: { show: true, formatter: '{b}: {d}%' },
        emphasis: { scale: false }
      }]
    })
    chart.resize()
    return
  }

  // 准备地图数据（省份名称映射逻辑保持不变）
  const mapData = sourceData.value.map(item => {
    let name = item.name
    const suffixMap = {
      '北京': '北京市', '上海': '上海市', '天津': '天津市', '重庆': '重庆市',
      '香港': '香港特别行政区', '澳门': '澳门特别行政区'
    }
    if (suffixMap[name]) {
      name = suffixMap[name]
    } else if (name.length === 2 && !name.endsWith('省') && !name.endsWith('自治区')) {
      const provinceSuffix = ['广东','湖南','湖北','山东','山西','河南','河北','辽宁','吉林','黑龙江',
        '江苏','浙江','安徽','福建','江西','四川','贵州','云南','陕西','甘肃','青海','海南','台湾']
      if (provinceSuffix.includes(name)) {
        name = name + '省'
      }
    }
    return { name, value: item.value }
  })

  // 渲染地图（优化后）
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        return params.name + '<br/>客源人数：' + params.value
      }
    },
    visualMap: {
      min: 0,
      max: Math.max(...sourceData.value.map(d => d.value), 1),
      left: 'left',
      bottom: '20',
      text: ['高', '低'],
      inRange: {
        color: ['#e0f3f8', '#abd9e9', '#74add1', '#4575b4', '#313695']
      },
      calculable: true
    },
    series: [{
      name: '客源分布',
      type: 'map',
      map: 'china',
      roam: true,               // 允许拖动缩放
      selectedMode: false,
      zoom: 1.2,                // 🔥 放大到 1.2 倍
      center: [105, 35],        // 调整地图中心（中国大致居中）
      data: mapData,
      // ---------- 关键修改：关闭省份名称 ----------
      label: {
        show: false             // 不显示省份名称，避免拥挤
      },
      emphasis: {
        label: {
          show: true,           // 鼠标悬停时显示省份名称
          fontSize: 14,
          fontWeight: 'bold',
          color: '#333'
        }
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      }
    }]
  })
  chart.resize()
}

// 渲染入住率折线图
const renderOccupancyChart = () => {
  const dom = occupancyChart.value
  if (!dom) return
  const chart = echarts.init(dom)
  const dates = occupancyData.value.map(item => item.date)
  const rates = occupancyData.value.map(item => item.rate)

  // 判断是否为月粒度（日期格式为 yyyy-MM）
  const isMonthly = dates.length > 0 && dates[0].length === 7

  chart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const p = params[0]
        const label = isMonthly ? p.name + '月' : p.name
        return label + '<br/>入住率：' + p.value + '%'
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        formatter: function(value) {
          if (isMonthly) {
            return value.substring(5) + '月'  // 显示 "06月"
          } else {
            return value.substring(5)        // 显示 "06-14"
          }
        }
      }
    },
    yAxis: { type: 'value', min: 0, max: 100, axisLabel: { formatter: '{value}%' } },
    series: [{
      type: 'line',
      data: rates,
      smooth: true,
      areaStyle: {},
      markLine: {
        data: [{ type: 'average', name: '平均值' }]
      }
    }]
  })
  chart.resize()
}

// 时间切换
const onTimeChange = () => {
  const now = new Date()
  const year = now.getFullYear()
  if (timeRange.value === '本月') {
    startDate.value = `${year}-${String(now.getMonth()+1).padStart(2,'0')}-01`
    endDate.value = now.toISOString().slice(0,10)
  } else if (timeRange.value === '本季度') {
    const month = now.getMonth()
    const quarterStartMonth = Math.floor(month / 3) * 3
    startDate.value = `${year}-${String(quarterStartMonth+1).padStart(2,'0')}-01`
    endDate.value = now.toISOString().slice(0,10)
  } else if (timeRange.value === '本年') {
    startDate.value = `${year}-01-01`
    endDate.value = now.toISOString().slice(0,10)
  }
  customDate.value = [startDate.value, endDate.value]
  fetchAll()
}

const onCustomDateChange = (val) => {
  if (val && val.length === 2) {
    startDate.value = val[0]
    endDate.value = val[1]
    timeRange.value = '' // 取消预设
    fetchAll()
  }
}

const refresh = () => {
  fetchAll()
}

// 窗口变化自适应
const resizeCharts = () => {
  const charts = [revenueChart.value, sourceChart.value, occupancyChart.value]
  charts.forEach(dom => {
    if (dom) {
      const instance = echarts.getInstanceByDom(dom)
      if (instance) instance.resize()
    }
  })
}
window.addEventListener('resize', resizeCharts)

onMounted(() => {
  initTime()
  fetchAll()
})
</script>

<style scoped>
.gm-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.filter-card {
  margin-bottom: 20px;
}
.filter-bar {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}
.kpi-row {
  margin-bottom: 20px;
}
.kpi-card {
  text-align: center;
}
.kpi-label {
  color: #909399;
  font-size: 14px;
}
.kpi-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-top: 8px;
}
.chart-card {
  height: 100%;
}
.chart-container {
  width: 100%;
  height: 350px;
}
.insight-box {
  background: #f5f7fa;
  padding: 10px 16px;
  border-radius: 6px;
  color: #606266;
  font-size: 13px;
  margin-top: 12px;
}
.ratio-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 280px;
}
.ratio-number {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
}
.ratio-detail {
  text-align: center;
  margin-top: 12px;
  color: #606266;
  font-size: 14px;
}
.ratio-detail p {
  margin: 4px 0;
}
</style>