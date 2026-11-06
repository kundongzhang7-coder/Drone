import request from '@/utils/request'

// 登录，后端返回 token 与用户信息
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data,
  })
}

// 注册
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data,
  })
}

// 登出
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post',
  })
}

// 获取当前登录用户资料
export const getProfile = () => {
  return request({
    url: '/user/profile',
    method: 'get',
  })
}

// 更新资料
export const updateProfile = (data) => {
  return request({
    url: '/user/profile',
    method: 'put',
    data,
  })
}

// 上传头像
export const uploadAvatar = (formData) => {
  return request({
    url: '/user/avatar',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
