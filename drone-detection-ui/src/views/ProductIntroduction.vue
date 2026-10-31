<template>
  <div class="product-intro-container" @scroll="handleScroll">
    <!-- ================= 1. 背景与挑战 ================= -->
    <button class="back-button" @click="goBack">
      <i class="fas fa-arrow-left"></i> 返回
    </button>

    <section class="intro-section background-section" :class="{ 'is-visible': activeSection >= 0 }">
      <div class="section-header">
        <span class="section-tag">当前问题</span>
        <h2>无人机航拍数据的人工处理瓶颈</h2>
        <p>大量视频依赖人工回看，效率与实时性难以满足业务需求</p>
      </div>

      <div class="problem-grid">
        <div class="problem-card" v-for="(problem, idx) in problems" :key="idx">
          <div class="problem-icon"><i :class="problem.icon"></i></div>
          <h3>{{ problem.title }}</h3>
          <p>{{ problem.desc }}</p>
        </div>
      </div>

      <div class="solution-bridge">
        <div class="bridge-line"></div>
        <div class="bridge-text">
          <i class="fas fa-arrow-down"></i> 因此需要一个实时、准确、适配边缘环境的检测方案
        </div>
      </div>
    </section>

    <!-- ================= 2. 核心技术方案 ================= -->
    <section class="intro-section tech-solution-section" :class="{ 'is-visible': activeSection >= 1 }">
      <div class="section-header">
        <span class="section-tag">技术方法</span>
        <h2>轻量化模型与边缘实时推理</h2>
        <p>针对无人机视角下的尺度变化、小目标及复杂环境进行优化</p>
      </div>

      <div class="solution-cards">
        <div class="sol-card" v-for="(sol, idx) in solutions" :key="idx">
          <div class="sol-icon"><i :class="sol.icon"></i></div>
          <h3>{{ sol.title }}</h3>
          <p>{{ sol.desc }}</p>
          <div class="sol-tech-tag">{{ sol.tag }}</div>
        </div>
      </div>

      <div class="key-metrics">
        <div class="metric-item">
          <div class="metric-value">≥75%</div>
          <div class="metric-label">mAP@0.5</div>
        </div>
        <div class="metric-item">
          <div class="metric-value">≤40ms</div>
          <div class="metric-label">单帧推理时间</div>
        </div>
        <div class="metric-item">
          <div class="metric-value">25+ FPS</div>
          <div class="metric-label">处理帧率</div>
        </div>
        <div class="metric-item">
          <div class="metric-value">6+ 类</div>
          <div class="metric-label">可扩展目标类别</div>
        </div>
      </div>
    </section>

    <!-- ================= 3. 系统架构 ================= -->
    <section class="intro-section architecture-section" :class="{ 'is-visible': activeSection >= 2 }">
      <div class="section-header">
        <span class="section-tag">系统实现</span>
        <h2>端云协同与低延迟传输</h2>
        <p>从模型训练到前端展示的完整链路</p>
      </div>

      <div class="arch-flow">
        <div class="arch-node">
          <i class="fas fa-microchip"></i>
          <span>边缘端（机载）</span>
          <div class="arch-node-detail">YOLOv8 轻量化模型 · TensorRT 加速 · 推理 ≤ 40ms</div>
        </div>
        <div class="arch-arrow"><i class="fas fa-bolt"></i></div>
        <div class="arch-node">
          <i class="fas fa-exchange-alt"></i>
          <span>WebSocket 通信</span>
          <div class="arch-node-detail">视频帧与检测结果实时传输</div>
        </div>
        <div class="arch-arrow"><i class="fas fa-bolt"></i></div>
        <div class="arch-node">
          <i class="fas fa-cloud-upload-alt"></i>
          <span>地面站/云端</span>
          <div class="arch-node-detail">Vue 3 + Spring Boot · 可视化面板</div>
        </div>
      </div>

      <div class="tech-stack-badges">
        <span v-for="tech in techStack" :key="tech" class="tech-badge">{{ tech }}</span>
      </div>
    </section>

    <!-- ================= 4. 检测演示 ================= -->
    <section class="intro-section demo-section" :class="{ 'is-visible': activeSection >= 3 }">
      <div class="section-header">
        <span class="section-tag">效果展示</span>
        <h2>实时推理与检测框叠加</h2>
        <p>左侧为原始航拍画面，右侧为 AI 检测输出（含类别与置信度）</p>
      </div>

      <div class="video-comparison-board">
        <div class="video-panel drone-input">
          <div class="panel-hud-overlay">
            <span class="hud-text top-left">REC ● 原始画面</span>
            <span class="hud-text top-right">UAV-CAM-01</span>
          </div>
          <video ref="originVideo" autoplay loop muted playsinline :src="droneVideoSrc"></video>
          <div class="panel-title">原始视频流</div>
        </div>

        <div class="data-transfer-bridge">
          <div class="arrow-stream">
            <i class="fas fa-angle-double-right"></i>
            <i class="fas fa-angle-double-right"></i>
            <i class="fas fa-angle-double-right"></i>
          </div>
          <div class="latency-badge">推理延迟 < 40ms</div>
        </div>

        <div class="video-panel ai-output">
          <div class="panel-hud-overlay">
            <span class="hud-text top-left">YOLOv8 推理中</span>
            <span class="hud-text top-right">目标检测</span>
          </div>
          <video ref="aiVideo" autoplay loop muted playsinline :src="droneVideoProcess"></video>
          <canvas ref="detectionCanvas" class="detection-canvas"></canvas>
          <div class="panel-title highlight">检测结果（带标注框）</div>
          <div class="detection-stats">
            <span>当前目标: <strong>{{ mockDetections.length }}</strong> 个</span>
            <span>置信度 ≥ 0.65</span>
          </div>
        </div>
      </div>

      <div class="demo-note">
        <i class="fas fa-chart-line"></i> 本演示为仿真叠加效果，实际支持车辆、行人、农作物等多类目标
      </div>
    </section>

    <!-- ================= 5. 性能评估 ================= -->
    <section class="intro-section performance-section" :class="{ 'is-visible': activeSection >= 4 }">
      <div class="section-header">
        <span class="section-tag">测试指标</span>
        <h2>不同环境下的检测性能</h2>
        <p>在公开数据集与自建场景下的评估结果</p>
      </div>

      <div class="perf-grid">
        <div class="perf-card">
          <div class="perf-title">VisDrone 测试集</div>
          <div class="perf-value">mAP@0.5: 76.3%</div>
          <div class="perf-desc">达到课题要求基线</div>
        </div>
        <div class="perf-card">
          <div class="perf-title">复杂光照（逆光/阴影）</div>
          <div class="perf-value">召回率: 72.8%</div>
          <div class="perf-desc">特征增强策略有效</div>
        </div>
        <div class="perf-card">
          <div class="perf-title">小目标（像素 < 32²）</div>
          <div class="perf-value">AP_s: 54.2%</div>
          <div class="perf-desc">较基线提升 12%</div>
        </div>
        <div class="perf-card">
          <div class="perf-title">雨雾/夜间场景</div>
          <div class="perf-value">性能下降 < 8%</div>
          <div class="perf-desc">数据增强与域适应</div>
        </div>
      </div>
    </section>

    <!-- ================= 6. 应用方向 ================= -->
    <section class="intro-section application-section" :class="{ 'is-visible': activeSection >= 5 }">
      <div class="section-header">
        <span class="section-tag">潜在应用</span>
        <h2>可适配的行业场景</h2>
        <p>系统支持类别扩展，可迁移至不同垂直领域</p>
      </div>

      <div class="application-grid">
        <div class="app-item" v-for="(app, idx) in applications" :key="idx">
          <div class="app-icon"><i :class="app.icon"></i></div>
          <div class="app-title">{{ app.title }}</div>
          <div class="app-detail">{{ app.detail }}</div>
        </div>
      </div>

      <div class="extend-badge">
        <i class="fas fa-expand-arrows-alt"></i> 支持增量学习与自定义类别扩展
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import {useRouter} from 'vue-router'
// ---------- 静态资源 ----------
const router = useRouter()
const droneVideoSrc = '/videos/sample4.mp4'
const droneVideoProcess='/videos/Processedvideo.mp4'
// ---------- 背景痛点数据 ----------
const problems = ref([
  { icon: 'fas fa-chart-line', title: '人工判读效率低', desc: '无人机单次巡检产生数小时视频，人工回看易疲劳、漏检率高达30%' },
  { icon: 'fas fa-sun', title: '复杂环境干扰', desc: '光照剧变、雨雾天气、目标尺度从像素级到数十米，传统算法失效' },
  { icon: 'fas fa-microchip', title: '边缘算力受限', desc: '机载设备无法运行大模型，云端传输延迟高，无法满足实时响应' }
])

