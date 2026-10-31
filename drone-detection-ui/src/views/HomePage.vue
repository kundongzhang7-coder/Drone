<template>
  <!-- 返回按钮 -->
  <div class="home-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="scan-line"></div>
      <div class="grid"></div>
      <div class="glow"></div>
    </div>

    <!-- 主内容 -->
    <main class="main-content">
      <!-- 顶部导航栏（用户信息） -->
      <header class="top-bar">
        <button class="nav-back-btn" @click="goBack" aria-label="返回上一页">
          ← 返回
        </button>
        <div class="logo-area">
          <div class="logo-icon">✈️</div>
          <span class="logo-text">DroneVision</span>
        </div>
        <div class="user-menu" @click="toggleUserMenu">
          <img :src="userAvatar" class="avatar" alt="avatar" />
          <span class="username">{{ userName }}</span>
          <svg class="chevron" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
            <path
              fill-rule="evenodd"
              d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z"
              clip-rule="evenodd"
            />
          </svg>
          <transition name="fade">
            <div v-show="menuVisible" class="dropdown">
              <div class="dropdown-item" @click.stop="goToProfile">个人中心</div>
              <div class="dropdown-item" @click.stop="goToSettings">系统设置</div>
              <div class="dropdown-item" @click.stop="handleLogout">退出登录</div>
            </div>
          </transition>
        </div>
      </header>

      <!-- 欢迎区域 -->
      <section class="welcome-section">
        <h1 class="welcome-title">
          欢迎回来，<span class="highlight">{{ userName }}</span>
        </h1>
        <p class="welcome-desc">无人机视觉智能检测系统已就绪，开始新的检测任务吧。</p>
      </section>

      <!-- 快速开始卡片 -->
      <div class="quick-start-card">
        <div class="card-glow"></div>
        <div class="card-content">
          <div class="text">
            <h2>开始实时检测</h2>
            <p>上传视频或连接无人机，立即获取目标识别结果</p>
            <button class="start-btn" @click="startDetection">开始使用 →</button>
          </div>
          <div class="illustration">
            <svg viewBox="0 0 200 140" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path
                d="M40 70 L80 40 L120 40 L160 70 L120 100 L80 100 L40 70Z"
                stroke="#3b82f6"
                stroke-width="2"
                fill="rgba(59,130,246,0.2)"
              />
              <circle cx="100" cy="70" r="12" stroke="#3b82f6" stroke-width="2" fill="white" />
              <rect x="92" y="58" width="16" height="24" fill="#3b82f6" rx="2" />
              <path d="M70 80 L130 80" stroke="#3b82f6" stroke-width="2" stroke-dasharray="4 4" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 功能入口网格 -->
      <div class="feature-grid">
        <!-- 视频检测：新增按钮 -->
        <div class="feature-card">
          <div class="icon">🎥</div>
          <h3>视频检测</h3>
          <p>上传视频文件，实时识别目标</p>
          <button class="feature-btn" @click="goToDetection">进入检测</button>
        </div>

        <!-- 检测记录：新增按钮 -->
        <div class="feature-card">
          <div class="icon">📊</div>
          <h3>检测记录</h3>
          <p>查看历史检测结果与报告</p>
          <button class="feature-btn" @click="goToHistory">查看记录</button>
        </div>

        <!-- 个人中心：保持原有整卡点击跳转 -->
        <div class="feature-card" @click="goToProfile">
          <div class="icon">👤</div>
          <h3>个人中心</h3>
          <p>账号信息、偏好设置</p>
          <button class="feature-btn" @click="goToProfile">查看信息</button>
        </div>

        <!-- 模型管理：新增按钮 -->
        <div class="feature-card">
          <div class="icon">⚙️</div>
          <h3>模型管理</h3>
          <p>切换检测模型、性能调优</p>
          <button class="feature-btn" @click="goToModel">管理模型</button>
        </div>
      </div>

      <!-- 统计卡片（示例） -->
      <div class="stats-section">
        <div class="stat-card">
          <div class="stat-value">{{ detectCount }}</div>
          <div class="stat-label">检测次数</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">92.5%</div>
          <div class="stat-label">平均准确率</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">24 FPS</div>
          <div class="stat-label">实时帧率</div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { detectCount } from '../stores/shared.js'
