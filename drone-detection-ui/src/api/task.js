import request from '@/utils/request'

const multipartHeaders = { 'Content-Type': 'multipart/form-data' }

// 开始检测任务
export const startTask = (params) => {
  return request({ url: '/task/start', method: 'post', params })
}

// 结束任务
export const endTask = (id, data) => {
  return request({ url: `/task/end/${id}`, method: 'put', data: data || {} })
}

// 上传结果视频
export const uploadResult = (id, formData) => {
  return request({
    url: `/task/result/${id}`,
    method: 'post',
    data: formData,
    headers: multipartHeaders,
    timeout: 120000,
  })
}

// 分页查询任务
export const listTasks = (params) => {
  return request({ url: '/task/list', method: 'get', params })
}

// 统计数据
export const getStats = () => {
  return request({ url: '/task/stats', method: 'get' })
}

// 任务详情
export const getTask = (id) => {
  return request({ url: `/task/${id}`, method: 'get' })
}

// 提交离线视频检测（异步，经消息队列处理）
export const offlineDetect = (formData) => {
  return request({
    url: '/task/offline',
    method: 'post',
    data: formData,
    headers: multipartHeaders,
    timeout: 120000,
  })
}
