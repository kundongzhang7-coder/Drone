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
      <p class="subtitle">请输入用户名并回答安全问题以找回密码</p>

      <form class="verify-form" @submit.prevent="submitAnswer">
        <div class="input-group">
          <div class="input-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 7C16 9.20914 14.2091 11 12 11C9.79086 11 8 9.20914 8 7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 14C16.4183 14 20 15.7909 20 18V21H4V18C4 15.7909 7.58172 14 12 14Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input type="text" placeholder="请输入用户名" class="glass-input" v-model="userName" required />
        </div>

        <div class="question-group">
          <label class="question-label">问题1：您的姓氏？</label>
          <div class="input-group">
            <div class="input-icon">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="#00d2ff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <input type="text" placeholder="请输入答案" class="glass-input" v-model="firstAns" required :disabled="!userExit" />
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
            <input type="text" placeholder="请输入答案" class="glass-input" v-model="secondAns" required :disabled="!userExit" />
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
            <input type="text" placeholder="请输入答案" class="glass-input" v-model="thirdAns" required :disabled="!userExit" />
          </div>
        </div>

        <button type="submit" class="glass-button" :disabled="!userExit || firsterr || seconderr || thirderr">验证答案</button>
        <div class="message">{{ message }}</div>
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
import { ref, watch } from 'vue'
import { userMap } from '../mock/userDB.js'
import router from '../router'

const userName = ref('')
const firstAns = ref('')
const secondAns = ref('')
const thirdAns = ref('')
const firsterr = ref(true)
const seconderr = ref(true)
const thirderr = ref(true)
const userExit = ref(false)
const message = ref('')

watch(userName, (newName) => {
  if (userMap.has(newName.trim())) {
    userExit.value = true
    message.value = ''
  } else {
    userExit.value = false
    message.value = '用户名不存在'
  }
})

watch(firstAns, (val) => { firsterr.value = val.trim() === '' })
watch(secondAns, (val) => { seconderr.value = val.trim() === '' })
watch(thirdAns, (val) => { thirderr.value = val.trim() === '' })

const clearInput = () => {
  firstAns.value = ''
  secondAns.value = ''
  thirdAns.value = ''
  userName.value = ''
}

const submitAnswer = () => {
  const user = userMap.get(userName.value)
  if (!user) {
    message.value = '用户不存在'
    return
  }
  if (user.first !== firstAns.value) {
    message.value = '作答错误'
    clearInput()
    return
  }
  if (user.second !== secondAns.value) {
    message.value = '作答错误'
    clearInput()
    return
  }
  if (user.third !== thirdAns.value) {
    message.value = '作答错误'
    clearInput()
    return
  }
  message.value = '验证成功'
  router.push(`/checkpassword?userName=${userName.value}`)
}
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
.message {
  text-align: center;
  margin-top: 15px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(0, 210, 255, 0.1);
  color: #00d2ff;
  font-size: 14px;
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
</style>
