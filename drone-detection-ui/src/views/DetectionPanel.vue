<template>
  <div class="home-container">
    <button class="back-btn" @click="goBack" aria-label="返回">
    <span class="back-arrow">←</span>
    <span class="back-text">返回</span>
    </button>
    <!-- 背景装饰（复用） -->
    <div class="bg-decoration">
      <div class="grid"></div>
      <div class="glow"></div>
    </div>

    <div class="main-content">
      <section class="welcome-section">
        <h1 class="welcome-title">检测记录概览</h1>
        <p class="welcome-desc">点击折点查看当日详细记录</p>
      </section>

      <!-- 折线图卡片 -->
      <div class="feature-card chart-card">
        <div v-if="isChartEmpty" class="empty-chart-tip">
          📊 暂无检测记录，快去 <router-link to="/dashboard">开始检测</router-link> 吧！
        </div>
        <canvas v-else ref="chartRef"></canvas>
      </div>

      <!-- 明细列表 -->
      <div v-if="selectedDate" class="feature-card detail-card">
        <div class="detail-header">
          <h3>{{ selectedDate }} 的检测记录</h3>
          <button class="close-btn" @click="selectedDate = ''">×</button>
        </div>
        <ul class="record-list">
          <li v-for="item in dailyRecords" :key="item.id" class="record-item">
            <span class="time">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</span>
            <span class="video-name">{{ item.videoName }}</span>
            <div class="record-actions">
              <button v-if="item.videoResultPath" class="play-btn" @click="playVideo(item.id)">▶ 播放</button>
              <button v-if="item.videoResultPath" class="download-btn-small" @click="downloadVideo(item.id)">⬇ 下载</button>
              <span v-else class="no-video">无录像</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Chart from 'chart.js/auto'
import axios from 'axios'
import {useRouter} from 'vue-router'
const router = useRouter()

const chartRef = ref(null)
const selectedDate = ref('')
const dailyRecords = ref([])

let chartInstance = null

//返回
const goBack = () => {
  router.push('homePage')
}
// 播放视频（在新窗口打开或使用弹窗）
const playVideo = (taskId) => {
  const url = `http://localhost:8080/api/detection/video/${taskId}`
  window.open(url, '_blank')
}

// 下载视频
const downloadVideo = (taskId) => {
  const url = `http://localhost:8080/api/detection/video/${taskId}`
  const a = document.createElement('a')
  a.href = url
  a.download = `result_${taskId}.webm`
  a.click()
}
// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '进行中'
  const date = new Date(timeStr)
  return date.toLocaleTimeString('zh-CN', { hour12: false })
}
const isChartEmpty = ref(false)
// 加载统计数据并渲染图表
const loadStats = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/detection/stats?days=30')
    const labels = res.data.map(item => item.date)
    const values = res.data.map(item => item.count)
    // 判断是否全为0
    isChartEmpty.value = values.every(v => v === 0)

    if (chartInstance) chartInstance.destroy()

    const ctx = chartRef.value.getContext('2d')
    chartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: '每日检测次数',
          data: values,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.3,
          fill: true,
          pointBackgroundColor: '#3b82f6',
          pointBorderColor: '#fff',
          pointBorderWidth: 2,
          pointRadius: 5,
          pointHoverRadius: 8
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          tooltip: {
            mode: 'index',
            intersect: false,
            callbacks: {
              title: (items) => `日期：${items[0].label}`,
              label: (item) => `检测次数：${item.raw} 次`
            }
          },
          legend: { display: false }
        },
        scales: {
          y: {
            beginAtZero: true,
            grid: { color: 'rgba(255,255,255,0.1)' },
            ticks: { stepSize: 1, color: '#94a3b8' }
          },
          x: {
            grid: { display: false },
            ticks: { color: '#94a3b8' }
          }
        },
        // 点击事件
        onClick: (event, elements) => {
          if (elements.length > 0) {
            const index = elements[0].index
            const date = labels[index]
            selectedDate.value = date
            loadDailyRecords(date)
          }
        }
      }
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载指定日期的详细记录
const loadDailyRecords = async (date) => {
  try {
    const res = await axios.get('http://localhost:8080/api/detection/list', {
      params: { date }
    })
    dailyRecords.value = res.data
  } catch (error) {
    console.error('加载明细失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.back-btn {
  position: fixed;
  top: 1.5rem;
  left: 1.5rem;
  z-index: 20;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.4);
  border-radius: 2rem;
  padding: 0.4rem 1rem;
  display: flex;
  align-items: center;
  gap: 0.4rem;
  cursor: pointer;
  color: #eef5ff;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.back-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: #3b82f6;
  transform: translateX(-2px);
}
.back-arrow {
  font-size: 1.2rem;
}
.empty-chart-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #94a3b8;
  font-size: 1.2rem;
}
.empty-chart-tip a {
  color: #3b82f6;
  margin-left: 0.5rem;
  text-decoration: underline;
}
.record-actions {
  display: flex;
  gap: 0.5rem;
}
.play-btn, .download-btn-small {
  background: rgba(59, 130, 246, 0.2);
  border: 1px solid #3b82f6;
  color: #3b82f6;
  border-radius: 1rem;
  padding: 0.2rem 0.6rem;
  font-size: 0.75rem;
  cursor: pointer;
  transition: all 0.2s;
}
.play-btn:hover, .download-btn-small:hover {
  background: #3b82f6;
  color: white;
}
.no-video {
  color: #64748b;
  font-size: 0.75rem;
}
/* 复用主页背景样式，这里仅补充新增卡片样式 */
.chart-card {
  padding: 1.5rem;
  height: 350px;
  margin-bottom: 2rem;
}

.detail-card {
  padding: 1.5rem;
  margin-top: 1rem;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.detail-header h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 1.8rem;
  cursor: pointer;
  line-height: 1;
  transition: color 0.2s;
}
.close-btn:hover {
  color: #f56c6c;
}

.record-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 300px;
  overflow-y: auto;
}

.record-item {
  display: flex;
  justify-content: space-between;
  padding: 0.6rem 0;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  color: #cbd5e1;
  font-size: 0.9rem;
}

.record-item:last-child {
  border-bottom: none;
}

.time {
  color: #94a3b8;
}

.video-name {
  font-weight: 500;
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-tip {
  text-align: center;
  color: #64748b;
  padding: 2rem 0;
}

/* 复用主页的背景装饰样式 */
.home-container {
  position: relative;
  min-height: 100vh;
  background: radial-gradient(circle at 20% 30%, #0a0f1e, #05070f);
  color: #eef5ff;
  font-family: 'Segoe UI', sans-serif;
  overflow-x: hidden;
}
.bg-decoration {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  pointer-events: none;
  z-index: 0;
}
.grid {
  position: absolute;
  width: 100%; height: 100%;
  background-image:
    linear-gradient(rgba(0, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 50px 50px;
}
.glow {
  position: absolute;
  top: 20%; left: 10%;
  width: 80%; height: 60%;
  background: radial-gradient(ellipse at center, rgba(0, 150, 255, 0.15), transparent 70%);
  filter: blur(80px);
}
.main-content {
  position: relative;
  z-index: 2;
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}
.welcome-section {
  margin-bottom: 2rem;
}
.welcome-title {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.welcome-desc {
  color: #94a3b8;
  font-size: 1rem;
}
.feature-card {
  background: rgba(15, 25, 45, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 1.5rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow: 0 15px 30px -10px rgba(0,0,0,0.3);
}
</style>
