<template>
  <div class="glass-container">
    <div class="drone-bg"></div>
    <div class="glass-card">
      <div class="logo-container">
        <div class="drone-icon">
          <RegisterDronIcon width="60" height="60" color="#00d2ff" />
        </div>
      </div>
      <h2 class="title">安全问题验证</h2>
      <p class="subtitle">请输入邮箱并回答以下安全问题以验证您的身份</p>

      <div class="input-group">
        <div class="input-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z" stroke="#00d2ff" stroke-width="2"/>
            <path d="M22 6L12 13L2 6" stroke="#00d2ff" stroke-width="2"/>
          </svg>
        </div>
        <input type="email" placeholder="请输入邮箱地址" class="glass-input" v-model="userEmail" required />
        <div v-if="emailError" class="error-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 9V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#ff4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
      </div>

      <form class="questions-form" @submit.prevent="handleSubmit">
        <div class="question-group">
          <label class="question-label">问题1：您的姓氏？</label>
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <input type="text" placeholder="请输入1-3个汉字" class="glass-input" v-model="first" required :disabled="!isEmailValid" />
            <div v-if="firsterr" class="error-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 9V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#ff4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </div>

        <div class="question-group">
          <label class="question-label">问题2：出生年份？</label>
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <input type="text" placeholder="请输入出生年份(例如：1995)" class="glass-input" required :disabled="!isEmailValid" v-model="second" />
            <div v-if="seconderr" class="error-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 9V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#ff4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </div>

        <div class="question-group">
          <label class="question-label">问题3：所在高校？</label>
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <input type="text" placeholder="请输入高校全称" class="glass-input" v-model="third" required :disabled="!isEmailValid" />
            <div v-if="thirderr" class="error-icon">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 9V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="#ff4444" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </div>

        <div class="message"></div>
        <button type="submit" class="glass-button" :disabled="!isEmailValid||firsterr||seconderr||thirderr||!(first.trim()&&second.trim()&&third.trim())">提交验证</button>
      </form>

      <div class="back-home">
        <router-link to="/" class="back-link">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 12H5M12 19l-7-7 7-7" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import RegisterDronIcon from '../components/icons/registerDronIcon.vue'
import { onMounted, ref, watch } from 'vue'
import { userMap } from '../mock/userDB.js'
import { useRouter, useRoute } from 'vue-router'
import { university } from '../mock/userDB.js'

const router = useRouter()
const userEmail = ref('')
const firsterr = ref(false)
const seconderr = ref(false)
const thirderr = ref(false)
const first = ref('')
const second = ref('')
const third = ref('')
const isEmailValid = ref(false)
const emailError = ref('')
const userName = ref('')
const route = useRoute()

onMounted(() => {
  userName.value = route.query.userName as string || ''
  console.log('接收到的用户名:', userName.value)
  if (userName.value && userMap.has(userName.value)) {
    const user = userMap.get(userName.value)
    userEmail.value = user.email
    checkEmailExists(userEmail.value)
  }
})

const checkEmailExists = (email: string): boolean => {
  for (const user of userMap.values()) {
    if (user.email === email) return true
  }
  return false
}

let timeoutId: number
watch(userEmail, (newEmail) => {
  clearTimeout(timeoutId)
  timeoutId = setTimeout(() => {
    if (!newEmail) {
      isEmailValid.value = false
      emailError.value = ''
      first.value = second.value = third.value = ''
      return
    }
    const exists = checkEmailExists(newEmail)
    if (!exists) {
      emailError.value = '邮箱不存在'
      isEmailValid.value = false
      first.value = second.value = third.value = ''
    } else {
      isEmailValid.value = true
      emailError.value = ''
    }
  }, 300)
})

watch(first, (newFirst) => judgeFirst(newFirst))
watch(second, (newSecond) => judgeSecond(newSecond))
watch(third, (newThird) => judgeThird(newThird))

