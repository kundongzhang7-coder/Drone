<template>
  <p>aaa</p>
  <button @click="handleClick">点我发送请求</button>
  <p v-if="userName">用户姓名：{{ userName }}</p>
  <p v-if="userPassword">用户密码：{{ userPassword }}</p>
  <p v-if="userEmail">用户邮箱：{{ userEmail }}</p>
</template>

<script setup>
import { ref } from 'vue'
import { getuserList } from '@/api/user'   // 注意大括号

let userName = ref('aaa')   // 创建一个响应式变量，用于存储用户姓名
let userPassword=ref('123')
let userEmail=ref('123')
const handleClick = async () => {
  try {
    const res = await getuserList()
    // 假设返回的数据结构为 { data: [{ name: '张三' }, ...] }
    // 这里以获取第一个用户的姓名为例
    userName.value = res.data.name;
    userPassword.value = res.data.password;
    userEmail.value = res.data.email;
  } catch (error) {
    console.error('请求失败', error)
  }
}
</script>

<style scoped></style>
