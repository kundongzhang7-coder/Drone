<template>
  <div class="home-container">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack" aria-label="返回">
      <span class="back-arrow">←</span>
      <span class="back-text">返回</span>
    </button>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="grid"></div>
      <div class="glow"></div>
      <!-- 新增：漂浮粒子装饰 -->
      <div class="particles">
        <span v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></span>
      </div>
    </div>

    <div class="main-content">
      <!-- 标题区域 -->
      <div class="header">
        <div class="title-badge">🛸 AI POWERED</div>
        <h1 class="title">无人机 <span class="highlight">实时检测</span></h1>
        <p class="subtitle">基于 YOLO + WebSocket 的实时视频目标检测系统</p>
        <!-- 新增：状态指示器 -->
        <div class="status-bar">
          <span class="status-dot" :class="wsStatus"></span>
          <span class="status-text">{{ wsStatusText }}</span>
          <span v-if="isProcessing" class="fps-counter">FPS: {{ currentFps }}</span>
        </div>
      </div>

      <!-- 上传卡片 -->
      <div class="upload-card">
        <div
          class="upload-area"
          :class="{ 'drag-over': isDragOver }"
          @click="triggerFileInput"
          @dragover.prevent="isDragOver = true"
          @dragleave.prevent="isDragOver = false"
          @drop.prevent="handleDrop"
        >
          <!-- 未选择文件 -->
          <div v-if="!videoFile" class="upload-prompt">
            <div class="icon-wrapper">
              <svg width="48" height="48" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                      d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
            </div>
            <p class="prompt-text">点击或拖拽视频文件到此处</p>
            <p class="hint-text">支持 MP4、AVI、MOV 等常见格式</p>
          </div>

          <!-- 已选择文件预览 -->
          <div v-else class="video-preview">
            <video
              ref="sourceVideoRef"
              class="preview-video"
              controls
              muted
              @play="startSendingFrames"
              @ended="onVideoEnded"
              @pause="pauseSending"
              @timeupdate="updateProgress"
            ></video>
            <!-- 新增：播放进度条 -->
            <div class="progress-bar-wrapper">
              <div class="progress-bar-fill" :style="{ width: videoProgress + '%' }"></div>
            </div>
            <div class="file-info">
              <span class="file-name">{{ videoFile.name }}</span>
              <button class="clear-btn" @click.stop="clearVideo">×</button>
            </div>
          </div>
        </div>

        <input
          ref="fileInputRef"
          type="file"
          accept="video/*"
          class="file-input"
          @change="handleFileSelect"
        />

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="btn primary" :disabled="!videoFile || isProcessing" @click="startDetection" >
            <span v-if="isProcessing" class="btn-pulse"></span>
            {{ isProcessing ? '检测中...' : '开始检测' }}
          </button>
          <button class="btn primary" :disabled="!isProcessing" @click="stopProcessing">
            停止检测
          </button>
          <!-- 新增：下载按钮，录制完成后才可点击 -->
          <button
            class="btn primary"
            :disabled="!recordedChunks.length || isProcessing"
            @click="downloadRecordedVideo"
          >
            ⬇ 下载处理视频
          </button>
        </div>

        <!-- 新增：录制状态提示 -->
        <div v-if="recordedChunks.length && !isProcessing" class="record-ready">
          ✅ 录制完成，共 {{ recordedChunks.length }} 帧片段，可点击下载
        </div>
      </div>

      <!-- 结果展示：双栏对比布局 -->
      <div v-if="processedImgSrc" class="result-card">
        <div class="result-header">
          <h3>🎯 实时检测结果</h3>
          <div class="result-actions">
            <button class="download-btn" @click="downloadCurrentFrame">📷 下载当前帧</button>
            <button class="clear-result" @click="clearResult">清除结果</button>
          </div>
        </div>
        <!-- 新增：双栏对比 -->
        <div class="result-compare">

          <div class="compare-box">
            <div class="compare-label result-label">检测结果</div>
            <img :src="processedImgSrc" alt="YOLO 处理结果" class="compare-img" />
          </div>
        </div>
      </div>

      <!-- 新增：特性展示卡片（装饰性） -->
      <div class="features">
        <div class="feature-item" v-for="f in features" :key="f.icon">
          <div class="feature-icon">{{ f.icon }}</div>
          <h3>{{ f.title }}</h3>
          <p>{{ f.desc }}</p>
        </div>
      </div>
    </div>

    <canvas ref="canvasRef" style="display: none;"></canvas>
    <canvas ref="originalFrameRef" style="display: none;"></canvas>
  </div>