const judgeFirst = (String: string) => {
  const val = String.trim()
  if (!val) { firsterr.value = true; return false }
  if (!/^[\u4e00-\u9fa5]+$/.test(val)) { firsterr.value = true; return false }
  if (val.length > 4) { firsterr.value = true; return false }
  firsterr.value = false
  return true
}

const judgeSecond = (String: string) => {
  if (!String.trim()) return
  if (isNaN(Number(String.trim()))) { seconderr.value = true; return }
  if (Number(String.trim()) < 1900 || Number(String.trim()) > 2024) { seconderr.value = true; return }
  seconderr.value = false
}

const judgeThird = (String: string) => {
  if (!String.trim()) return
  for (let uni of university) {
    if (uni === String.trim()) { thirderr.value = false; return }
  }
  thirderr.value = true
}

const handleSubmit = () => {
  userMap.get(userName.value).first = first.value
  userMap.get(userName.value).second = second.value
  userMap.get(userName.value).third = third.value
  router.push('/login')
}
</script>

<style scoped>
.error-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  animation: shake 0.5s ease-in-out;
}
@keyframes shake {
  0%, 100% { transform: translateY(-50%) translateX(0); }
  25% { transform: translateY(-50%) translateX(-3px); }
  75% { transform: translateY(-50%) translateX(3px); }
}
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
.title {
  color: #00d2ff;
  text-align: center;
  margin-bottom: 10px;
  font-weight: 600;
  font-size: 24px;
  text-shadow: 0 0 15px rgba(0, 210, 255, 0.6);
  letter-spacing: 1px;
}
.subtitle {
  color: rgba(0, 210, 255, 0.7);
  text-align: center;
  margin-bottom: 30px;
  font-size: 14px;
  letter-spacing: 0.5px;
}
.input-group {
  margin-bottom: 20px;
  position: relative;
}
.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  opacity: 0.7;
  transition: all 0.3s ease;
}
.glass-input {
  width: 100%;
  padding: 15px 15px 15px 45px;
  border: none;
  border-radius: 12px;
  background: rgba(0, 210, 255, 0.1);
  backdrop-filter: blur(4px);
  color: #fff;
  font-size: 16px;
  outline: none;
  transition: all 0.3s ease;
  box-sizing: border-box;
  border: 1px solid rgba(0, 210, 255, 0.2);
  font-family: inherit;
}
.input-group:focus-within .input-icon {
  opacity: 1;
  transform: translateY(-50%) scale(1.1);
}
.glass-input:focus {
  background: rgba(0, 210, 255, 0.2);
  box-shadow: 0 0 15px rgba(0, 210, 255, 0.4);
  border-color: #00d2ff;
}
.glass-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}
.question-group {
  margin-bottom: 25px;
}
.question-label {
  display: block;
  color: #00d2ff;
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 10px;
  text-shadow: 0 0 5px rgba(0, 210, 255, 0.3);
}
.glass-button {
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #00d2ff 0%, #0077b3 100%);
  color: #0a0a0a;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 20px;
  box-shadow: 0 6px 20px rgba(0, 210, 255, 0.4);
  font-family: inherit;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}
.glass-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}
.glass-button:hover::before {
  left: 100%;
}
.glass-button:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 210, 255, 0.5);
}
.glass-button:disabled {
  background: rgba(0, 210, 255, 0.3);
  color: rgba(0, 0, 0, 0.5);
  cursor: not-allowed;
  transform: none;
}
.back-home {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 210, 255, 0.2);
}
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: rgba(0, 210, 255, 0.8);
  text-decoration: none;
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  background: rgba(0, 210, 255, 0.1);
}
.back-link:hover {
  color: #00d2ff;
  background: rgba(0, 210, 255, 0.2);
  text-decoration: none;
  transform: translateX(-3px);
}
.message {
  text-align: center;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(0, 210, 255, 0.1);
  color: #00d2ff;
  font-size: 14px;
}
</style>