import { ElMessage } from 'element-plus'

// 用户相关数据（示例）
const router = useRouter()
const route = useRoute()
const userName = ref(route.query.userName || '') // 可从 store 获取
const userAvatar = ref('')
const menuVisible = ref(false)
//返回按钮
const goBack = () => {
  router.push('/')
}

// 切换下拉菜单
const toggleUserMenu = () => {
  menuVisible.value = !menuVisible.value
}

// 点击其他区域关闭菜单（需要全局监听，这里简化，可自行扩展）
const closeMenu = () => {
  menuVisible.value = false
}

// 各个跳转方法（用户自行实现）
const startDetection = () => {
  router.push('/dashBoard')
  // 跳转到检测页面或打开检测组件
}

const goToDetection = () => {
  startDetection()
}

const goToHistory = () => {
  router.push({ path: '/detectionpanel', query: { userName: userName.value } })
}
//跳转到个人中心
const goToProfile = () => {
  router.push({ path: '/personalcenter', query: { userName: userName.value } })
}

const goToSettings = () => {
  console.log('跳转到设置')
}

const goToModel = () => {
  ElMessage.success('模型管理功能暂未开放')
}
//登出
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 确认退出
      router.push('/')
    })
    .catch((error) => {
      console.log('取消退出', error)
    })
}
onMounted(() => {
  // 读取已保存的头像
  const savedAvatar = localStorage.getItem(`user_avatar_${userName.value}`)
  if (savedAvatar) {
    userAvatar.value = savedAvatar
  } else {
    // 使用默认头像
    userAvatar.value = 'data:image/svg+xml,...' // 你的默认 Base64 头像
  }
})
</script>

<style scoped>
.nav-back-btn {
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(59, 130, 246, 0.4);
  color: #eef5ff;
  padding: 0.4rem 1rem;
  border-radius: 2rem;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  margin-right: 1rem;
}
.nav-back-btn:hover {
  background: rgba(59, 130, 246, 0.2);
  border-color: #3b82f6;
  transform: translateX(-2px);
}
/* 功能卡片内按钮 */
.feature-btn {
  margin-top: 0.75rem;
  padding: 0.45rem 1.4rem;
  border-radius: 3rem;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  border: 1px solid rgba(59, 130, 246, 0.5);
  background: transparent;
  color: #60a5fa;
  transition: all 0.2s;
}
.feature-btn:hover {
  background: rgba(59, 130, 246, 0.15);
  border-color: #3b82f6;
  transform: translateY(-1px);
}
.feature-btn.primary-btn {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border: none;
  color: white;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}