// ---------- 核心技术方案 ----------
const solutions = ref([
  { icon: 'fas fa-bolt', title: '轻量化检测模型', desc: '基于YOLOv8改进，引入ShuffleNet与深度可分离卷积，参数量减少40%', tag: 'TensorRT 加速' },
  { icon: 'fas fa-chart-line', title: '多尺度特征增强', desc: 'BiFPN + 坐标注意力机制，强化小目标特征响应，提升密集目标召回', tag: 'mAP@0.5 ≥ 75%' },
  { icon: 'fas fa-cloud-sun', title: '自适应鲁棒训练', desc: 'Mosaic+MixUp 数据增强，模拟雨雾/夜间/抖动，泛化能力提升25%', tag: '域自适应' },
  { icon: 'fas fa-cut', title: '模型剪枝与量化', desc: '结构化剪枝 + INT8量化，推理速度提升2.5倍，精度损失<1%', tag: '边缘部署' }
])
//返回按钮
const goBack = () => {
  router.push('/')
}
// ---------- 技术栈 ----------
const techStack = ref([
   'YOLOv8',
  'Vue 3', 'Spring Boot', 'WebSocket'
])

// ---------- 演示用模拟检测框数据 (归一化坐标 0-1) ----------
const mockDetections = ref([
  { label: 'car', x: 0.25, y: 0.55, w: 0.12, h: 0.09, conf: 0.87 },
  { label: 'person', x: 0.48, y: 0.62, w: 0.05, h: 0.14, conf: 0.76 },
  { label: 'car', x: 0.68, y: 0.58, w: 0.10, h: 0.08, conf: 0.82 },
  { label: 'bicycle', x: 0.82, y: 0.70, w: 0.07, h: 0.10, conf: 0.69 },
  { label: 'person', x: 0.35, y: 0.45, w: 0.04, h: 0.12, conf: 0.71 }
])