</template>

<script setup>
import axios from 'axios'
import {detectCount} from '@/stores/shared.js'
import { useRouter } from 'vue-router' // 导入路由

import { ref, computed, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'

// ========== 路由实例 ==========
const router = useRouter()

// ========== 状态 ==========
const currentTaskId = ref(null)
//创建本地URL后，赋值给sourceVideoRef，用于视频预览
const sourceVideoRef = ref(null)
const canvasRef = ref(null)
const originalFrameRef = ref(null)
const fileInputRef = ref(null)
const videoFile = ref(null)
const processedImgSrc = ref('')
const isProcessing = ref(false)
const isDragOver = ref(false)
const videoProgress = ref(0)
const currentFps = ref(0)

// 在 script setup 中添加 uploadRecordedVideo 函数
const uploadRecordedVideo = async (taskId, blob) => {
  const formData = new FormData()
  formData.append('video', blob, 'result.webm')
  try {
    await axios.post(`http://localhost:8080/api/detection/upload-result/${taskId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    ElMessage.success('检测视频已保存')
  } catch (error) {
    console.error('上传视频失败:', error)
    ElMessage.error('视频保存失败')
  }
}
// 创建检测任务
const createDetectionTask = async (videoName) => {
  try {
    const response = await axios.post('http://localhost:8080/api/detection/start', {
      videoName: videoName,
      startTime: new Date().toISOString()
    })
    currentTaskId.value = response.data.taskId
    console.log('任务创建成功，ID:', currentTaskId.value)
  } catch (error) {
    console.error('创建任务失败:', error)
    ElMessage.error('无法创建检测记录，请检查 Java 后端')
    throw error
  }
}

// 结束检测任务
const completeDetectionTask = async () => {
  if (!currentTaskId.value) return
  try {
    await axios.post('http://localhost:8080/api/detection/end', {
      taskId: currentTaskId.value,
      endTime: new Date().toISOString()
    })
    console.log('任务结束，ID:', currentTaskId.value)
    currentTaskId.value = null
  } catch (error) {
    console.error('结束任务失败:', error)
  }
}
// WebSocket 状态
const wsStatus = ref('disconnected') // disconnected | connecting | connected
const wsStatusText = computed(() => ({
  disconnected: '未连接',
  connecting: '连接中...',
  connected: '已连接',
}[wsStatus.value]))

// 录制相关
const recordedChunks = ref([])
let mediaRecorder = null
let recordStream = null

let ws = null
let frameInterval = null
//本地的视频url，用于本地视频播放
let videoObjectURL = null
let fpsTimer = null
let frameCount = 0

// 装饰性特性数据
const features = [
  { icon: '⚡', title: '实时处理', desc: '毫秒级 WebSocket 帧传输' },
  { icon: '🎯', title: '精准检测', desc: 'YOLOv8 目标识别引擎' },
  { icon: '📹', title: '视频录制', desc: '处理结果一键下载' },
  { icon: '🔒', title: '本地部署', desc: '数据不出本机，安全可靠' },
]

// 粒子样式生成（装饰性）
const getParticleStyle = (i) => ({
  left: `${(i * 17 + 5) % 100}%`,
  top: `${(i * 13 + 10) % 100}%`,
  animationDelay: `${(i * 0.3) % 5}s`,
  animationDuration: `${4 + (i % 4)}s`,
  width: `${2 + (i % 3)}px`,
  height: `${2 + (i % 3)}px`,
})

// 文件选择 ，如果当前没有视频文件，就可以选择文件，否则，点击没有反应
const triggerFileInput = () => {
  if (!videoFile.value) fileInputRef.value?.click()
}
const handleFileSelect = (e) => {
  const file = e.target.files[0]
  if (file) loadVideoFile(file)
}
const handleDrop = (e) => {
  isDragOver.value = false
  const file = e.dataTransfer.files[0]
  if (file?.type.startsWith('video/')) loadVideoFile(file)
  else ElMessage.error('请上传有效的视频文件')
}
//将选择的视频文件加载到sourceVideoRef中，用于视频预览
const loadVideoFile = (file) => {
  //检查是否有旧的视频文件，如果有那么释放旧的视频文件
  if (videoObjectURL) URL.revokeObjectURL(videoObjectURL)
  //加载新的视频文件
  videoFile.value = file
  //为视频文件创建一个本地的URL，用于视频播放
  videoObjectURL = URL.createObjectURL(file)
  recordedChunks.value = []
  processedImgSrc.value = ''
  setTimeout(() => {
    if (sourceVideoRef.value) sourceVideoRef.value.src = videoObjectURL
  }, 100)
}
const clearVideo = () => {
  stopProcessing()
  videoFile.value = null
  processedImgSrc.value = ''
  recordedChunks.value = []
  if (videoObjectURL) { URL.revokeObjectURL(videoObjectURL); videoObjectURL = null }
}
const updateProgress = () => {
  const v = sourceVideoRef.value
  if (v && v.duration) videoProgress.value = (v.currentTime / v.duration) * 100
}

// ========== WebSocket ==========
const initWebSocket = () => {
  if (ws) ws.close()
  wsStatus.value = 'connecting'
  ws = new WebSocket('ws://localhost:8000/ws/video')
  ws.onopen = () => {
    wsStatus.value = 'connected'
    console.log('WebSocket连接成功')
    ElMessage.success('已连接到检测服务')
  }
  ws.onmessage = (event) => {
    console.log('收到消息，长度:', event.data.length)
    processedImgSrc.value = event.data
    frameCount++
  }
  ws.onerror = (error) => {
    console.error('WebSocket错误:', error)
    ElMessage.error('WebSocket 连接错误')
    stopProcessing()
  }
  ws.onclose = (event) => {
    console.log('WebSocket关闭，代码:', event.code, '原因:', event.reason)
    wsStatus.value = 'disconnected'
  }
}


// ========== 开始检测 + 录制 ==========
const startDetection = async () => {
  detectCount.value+=1;
  if (!videoFile.value) return

  // 先创建检测任务
  try {
    await createDetectionTask(videoFile.value.name)
  } catch (e) {
    return  // 创建失败则不继续
  }

  isProcessing.value = true
  recordedChunks.value = []
  initWebSocket()  // 直连 Python

  fpsTimer = setInterval(() => {
    currentFps.value = frameCount
    frameCount = 0
  }, 1000)

  setTimeout(() => {
    sourceVideoRef.value?.play()
    startRecording()
  }, 500)
}
// 使用 MediaRecorder 录制处理后的帧
const startRecording = () => {
  try {
    const video = sourceVideoRef.value;
    const origWidth = video.videoWidth;
    const origHeight = video.videoHeight;
    if (!origWidth || !origHeight) {
      console.warn('无法获取原始视频尺寸，录制可能异常');
      return;
    }

    // 创建与原视频同尺寸的离屏画布
    const offscreen = document.createElement('canvas');
    offscreen.width = origWidth;
    offscreen.height = origHeight;
    recordStream = offscreen.captureStream(15); // 帧率可保持 15fps

    mediaRecorder = new MediaRecorder(recordStream, { mimeType: 'video/webm;codecs=vp9' });
    mediaRecorder.ondataavailable = (e) => {
      if (e.data.size > 0) recordedChunks.value.push(e.data);
    };
    mediaRecorder.start(200);

    const ctx = offscreen.getContext('2d');
    // 每帧将后端返回的处理结果绘制到离屏画布（图像尺寸已与原视频一致）
    const drawLoop = setInterval(() => {
      if (!isProcessing.value) { clearInterval(drawLoop); return; }
      if (processedImgSrc.value) {
        const img = new Image();
        img.onload = () => {
          // 直接绘制，无需缩放（画布尺寸 == 图像尺寸）
          ctx.drawImage(img, 0, 0, offscreen.width, offscreen.height);
        };
        img.src = processedImgSrc.value;
      }
    }, 1000 / 10);

    // 存储清理函数（可选）
    window.__drawLoop = drawLoop;
  } catch (e) {
    console.warn('录制不支持或启动失败:', e);
  }
};

// ========== 截帧发送 ==========
const startSendingFrames = () => {
  if (!ws || ws.readyState !== WebSocket.OPEN) return;
  const video = sourceVideoRef.value;
  if (canvasRef.value && video) {
    canvasRef.value.width = video.videoWidth || 640;
    canvasRef.value.height = video.videoHeight || 480;
  }
  frameInterval = setInterval(sendCurrentFrame, 1000 /10);
};
const pauseSending = () => {
  if (frameInterval) { clearInterval(frameInterval); frameInterval = null }
}
const sendCurrentFrame = () => {
  if (ws?.readyState === WebSocket.OPEN && sourceVideoRef.value && canvasRef.value) {
    const video = sourceVideoRef.value;
    const canvas = canvasRef.value;
    const ctx = canvas.getContext('2d');

    // 直接使用视频原始尺寸
    const width = video.videoWidth;
    const height = video.videoHeight;

    if (width === 0 || height === 0) return;

    canvas.width = width;
    canvas.height = height;
    ctx.drawImage(video, 0, 0, width, height);

    // JPEG质量参数为0.5，为保证高清视频的时候仍然能够正常不卡顿
    ws.send(canvas.toDataURL('image/jpeg',0.3));
  }
};
// ========== 停止 ==========
const stopProcessing = () => {
  isProcessing.value = false
  pauseSending()
  sourceVideoRef.value?.pause()
  if (fpsTimer) { clearInterval(fpsTimer); fpsTimer = null; currentFps.value = 0 }

  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.onstop = async () => {
      // 所有数据块已收集到 recordedChunks
      if (recordedChunks.value.length > 0 && currentTaskId.value) {
        const blob = new Blob(recordedChunks.value, { type: 'video/webm' })
        await uploadRecordedVideo(currentTaskId.value, blob)
      }
    }
    mediaRecorder.stop()
  }

  if (ws) { ws.close(); ws = null }
  completeDetectionTask()
}

// ========== 下载功能 ==========
// 下载录制的完整处理视频
const downloadRecordedVideo = () => {
  if (!recordedChunks.value.length) {
    ElMessage.warning('没有可下载的视频')
    return
  }
  const blob = new Blob(recordedChunks.value, { type: 'video/webm' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `detected_${videoFile.value?.name || 'video'}.webm`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  ElMessage.success('开始下载处理后的视频')
}

// 下载当前单帧
const downloadCurrentFrame = () => {
  if (!processedImgSrc.value) return
  const a = document.createElement('a')
  a.href = processedImgSrc.value
  a.download = `frame_${Date.now()}.jpg`
  a.click()
  ElMessage.success('当前帧已下载')
}

const clearResult = () => { processedImgSrc.value = '' }

// ========== 返回上一页 ==========
// 如需自定义返回逻辑（如跳转到特定页面），请修改此函数
const goBack = () => {
  router.push('/homepage')
}

// 视频结束事件（如果需要）
const onVideoEnded = () => {
  // 可选：视频播放完毕后的处理
  console.log('视频播放结束')
}

onBeforeUnmount(() => {
  stopProcessing()
  if (videoObjectURL) URL.revokeObjectURL(videoObjectURL)
})
</script>

<style scoped>
/* ===== 返回按钮样式 ===== */
.back-btn {
  position: fixed;
  top: 1.2rem;
  left: 1.2rem;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: rgba(15, 25, 45, 0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(59, 130, 246, 0.4);
  border-radius: 2rem;
  color: #eef5ff;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.back-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: #3b82f6;
  transform: translateX(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.back-btn:active {
  transform: translateX(0);
}

.back-arrow {
  font-size: 1.2rem;
  line-height: 1;
}

.back-text {
  font-size: 0.9rem;
}

/* 响应式：小屏幕时只显示箭头 */
@media (max-width: 640px) {
  .back-text {
    display: none;
  }
  .back-btn {
    padding: 0.5rem 0.8rem;
  }
}

/* ===== 原有样式保持不变 ===== */
.compare-img {
  width: 100%;
  height: auto;
  border-radius: 0.75rem;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}
/* ===== 主容器 ===== */
.home-container {
  position: relative;
  min-height: 100vh;
  background: radial-gradient(circle at 20% 30%, #0a0f1e, #05070f);
  color: #eef5ff;
  font-family: 'Segoe UI', 'Poppins', 'Inter', system-ui, -apple-system, sans-serif;
  overflow-x: hidden;
}

/* ===== 背景装饰 ===== */
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
  animation: pulse 8s infinite alternate;
}
@keyframes pulse {
  0%   { opacity: 0.4; transform: scale(1); }
  100% { opacity: 0.8; transform: scale(1.1); }
}

/* ===== 新增：粒子效果 ===== */
.particles { position: absolute; width: 100%; height: 100%; }
.particle {
  position: absolute;
  border-radius: 50%;
  background: rgba(59, 130, 246, 0.6);
  animation: float linear infinite;
}
@keyframes float {
  0%   { transform: translateY(0) scale(1); opacity: 0.6; }
  50%  { transform: translateY(-30px) scale(1.2); opacity: 1; }
  100% { transform: translateY(0) scale(1); opacity: 0.6; }
}

/* ===== 主内容区 ===== */
.main-content {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem 4rem;
}

/* ===== 标题 ===== */
.header {
  text-align: center;
  margin-bottom: 3rem;
}
.title-badge {
  display: inline-block;
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.4);
  color: #60a5fa;
  font-size: 0.75rem;
  letter-spacing: 2px;
  padding: 0.25rem 1rem;
  border-radius: 2rem;
  margin-bottom: 1rem;
}
.title {
  font-size: 3rem;
  font-weight: 800;
  background: linear-gradient(135deg, #fff, #5dade2);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  letter-spacing: -0.02em;
  margin-bottom: 0.75rem;
}
.highlight {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
  font-weight: 900;
}
.subtitle {
  font-size: 1rem;
  color: #94a3b8;
  letter-spacing: 0.3px;
}

/* ===== 新增：状态栏 ===== */
.status-bar {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
  background: rgba(0,0,0,0.3);
  border-radius: 2rem;
  padding: 0.25rem 1rem;
  font-size: 0.85rem;
}
.status-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
}
.status-dot.disconnected { background: #ef4444; }
.status-dot.connecting   { background: #f59e0b; animation: blink 1s infinite; }
.status-dot.connected    { background: #10b981; box-shadow: 0 0 6px #10b981; }
@keyframes blink { 0%,100% { opacity: 1; } 50% { opacity: 0.3; } }
.fps-counter {
  background: rgba(59,130,246,0.2);
  border-radius: 1rem;
  padding: 0.1rem 0.5rem;
  color: #60a5fa;
  font-size: 0.8rem;
  margin-left: 0.5rem;
}

/* ===== 上传卡片 ===== */
.upload-card {
  background: rgba(15, 25, 45, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 2rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  padding: 2rem;
  margin-bottom: 2rem;
  transition: all 0.3s ease;
}
.upload-area {
  border: 2px dashed rgba(59, 130, 246, 0.5);
  border-radius: 1.5rem;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(0, 0, 0, 0.2);
}
.upload-area:hover {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.05);
  transform: scale(0.98);
}
.upload-area.drag-over {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.3);
}
.file-input { display: none; }

/* ===== 上传提示 ===== */
.upload-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}
.icon-wrapper svg { color: #3b82f6; opacity: 0.7; }
.prompt-text { font-size: 1.2rem; font-weight: 500; margin: 0; }
.hint-text { font-size: 0.85rem; color: #7e8a98; margin: 0; }

/* ===== 视频预览 ===== */
.video-preview { position: relative; }
.preview-video {
  width: 100%;
  max-height: 400px;
  border-radius: 1rem;
  background: #000;
  box-shadow: 0 8px 20px rgba(0,0,0,0.4);
}

/* ===== 新增：进度条 ===== */
.progress-bar-wrapper {
  width: 100%;
  height: 4px;
  background: rgba(255,255,255,0.1);
  border-radius: 2px;
  margin-top: 0.5rem;
  overflow: hidden;
}
.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.file-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: rgba(0,0,0,0.5);
  border-radius: 2rem;
  font-size: 0.9rem;
}
.file-name {
  max-width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.clear-btn {
  background: rgba(255,70,70,0.8);
  border: none;
  color: white
;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  font-size: 1.2rem;
  line-height: 1;
  cursor: pointer;
  transition: 0.2s;
}
.clear-btn:hover {
  background: #ff4444;
  transform: scale(1.05);
}

/* ===== 操作按钮 ===== */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
  margin-top: 2rem;
}
.btn {
  padding: 0.75rem 2rem;
  border-radius: 3rem;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  background: none;
}
.btn.primary {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}
.btn.primary:not(:disabled):hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.5);
}
.btn.primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.btn.secondary {
  border: 1px solid #3b82f6;
  color: #3b82f6;
  background: transparent;
}
.btn.secondary:hover:not(:disabled) {
  background: rgba(59, 130, 246, 0.1);
  transform: translateY(-2px);
}
.btn.secondary:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ===== 结果卡片 ===== */
.result-card {
  background: rgba(15, 25, 45, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 2rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  padding: 1.5rem;
  margin-bottom: 2rem;
}
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.2);
}
.result-header h3 {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0;
}
.result-actions {
  display: flex;
  gap: 1rem;
}
.download-btn {
  background: linear-gradient(135deg, #10b981, #059669);
  border: none;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 2rem;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
}
.download-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}
.clear-result {
  background: transparent;
  border: none;
  color: #f56c6c;
  cursor: pointer;
  font-size: 0.85rem;
  transition: color 0.2s;
}
.clear-result:hover {
  color: #ff8888;
}
.result-content {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 1rem;
  padding: 1rem;
}
</style>
