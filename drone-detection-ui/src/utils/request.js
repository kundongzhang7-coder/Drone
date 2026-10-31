import axios from 'axios'

//创建axios实例,发送请求
const request=axios.create({
  baseURL: '/',
  timeout: 5000
})

export default request