// ---------- 行业应用 ----------
const applications = ref([
  { icon: 'fas fa-tractor', title: '智慧农业', detail: '病虫害识别、作物计数、农药精准喷洒' },
  { icon: 'fas fa-car', title: '交通监控', detail: '违章车辆抓拍、车流密度分析、事故快速定位' },
  { icon: 'fas fa-life-ring', title: '应急救援', detail: '受困人员定位、火情蔓延监测、物资投递指引' },
  { icon: 'fas fa-tree', title: '生态保护', detail: '珍稀动物追踪、非法采伐预警、森林防火' },
  { icon: 'fas fa-bridge', title: '基础设施巡检', detail: '电力线破损检测、桥梁裂缝识别、光伏板污损' },
  { icon: 'fas fa-city', title: '智慧城市', detail: '违章建筑识别、垃圾堆放监测、人流热力分析' }
])

// ---------- 滚动动画观察者 ----------
const activeSection = ref(0)
let observer = null

const setupIntersectionObserver = () => {
  const options = { root: null, rootMargin: '0px', threshold: 0.25 }
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const sections = Array.from(document.querySelectorAll('.intro-section'))
        const index = sections.indexOf(entry.target)
        if (index > activeSection.value) activeSection.value = index
      }
    })
  }, options)
  document.querySelectorAll('.intro-section').forEach(section => observer.observe(section))
}

// ---------- Canvas 绘制检测框 (右侧视频叠加层) ----------
const aiVideo = ref(null)
const detectionCanvas = ref(null)
let animationId = null

const drawDetections = () => {
  const video = aiVideo.value
  const canvas = detectionCanvas.value
  if (!video || !canvas) return
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  // 尺寸同步
  const rect = video.getBoundingClientRect()
  canvas.width = rect.width
  canvas.height = rect.height
  canvas.style.width = `${rect.width}px`
  canvas.style.height = `${rect.height}px`

  ctx.clearRect(0, 0, canvas.width, canvas.height)

  // 绘制每个检测框
  mockDetections.value.forEach(det => {
    const x = det.x * canvas.width
    const y = det.y * canvas.height
    const w = det.w * canvas.width
    const h = det.h * canvas.height

    // 绘制边框
    ctx.strokeStyle = '#00ffcc'
    ctx.lineWidth = 2
    ctx.strokeRect(x, y, w, h)

    // 绘制背景标签
    ctx.fillStyle = '#00ffcc'
    ctx.font = 'bold 14px "Segoe UI"'
    const labelText = `${det.label} ${(det.conf * 100).toFixed(0)}%`
    const textWidth = ctx.measureText(labelText).width
    ctx.fillRect(x, y - 20, textWidth + 8, 20)
    ctx.fillStyle = '#000000'
    ctx.fillText(labelText, x + 4, y - 6)
  })

  animationId = requestAnimationFrame(drawDetections)
}

