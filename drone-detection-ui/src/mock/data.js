import Mock from 'mockjs';
// api/user.js
import {userMap} from '@/mock/userDB.js'




//使用Mock.mock()方法定义接口数据
Mock.mock('/api/users','get',{
  'name|1-10':"da",
  'password|1':"da",
  'email|1':'@email',
})

//模拟登陆数据并进行验证
Mock.mock('/api/login','post',(options)=>{
  const { userName, userPassword } = JSON.parse(options.body)
  if(!userMap.has(userName))
    return{
    data:{
      code:400,
      message:'用户名不存在'
    }
  };
  if(userMap.get(userName).password===userPassword)
  {
    return {
      data:{
        code:200,
        message:'登录成功'
      }
    };
  }
  else
    return {
    data:{
      code:400,
      message:'密码错误'
    }
    };
}
)

