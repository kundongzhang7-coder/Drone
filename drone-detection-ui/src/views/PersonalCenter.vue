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

            <!-- 邮箱 -->
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" :disabled="!editMode" placeholder="请输入邮箱" />
            </el-form-item>

            <!-- 姓氏 -->
            <el-form-item label="姓氏">
              <el-input v-model="userInfo.first" :disabled="!editMode" placeholder="请输入姓氏" />
            </el-form-item>

            <!-- 出生年份 -->
            <el-form-item label="出生年份">
              <el-input
                v-model="userInfo.second"
                :disabled="!editMode"
                placeholder="例如：2000"
              />
            </el-form-item>

            <!-- 学校（下拉选择） -->
            <el-form-item label="学校">
              <el-select
                v-model="userInfo.third"
                :disabled="!editMode"
                placeholder="请选择学校"
                filterable
                allow-create
              >
                <el-option
                  v-for="school in university"
                  :key="school"
                  :label="school"
                  :value="school"
                />
              </el-select>
            </el-form-item>

            <!-- 操作按钮 -->
            <div class="action-buttons">
              <el-button v-if="!editMode" type="primary" @click="enableEdit">编辑资料</el-button>
              <template v-else>
                <el-button type="primary" @click="saveProfile">保存</el-button>
                <el-button @click="cancelEdit">取消</el-button>
              </template>
              <el-button type="danger" plain @click="changePassword">修改密码</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userMap, university } from '../mock/userDB'

const router = useRouter()
const route = useRoute()
const editMode = ref(false)

// 获取当前登录用户名（从路由 query 获取）
const currentUsername = route.query.userName as string || ''

// 用户信息（副本，用于编辑）
const userInfo = reactive({
  username: '',
  password: '',
  email: '',
  first: '',
  second: '',
  third: '',
})

// 原始用户数据（用于取消编辑时恢复）
let originalUser: any = null

// 头像（默认 Base64 图标，后期可替换）
let userAvatar = ref(
  'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 24 24" fill="%233b82f6"%3E%3Cpath d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/%3E%3C/svg%3E'
)

// 加载用户数据
const loadUserData = () => {
  if (!currentUsername) {
    ElMessage.error('未登录，请先登录')
    router.push('/login')
    return
  }

  const user = userMap.get(currentUsername)
  if (!user) {
    ElMessage.error('用户不存在')
    router.push('/login')
    return
  }

  // 复制数据到响应式对象
  userInfo.username = user.name
  userInfo.password = user.password
  userInfo.email = user.email || ''
  userInfo.first = user.first || ''
  userInfo.second = user.second || ''
  userInfo.third = user.third || ''

  // 保存原始数据副本
  originalUser = { ...user }

  // 加载已保存的头像
  const savedAvatar = localStorage.getItem(`user_avatar_${currentUsername}`)
  if (savedAvatar) {
    userAvatar.value = savedAvatar
  }
}

// 启用编辑模式
const enableEdit = () => {
  editMode.value = true
}

// 取消编辑，恢复原始数据
const cancelEdit = () => {
  if (originalUser) {
    userInfo.email = originalUser.email || ''
    userInfo.first = originalUser.first || ''
    userInfo.second = originalUser.second || ''
    userInfo.third = originalUser.third || ''
  }
  editMode.value = false
}

// 校验表单数据
const validateForm = (): boolean => {
  const email = userInfo.email.trim()
  const firstName = userInfo.first.trim()
  const birthYear = userInfo.second.trim()
  const school = userInfo.third.trim()

  // 邮箱非空校验
  if (!email) {
    ElMessage.error('邮箱不能为空')
    return false
  }
  // 邮箱格式校验
  const emailRegex = /^[^\s@]+@([^\s@]+\.)+[^\s@]+$/
  if (!emailRegex.test(email)) {
    ElMessage.error('请输入正确的邮箱格式')
    return false
  }

  // 姓氏非空
  if (!firstName) {
    ElMessage.error('姓氏不能为空')
    return false
  }
  // 姓氏只能包含中文
  if (!/^[\u4e00-\u9fa5]+$/.test(firstName)) {
    ElMessage.error('姓氏只能包含中文')
    return false
  }
  // 姓氏长度最多4个汉字
  if (firstName.length > 4) {
    ElMessage.error('姓氏最多4个汉字')
    return false
  }

  // 出生年份非空
  if (!birthYear) {
    ElMessage.error('出生年份不能为空')
    return false
  }
  // 出生年份必须是数字
  const yearNum = Number(birthYear)
  if (isNaN(yearNum)) {
    ElMessage.error('出生年份必须是数字')
    return false
  }
  // 年份范围校验（1900-2023，可根据实际调整）
  if (yearNum < 1900 || yearNum > 2023) {
    ElMessage.error('出生年份范围应在1900-2023之间')
    return false
  }

  // 学校非空
  if (!school) {
    ElMessage.error('学校不能为空')
    return false
  }

  return true
}

// 保存修改
const saveProfile = async () => {
  // 先校验数据
  if (!validateForm()) return

  try {
    const user = userMap.get(currentUsername)
    if (!user) throw new Error('用户不存在')

    // 更新用户信息（Proxy 会自动保存到 localStorage）
    user.email = userInfo.email
    user.first = userInfo.first
    user.second = userInfo.second
    user.third = userInfo.third

    // 更新原始数据副本
    originalUser = { ...user }

    ElMessage.success('保存成功')
    editMode.value = false
  } catch (error) {
    ElMessage.error('保存失败，请重试')
    console.error(error)
  }
}

// 修改密码
const changePassword = async () => {
  try {
    const { value: formValue } = await ElMessageBox.prompt('请输入旧密码', '验证身份', {
      confirmButtonText: '下一步',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '旧密码',
    })

    const user = userMap.get(currentUsername)
    if (!user) throw new Error('用户不存在')

    if (formValue !== user.password) {
      ElMessage.error('旧密码错误')
      return
    }

    const { value: newPassword } = await ElMessageBox.prompt('请输入新密码', '修改密码', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '新密码',
      inputValidator: (val) => {
        if (!val) return '密码不能为空'
        if (val.length < 6) return '密码至少6位'
        return true
      },
    })

    user.password = newPassword
    ElMessage.success('密码修改成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// 更换头像
const changeAvatar = () => {
  // 创建隐藏的 file input
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (event: Event) => {
    const file = (event.target as HTMLInputElement).files?.[0]
    if (!file) return

    // 限制图片大小（2MB）
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.warning('图片大小不能超过 2MB')
      return
    }

    const reader = new FileReader()
    reader.onload = (e) => {
      const base64 = e.target?.result as string
      if (base64) {
        userAvatar.value = base64
        // 保存到 localStorage
        localStorage.setItem(`user_avatar_${currentUsername}`, base64)
        ElMessage.success('头像更新成功')
      }
    }
    reader.onerror = () => {
      ElMessage.error('读取图片失败')
    }
    reader.readAsDataURL(file)
  }
  input.click()
}

// 返回主页
const goToHome = () => {
  router.push({ path: 'homepage', query: { userName: currentUsername } })
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
:deep(.el-select .el-input__wrapper) {
  background: rgba(0, 0, 0, 0.3);
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
:deep(.el-button--danger.is-plain) {
  background: transparent;
  border: 1px solid #f56c6c;
  color: #f56c6c;
  border-radius: 2rem;
}
:deep(.el-button--danger.is-plain:hover) {
  background: rgba(245, 108, 108, 0.1);
  transform: translateY(-1px);
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