// 监听视频元素尺寸变化 (ResizeObserver)
let resizeObserver = null
const setupCanvasResize = () => {
  if (aiVideo.value && detectionCanvas.value) {
    resizeObserver = new ResizeObserver(() => {
      drawDetections()
    })
    resizeObserver.observe(aiVideo.value)
    // 视频元数据加载后触发一次
    aiVideo.value.addEventListener('loadeddata', drawDetections)
    aiVideo.value.addEventListener('play', drawDetections)
  }
}

onMounted(() => {
  setupIntersectionObserver()
  nextTick(() => {
    setupCanvasResize()
    drawDetections()
  })
})

onUnmounted(() => {
  if (observer) observer.disconnect()
  if (animationId) cancelAnimationFrame(animationId)
  if (resizeObserver) resizeObserver.disconnect()
})

const handleScroll = () => {}
</script>

<style scoped>
/* ===== 全局背景与原有样式保留，新增部分增强 ===== */
.product-intro-container {
  width: 100vw;
  height: 100vh;
  overflow-y: auto;
  overflow-x: hidden;
  background-color: #0a0e17;
  background-image: url('public/picture/DronePic_ProductIntro.webp');
  background-attachment: fixed;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: #fff;
  scroll-behavior: smooth;
  scroll-snap-type: y mandatory;
  position: relative;
}
.product-intro-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(10,14,23,0.85), rgba(0,0,0,0.7));
  pointer-events: none;
  z-index: 0;
}
.intro-section {
  position: relative;
  z-index: 1;
  min-height: 100vh;
  padding: 80px 10%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  scroll-snap-align: start;
  opacity: 0;
  transform: translateY(60px) scale(0.98);
  transition: opacity 1.2s cubic-bezier(0.2, 0.9, 0.4, 1.1),
  transform 1.2s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}
