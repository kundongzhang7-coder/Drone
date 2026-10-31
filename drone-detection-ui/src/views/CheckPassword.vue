<template>
  <div class="glass-container">
    <div class="drone-bg"></div>
    <div class="glass-card">
      <div class="logo-container">
        <div class="drone-icon">
          <RegisterDronIcon width="60" height="60" color="#00d2ff" />
        </div>
      </div>

      <div class="success-section">
        <div class="success-icon">
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 6L9 17L4 12" stroke="#00d2ff" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h2 class="success-title">验证成功</h2>
        <p class="success-subtitle">您的身份验证已通过，以下是您的密码信息</p>
      </div>

      <div class="password-card">
        <div class="password-header">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 15C15.866 15 19 11.866 19 8C19 4.13401 15.866 1 12 1C8.13401 1 5 4.13401 5 8C5 11.866 8.13401 15 12 15Z" stroke="#00d2ff" stroke-width="2"/>
            <path d="M12 15V17" stroke="#00d2ff" stroke-width="2"/>
            <path d="M12 21V19" stroke="#00d2ff" stroke-width="2"/>
          </svg>
          <span class="password-label">您的密码</span>
        </div>

        <div class="password-display">
          <span class="password-text">{{ maskedPassword }}</span>
          <button class="copy-button" @click="copyPassword">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 8V5C16 4.46957 15.7893 3.96086 15.4142 3.58579C15.0391 3.21071 14.5304 3 14 3H5C4.46957 3 3.96086 3.21071 3.58579 3.58579C3.21071 3.96086 3 4.46957 3 5V14C3 14.5304 3.21071 15.0391 3.58579 15.4142C3.96086 15.7893 4.46957 16 5 16H8M10 8H19C20.1046 8 21 8.89543 21 10V19C21 20.1046 20.1046 21 19 21H10C8.89543 21 8 20.1046 8 19V10C8 8.89543 8.89543 8 10 8Z" stroke="#00d2ff" stroke-width="2"/>
            </svg>
            复制
          </button>
        </div>

        <div class="password-tips">
          <p>💡 建议您立即登录并修改密码</p>
          <p>🔒 请妥善保管您的密码信息</p>
        </div>
      </div>

      <div class="action-buttons">
        <button class="glass-button primary" @click="goToLogin">立即登录</button>
        <button class="glass-button secondary" @click="goToHome">返回首页</button>
      </div>

      <div class="message" v-if="message">{{ message }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import RegisterDronIcon from '../components/icons/registerDronIcon.vue'
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { userMap } from '../mock/userDB.js'

const router = useRouter()
const route = useRoute()
const message = ref('')
const userName = ref('')
const password = ref('')

const maskedPassword = computed(() => {
  if (!password.value) return '**********'
  return '*'.repeat(password.value.length)
})

onMounted(() => {
  userName.value = route.query.userName as string || ''
  if (userMap.has(userName.value)) {
    password.value = userMap.get(userName.value).password
  }
})

const copyPassword = async () => {
  try {
    await navigator.clipboard.writeText(password.value)
    message.value = '密码已复制到剪贴板'
    setTimeout(() => { message.value = '' }, 3000)
  } catch (error) {
    message.value = '复制失败，请手动复制'
  }
}

const goToLogin = () => router.push('/login')
const goToHome = () => router.push('/')
</script>

<style scoped>
.glass-container {
  background: linear-gradient(135deg, #0a0e17 0%, #0a0a0a 100%);
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  position: relative;
  overflow: hidden;
}
.drone-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(0, 210, 255, 0.08) 0%, transparent 50%),
  radial-gradient(circle at 80% 20%, rgba(0, 210, 255, 0.08) 0%, transparent 50%),
  radial-gradient(circle at 40% 40%, rgba(0, 210, 255, 0.03) 0%, transparent 50%);
  animation: float 20s ease-in-out infinite;
}
@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(1deg); }
}
.glass-card {
  background: rgba(10, 14, 23, 0.75);
  backdrop-filter: blur(15px);
  border-radius: 32px;
  padding: 50px;
  width: 450px;
  box-shadow: 0 8px 32px 0 rgba(0, 210, 255, 0.2), 0 0 0 1px rgba(0, 210, 255, 0.1) inset;
  border: 1px solid rgba(0, 210, 255, 0.3);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}
.glass-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px 0 rgba(0, 210, 255, 0.25), 0 0 0 1px rgba(0, 210, 255, 0.15) inset;
}
.logo-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}
.drone-icon {
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, rgba(0, 210, 255, 0.2) 0%, rgba(0, 210, 255, 0.05) 70%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 20px rgba(0, 210, 255, 0.3);
  animation: pulse 2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { box-shadow: 0 0 20px rgba(0, 210, 255, 0.3); }
  50% { box-shadow: 0 0 30px rgba(0, 210, 255, 0.5); }
}
.success-section {
  text-align: center;
  margin-bottom: 30px;
}
.success-icon {
  margin-bottom: 20px;
  animation: success 1s ease-in-out;
}
@keyframes success {
  0% { transform: scale(0); opacity: 0; }
  70% { transform: scale(1.2); opacity: 1; }
  100% { transform: scale(1); opacity: 1; }
}
.success-title {
  color: #00d2ff;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 10px;
  text-shadow: 0 0 15px rgba(0, 210, 255, 0.6);
}
.success-subtitle {
  color: rgba(0, 210, 255, 0.8);
  font-size: 16px;
  line-height: 1.5;
}
.password-card {
  background: rgba(0, 210, 255, 0.1);
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
  border: 1px solid rgba(0, 210, 255, 0.2);
  backdrop-filter: blur(8px);
}
.password-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.password-label {
  color: #00d2ff;
  font-size: 18px;
  font-weight: 600;
}
.password-display {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(0, 210, 255, 0.1);
  border-radius: 12px;
  padding: 15px 20px;
  margin-bottom: 20px;
  border: 1px solid rgba(0, 210, 255, 0.3);
}
.password-text {
  color: #00d2ff;
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  font-family: 'Courier New', monospace;
}
.copy-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(0, 210, 255, 0.2);
  border: 1px solid rgba(0, 210, 255, 0.4);
  color: #00d2ff;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}
.copy-button:hover {
  background: rgba(0, 210, 255, 0.3);
  transform: translateY(-1px);
}
.password-tips {
  color: rgba(0, 210, 255, 0.7);
  font-size: 14px;
  line-height: 1.6;
}
.password-tips p {
  margin: 5px 0;
}
.action-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}
.glass-button {
  flex: 1;
  padding: 15px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
}
.glass-button.primary {
  background: linear-gradient(135deg, #00d2ff 0%, #0077b3 100%);
  color: #0a0a0a;
  box-shadow: 0 6px 20px rgba(0, 210, 255, 0.4);
}
.glass-button.secondary {
  background: rgba(0, 210, 255, 0.2);
  color: #00d2ff;
  border: 1px solid rgba(0, 210, 255, 0.3);
}
.glass-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 210, 255, 0.5);
}
.message {
  text-align: center;
  padding: 10px;
  border-radius: 8px;
  background: rgba(0, 210, 255, 0.1);
  color: #00d2ff;
  font-size: 14px;
  animation: fadeIn 0.3s ease;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
