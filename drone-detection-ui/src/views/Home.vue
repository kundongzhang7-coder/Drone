<template>
  <div class="home">
    <!-- 视频背景组件 -->
    <video-background
      src="/videos/droneVideo.mp4"
      overlay="linear-gradient(45deg, rgba(0,0,0,0.5), rgba(0,0,0,0.2))"
      style="height: 100vh; width: 100vw; position: fixed; top: 0; left: 0; z-index: 0;"
    />

    <!-- 页面内容 -->
    <div class="content">
      <h1>欢迎来到无人机检测系统</h1>
      <p>这里是首页，你可以从这里开始使用我们的应用。</p>

      <!-- 按钮容器：使两个按钮并排居中 -->
      <div class="button-group">
        <router-link to="/register" class="action-btn register-btn">立即注册</router-link>
        <div class="action-btn product-btn" @click="handleProductIntro">产品介绍</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import VideoBackground from '@hintay/vue-video-background'
import {useRouter} from 'vue-router'
const router = useRouter()
const handleProductIntro = () => {

  router.push('/productIntroduction')
}
</script>

<style scoped>
.home {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
  text-align: center;
  color: white;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.5);
  width: 80%;
  max-width: 800px;
}

h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
  animation: fadeInUp 1s ease;
}

p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
  animation: fadeInUp 1s ease 0.2s both;
}

/* 按钮组：使用 flex 布局实现并排居中 */
.button-group {
  display: flex;
  justify-content: center;
  gap: 1.5rem;          /* 两个按钮之间的间距 */
  flex-wrap: wrap;      /* 小屏时自动换行，仍保持友好 */
  animation: fadeInUp 1s ease 0.4s both;
}

/* 公共按钮样式（注册 & 产品介绍共用） */
.action-btn {
  display: inline-block;
  padding: 12px 30px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  border: 2px solid white;
  border-radius: 50px;
  color: white;
  font-weight: bold;
  text-decoration: none;
  transition: all 0.3s ease;
  cursor: pointer;
  user-select: none;
}

.action-btn:hover {
  background: white;
  color: #333;
  transform: scale(1.05);
}

/* 单独保留类名以备后续特殊扩展（目前样式已统一） */
.register-btn {}
.product-btn {}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