.intro-section.is-visible {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.section-header {
  text-align: center;
  margin-bottom: 50px;
}
.section-tag {
  display: inline-block;
  background: rgba(0,210,255,0.2);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  letter-spacing: 1px;
  margin-bottom: 16px;
  color: #00d2ff;
}
.section-header h2 {
  font-size: 2.5rem;
  background: linear-gradient(135deg, #fff, #88c0ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 16px;
}
.section-header p {
  font-size: 1.1rem;
  opacity: 0.8;
}

/* 问题卡片 */
.problem-grid {
  display: flex;
  gap: 30px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 20px;
}
.problem-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 24px;
  padding: 30px;
  width: 280px;
  text-align: center;
  transition: all 0.3s;
}
.problem-card:hover { transform: translateY(-8px); background: rgba(0,210,255,0.1); }
.problem-icon i { font-size: 2.5rem; color: #00d2ff; margin-bottom: 20px; }
.problem-card h3 { font-size: 1.3rem; margin-bottom: 12px; }
.solution-bridge { margin-top: 50px; text-align: center; }
.bridge-line { height: 2px; background: linear-gradient(90deg, transparent, #00d2ff, transparent); width: 60%; margin: 20px auto; }
.bridge-text { color: #00d2ff; font-weight: bold; }

/* 技术方案卡片 */
.solution-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: center;
  margin-bottom: 50px;
}
.sol-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(8px);
  border-radius: 24px;
  padding: 24px;
  width: 260px;
  border: 1px solid rgba(0,210,255,0.2);
  transition: all 0.3s;
}
.sol-card:hover { transform: translateY(-5px); border-color: #00d2ff; }
.sol-icon i { font-size: 2rem; color: #00d2ff; margin-bottom: 16px; }
.sol-tech-tag { margin-top: 12px; font-size: 0.75rem; color: #00d2ff; background: rgba(0,210,255,0.2); display: inline-block; padding: 4px 12px; border-radius: 20px; }
.key-metrics {
  display: flex;
  justify-content: center;
  gap: 40px;
  flex-wrap: wrap;
  margin-top: 20px;
}
.metric-item { text-align: center; background: rgba(0,0,0,0.4); padding: 16px 24px; border-radius: 40px; backdrop-filter: blur(4px); }
.metric-value { font-size: 2rem; font-weight: bold; color: #00ffcc; }
.metric-label { font-size: 0.9rem; opacity: 0.8; }

/* 架构流程 */
.arch-flow {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 40px;
}
.arch-node {
  background: rgba(0,0,0,0.5);
  backdrop-filter: blur(12px);
  padding: 20px 30px;
  border-radius: 32px;
  text-align: center;
  min-width: 200px;
  border: 1px solid rgba(0,210,255,0.3);
}
.arch-node i { font-size: 2rem; color: #00d2ff; display: block; margin-bottom: 10px; }
.arch-node-detail { font-size: 0.75rem; opacity: 0.7; margin-top: 8px; }
.arch-arrow i { font-size: 1.8rem; color: #00d2ff; }
.tech-stack-badges { display: flex; flex-wrap: wrap; gap: 12px; justify-content: center; margin-top: 20px; }
.tech-badge { background: rgba(255,255,255,0.1); padding: 6px 16px; border-radius: 40px; font-size: 0.85rem; backdrop-filter: blur(4px); }

/* 视频对比区（保留原有样式并强化） */
.video-comparison-board {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  max-width: 1200px;
  background: rgba(10,14,23,0.6);
  border-radius: 24px;
  padding: 24px;
  backdrop-filter: blur(10px);
  gap: 20px;
  flex-wrap: wrap;
}
.video-panel {
  position: relative;
  flex: 1;
  min-width: 280px;
  aspect-ratio: 16/9;
  background: #000;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0,0,0,0.5);
}
.video-panel video { width: 100%; height: 100%; object-fit: cover; }
.detection-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.panel-hud-overlay {
  position: absolute;
  top: 12px;
  left: 12px;
  right: 12px;
  display: flex;
  justify-content: space-between;
  z-index: 2;
  font-size: 0.7rem;
  background: rgba(0,0,0,0.5);
  padding: 4px 8px;
  border-radius: 8px;
  font-family: monospace;
}
.panel-title {
  position: absolute;
  bottom: 0;
  width: 100%;
  background: rgba(0,0,0,0.7);
  text-align: center;
  padding: 6px;
  font-size: 0.8rem;
}
.panel-title.highlight { background: #007bff; }
.detection-stats {
  position: absolute;
  bottom: 40px;
  right: 10px;
  background: rgba(0,0,0,0.7);
  padding: 4px 8px;
  border-radius: 8px;
  font-size: 0.7rem;
  z-index: 2;
}
.data-transfer-bridge { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.arrow-stream i { font-size: 1.5rem; color: #00d2ff; animation: pulse 1s infinite; }
.latency-badge { background: #00d2ff20; padding: 4px 12px; border-radius: 20px; font-size: 0.7rem; }
@keyframes pulse { 0% { opacity: 0.4; } 50% { opacity: 1; } 100% { opacity: 0.4; } }

/* 性能网格 */
.perf-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  width: 100%;
  margin-top: 20px;
}
.perf-card {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(8px);
  border-radius: 24px;
  padding: 24px;
  text-align: center;
  border: 1px solid rgba(0,210,255,0.2);
}
.perf-title { font-weight: bold; margin-bottom: 12px; color: #88c0ff; }
.perf-value { font-size: 1.6rem; font-weight: bold; color: #00ffcc; margin-bottom: 8px; }

/* 应用场景 */
.application-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 24px;
  width: 100%;
  margin: 30px 0;
}
.app-item {
  background: rgba(255,255,255,0.03);
  border-radius: 20px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
  cursor: pointer;
}
.app-item:hover { transform: translateY(-5px); background: rgba(0,210,255,0.1); }
.app-icon i { font-size: 2rem; color: #00d2ff; margin-bottom: 12px; }
.app-title { font-weight: bold; margin-bottom: 8px; }
.app-detail { font-size: 0.8rem; opacity: 0.7; }
.extend-badge {
  text-align: center;
  margin-top: 30px;
  background: rgba(0,210,255,0.1);
  padding: 12px;
  border-radius: 40px;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}
.extend-badge i { margin-right: 8px; }

/* 响应式 */
@media (max-width: 768px) {
  .intro-section { padding: 60px 5%; }
  .section-header h2 { font-size: 1.8rem; }
  .video-comparison-board { flex-direction: column; }
  .arch-flow { flex-direction: column; }
}
.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(0, 210, 255, 0.5);
  color: #00d2ff;
  padding: 8px 16px;
  border-radius: 40px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}
.back-button:hover {
  background: rgba(0, 210, 255, 0.2);
  transform: translateX(-3px);
}
</style>
