import request from '@/utils/request'

//获取用户列表

export const getuserList=()=>{
  return request({
    url: '/api/users' ,
    method: 'get'
  })
}

export const login=(data)=>{
  return request({
    url:'/api/login',
    method: 'post',
    data
  })
}




