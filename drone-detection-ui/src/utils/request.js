import axios from 'axios'
import { ElMessage } from 'element-plus'

// 统一 axios 实例：baseURL 走相对路径 /api，由 Nginx 反向代理到 Java 后端
const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// 请求拦截器：自动携带 JWT
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('drone_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一解析后端 Result 结构
request.interceptors.response.use(
  (response) => {
    // 文件流直接返回完整响应
    if (response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code === 200) {
        return res
      }
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || 'Business Error'))
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      localStorage.removeItem('drone_token')
      localStorage.removeItem('drone_user')
      ElMessage.error('登录状态已过期，请重新登录')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else {
      ElMessage.error(error.response?.data?.msg || '网络异常，请稍后再试')
    }
    return Promise.reject(error)
  },
)

export default request
