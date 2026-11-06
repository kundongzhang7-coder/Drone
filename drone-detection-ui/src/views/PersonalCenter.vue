<template>
  <div class="personal-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="grid"></div>
      <div class="glow"></div>
    </div>

    <main class="main-content">
      <!-- 头部导航 -->
      <header class="top-bar">
        <div class="logo-area">
          <div class="logo-icon">✈️</div>
          <span class="logo-text">DroneVision</span>
        </div>
        <div class="user-info" @click="goToHome">
          <span class="back-link">← 返回主页</span>
        </div>
      </header>

      <!-- 个人中心卡片 -->
      <div class="profile-card">
        <div class="card-glow"></div>
        <div class="profile-header">
          <div class="avatar-wrapper">
            <img :src="userAvatar" class="avatar" alt="avatar" />
            <div class="avatar-edit" @click="changeAvatar">✎</div>
          </div>
          <h2 class="username">{{ userInfo.username }}</h2>
          <p class="user-role">普通用户</p>
        </div>

        <div class="profile-body">
          <el-form :model="userInfo" label-width="80px" label-position="left">
            <!-- 用户名（不可修改） -->
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled />
            </el-form-item>

            <!-- 昵称 -->
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" :disabled="!editMode" placeholder="请输入昵称" />
            </el-form-item>

            <!-- 邮箱 -->
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" :disabled="!editMode" placeholder="请输入邮箱" />
            </el-form-item>

            <!-- 手机号 -->
            <el-form-item label="手机号">
              <el-input v-model="userInfo.phone" :disabled="!editMode" placeholder="请输入手机号" />
            </el-form-item>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button v-if="!editMode" type="primary" @click="enableEdit">编辑资料</el-button>
              <template v-else>
                <el-button type="primary" @click="saveProfile">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
              </template>
            </div>
          </el-form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProfile, updateProfile, uploadAvatar } from '../api/auth'

const router = useRouter()
const editMode = ref(false)

// 用户信息（副本，用于编辑）
const userInfo = reactive({
  id: null as number | null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
})

// 原始数据（用于取消编辑时恢复）
let original: Record<string, unknown> = { ...userInfo }

// 头像（默认 Base64 图标，登录后替换为 COS 地址）
const userAvatar = ref(
  'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 24 24" fill="%233b82f6"%3E%3Cpath d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/%3E%3C/svg%3E'
)

// 加载当前登录用户资料
const loadUserData = async () => {
  try {
    const res = await getProfile()
    const u = res.data
    Object.assign(userInfo, {
      id: u.id,
      username: u.username,
      nickname: u.nickname || '',
      email: u.email || '',
      phone: u.phone || '',
      avatar: u.avatar || '',
    })
    original = { ...userInfo }
    if (u.avatar) userAvatar.value = u.avatar
  } catch (error) {
    // 401 已由 request 拦截器统一处理
  }
}

const enableEdit = () => {
  editMode.value = true
}

const cancelEdit = () => {
  Object.assign(userInfo, original)
  editMode.value = false
}

const saveProfile = async () => {
  try {
    await updateProfile({
      nickname: userInfo.nickname,
      email: userInfo.email,
      phone: userInfo.phone,
    })
    ElMessage.success('保存成功')
    original = { ...userInfo }
    editMode.value = false
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  }
}

// 更换头像：上传到腾讯云 COS，返回可访问 URL
const changeAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (event: Event) => {
    const file = (event.target as HTMLInputElement).files?.[0]
    if (!file) return
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 2MB')
      return
    }
    const formData = new FormData()
    formData.append('file', file)
    try {
      const res = await uploadAvatar(formData)
      const url = res.data
      userAvatar.value = url
      userInfo.avatar = url
      ElMessage.success('头像更新成功')
    } catch (error) {
      ElMessage.error('头像上传失败')
    }
  }
  input.click()
}

// 返回主页
const goToHome = () => {
  router.push('/homepage')
}

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
/* 复用主页背景样式 */
.personal-container {
  position: relative;
  min-height: 100vh;
  background: radial-gradient(circle at 20% 30%, #0a0f1e, #05070f);
  color: #eef5ff;
  font-family: 'Segoe UI', 'Poppins', 'Inter', system-ui, sans-serif;
  overflow-x: hidden;
}

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

.main-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

/* 顶部栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
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
}
.user-info .back-link {
  cursor: pointer;
  color: #3b82f6;
  transition: color 0.2s;
}
.user-info .back-link:hover {
  color: #8b5cf6;
}

/* 个人卡片 */
.profile-card {
  position: relative;
  background: rgba(15, 25, 45, 0.6);
  backdrop-filter: blur(12px);
  border-radius: 2rem;
  border: 1px solid rgba(59, 130, 246, 0.3);
  overflow: hidden;
  transition: transform 0.3s;
}
.card-glow {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 30%, rgba(59, 130, 246, 0.2), transparent 70%);
  pointer-events: none;
}
.profile-header {
  text-align: center;
  padding: 2rem 2rem 1rem;
  border-bottom: 1px solid rgba(59, 130, 246, 0.2);
}
.avatar-wrapper {
  position: relative;
  display: inline-block;
}
.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #3b82f6;
  object-fit: cover;
}
.avatar-edit {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  background: #3b82f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  transition: transform 0.2s;
}
.avatar-edit:hover {
  transform: scale(1.1);
  background: #8b5cf6;
}
.username {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0.5rem 0 0.25rem;
}
.user-role {
  color: #94a3b8;
  font-size: 0.85rem;
  margin-bottom: 0;
}
.profile-body {
  padding: 2rem;
}
.action-buttons {
  margin-top: 2rem;
  display: flex;
  gap: 1rem;
  justify-content: center;
}
:deep(.el-form-item__label) {
  color: #cbd5e1;
}
:deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.3);
  box-shadow: none;
  border: 1px solid rgba(59, 130, 246, 0.3);
  border-radius: 12px;
}
:deep(.el-input.is-disabled .el-input__wrapper) {
  background: rgba(0, 0, 0, 0.2);
  border-color: rgba(59, 130, 246, 0.2);
}
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #3b82f6, #8b5cf6);
  border: none;
  border-radius: 2rem;
  padding: 0.5rem 1.2rem;
}
:deep(.el-button--primary:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}
@media (max-width: 768px) {
  .main-content {
    padding: 1rem;
  }
  .profile-body {
    padding: 1rem;
  }
}
</style>