.feature-btn.primary-btn:hover {
  box-shadow: 0 6px 18px rgba(59, 130, 246, 0.5);
  transform: translateY(-2px);
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent 0%, rgba(59, 130, 246, 0.8) 50%, transparent 100%);
  animation: scanDown 6s linear infinite;
  opacity: 0.6;
}
@keyframes scanDown {
  0% {
    top: 0%;
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  90% {
    opacity: 0.6;
  }
  100% {
    top: 100%;
    opacity: 0;
  }
}

:deep(.el-button) {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border-radius: 2rem;
}
/* 复用之前的全局样式，并扩展新组件样式 */
.home-container {
  position: relative;
  min-height: 100vh;
  background: radial-gradient(circle at 20% 30%, #0a0f1e, #05070f);
  color: #eef5ff;
  font-family: 'Segoe UI', 'Poppins', 'Inter', system-ui, sans-serif;
  overflow-x: hidden;
}

/* 背景装饰（同前） */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
.grid {
  position: absolute;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(0, 255, 255, 0.08) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 255, 255, 0.08) 1px, transparent 1px);
  background-size: 50px 50px;
}
.glow {
  position: absolute;
  top: 20%;
  left: 10%;
  width: 80%;
  height: 60%;
  background: radial-gradient(ellipse at center, rgba(0, 150, 255, 0.15), transparent 70%);
  filter: blur(80px);
  animation: pulse 8s infinite alternate;
}
@keyframes pulse {
  0% {
    opacity: 0.4;
    transform: scale(1);
  }
  100% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

/* 主内容区 */
.main-content {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 1.5rem 2rem 3rem;
}

/* 顶部导航栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding: 0.5rem 0;
}
.logo-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.logo-icon {
  font-size: 1.8rem;
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.logo-text {
  font-weight: 700;
  font-size: 1.25rem;
  letter-spacing: 1px;
}
.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.05);
  padding: 0.4rem 0.8rem;
  border-radius: 3rem;
  transition: background 0.2s;
}
.user-menu:hover {
  background: rgba(255, 255, 255, 0.1);
}
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #3b82f6;
}
.username {
  font-size: 0.9rem;
  font-weight: 500;
}
.chevron {
  transition: transform 0.2s;
}
.dropdown {
  position: absolute;
  top: 110%;
  right: 0;
  background: rgba(20, 30, 50, 0.95);
  backdrop-filter: blur(12px);
  border-radius: 1rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  width: 140px;
  overflow: hidden;
  z-index: 10;
}
.dropdown-item {
  padding: 0.6rem 1rem;
  font-size: 0.85rem;
  transition: background 0.2s;
}
.dropdown-item:hover {
  background: rgba(59, 130, 246, 0.2);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 2rem;
}
.welcome-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}
.highlight {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  background-clip: text;
  -webkit-background-clip: text;
  color: transparent;
}
.welcome-desc {
  color: #94a3b8;
  font-size: 1rem;
}

/* 快速开始卡片 */
.quick-start-card {
  position: relative;
  background: rgba(15, 25, 45, 0.5);
  backdrop-filter: blur(12px);
  border-radius: 2rem;
  border: 1px solid rgba(59, 130, 246, 0.4);
  overflow: hidden;
  margin-bottom: 3rem;
  transition: transform 0.3s;
}
.quick-start-card:hover {
  transform: translateY(-4px);
  border-color: #3b82f6;
}
.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 30%, rgba(59, 130, 246, 0.2), transparent 70%);
  pointer-events: none;
}
.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem;
  gap: 2rem;
}
.text h2 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
}
.text p {
  color: #b9c7d9;
  margin-bottom: 1.5rem;
}
.start-btn {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border: none;
  padding: 0.7rem 1.8rem;
  border-radius: 3rem;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition:
    transform 0.2s,
    box-shadow 0.2s;
}
.start-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}
.illustration svg {
  width: 180px;
  height: auto;
}

/* 功能网格 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}
.feature-card {
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(4px);
  border-radius: 1.5rem;
  padding: 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid rgba(59, 130, 246, 0.2);
}
.feature-card:hover {
  background: rgba(59, 130, 246, 0.1);
  transform: translateY(-4px);
  border-color: #3b82f6;
}
.feature-card .icon {
  font-size: 2.2rem;
  margin-bottom: 1rem;
}
.feature-card h3 {
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}
.feature-card p {
  font-size: 0.85rem;
  color: #b9c7d9;
}

/* 统计卡片 */
.stats-section {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  flex-wrap: wrap;
}
.stat-card {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 1.2rem;
  padding: 1rem 1.8rem;
  text-align: center;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(59, 130, 246, 0.3);
  min-width: 120px;
}
.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #3b82f6;
}
.stat-label {
  font-size: 0.8rem;
  color: #b9c7d9;
  margin-top: 0.25rem;
}

/* 响应式 */
@media (max-width: 768px) {
  .main-content {
    padding: 1rem;
  }
  .card-content {
    flex-direction: column;
    text-align: center;
  }
  .welcome-title {
    font-size: 1.6rem;
  }
  .feature-grid {
    grid-template-columns: 1fr;
  }
  .stats-section {
    gap: 1rem;
  }
}
</style>
