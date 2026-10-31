<template>
  <div class="glass-container">
    <div class="drone-bg"></div>
    <div class="glass-card">
      <div class="logo-container">
        <div class="drone-icon">
          <RegisterDronIcon width="60" height="60" color="#00d2ff" />
        </div>
      </div>
      <h2 class="title">无人机智能识别系统</h2>
      <p class="subtitle">加入我们的航拍智能识别网络</p>
      <form @submit.prevent="registerAccount">
        <div class="input-group">
          <div class="input-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 7C16 9.20914 14.2091 11 12 11C9.79086 11 8 9.20914 8 7C8 4.79086 9.79086 3 12 3C14.2091 3 16 4.79086 16 7Z" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 14C16.4183 14 20 15.7909 20 18V21H4V18C4 15.7909 7.58172 14 12 14Z" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input type="text" placeholder="用户名" class="glass-input" v-model="userName" />
        </div>
        <div class="input-group">
          <div class="input-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 4H20C21.1046 4 22 4.89543 22 6V18C22 19.1046 21.1046 20 20 20H4C2.89543 20 2 19.1046 2 18V6C2 4.89543 2.89543 4 4 4Z" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 6L12 13L2 6" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input type="email" placeholder="邮箱" class="glass-input" v-model="userEmail" />
        </div>
        <div class="input-group">
          <div class="input-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 15C15.866 15 19 11.866 19 8C19 4.13401 15.866 1 12 1C8.13401 1 5 4.13401 5 8C5 11.866 8.13401 15 12 15Z" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 15V17" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 21V19" stroke="#00d2ff" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <input type="password" placeholder="密码" class="glass-input" v-model="userPassword" />
        </div>
        <div class="exitAccount" v-if="exitAccount">{{exitAccount}}</div>
        <div class="exitAccount" v-if="registerMessage">{{registerMessage}}</div>
        <button type="submit" class="glass-button">创建账号</button>
      </form>
      <p class="footer-text">已有账号？ <router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup>
import {userMap} from '../mock/userDB.js'
import {ref} from 'vue';
import RegisterDronIcon from '../components/icons/registerDronIcon.vue'
import router from '../router'

let userName=ref('');
let userEmail=ref('');
let userPassword=ref('');
let exitAccount=ref('');
let registerMessage=ref('');

function registerAccount() {
  console.log('userMap:', userMap, '类型:', userMap instanceof Map);
  if(userName.value=='' || userEmail.value=='' || userPassword.value=='') {
    registerMessage.value='请填写完整信息';
    return;
  }
  if(userMap.has(userName.value)) {
    exitAccount.value='用户名已存在';
    registerMessage.value='账号创建失败';
    return ;
  }
  const newUser={
    name:userName.value,
    email:userEmail.value,
    password:userPassword.value,
    first:'',
    second:'',
    third:''
  }
  userMap.set(userName.value,newUser);
  registerMessage.value='账号创建成功';
  exitAccount.value='';
  router.push(`/emailquestionsset?userName=${userName.value}`)
}
</script>

<style scoped>
.exitAccount {
  text-align: center;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  background: rgba(0, 210, 255, 0.1);
  color: #00d2ff;
  font-size: 14px;
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
  width: 380px;
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
.footer-text {
  text-align: center;
  margin-top: 20px;
  color: rgba(0, 210, 255, 0.8);
  font-size: 0.9em;
}
.footer-text a {
  color: #00d2ff;
  text-decoration: none;
  font-weight: bold;
  text-shadow: 0 0 5px rgba(0, 210, 255, 0.5);
}
.footer-text a:hover {
  text-decoration: underline;
}
</style>
