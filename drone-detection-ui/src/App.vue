<template>
  <div id="app"><!--为该块内容设置名字，即id=app-->
    <!-- 路由出口：这里会显示当前页面组件（Home 或 Register） -->
    <router-view />
  </div>
</template>

<script setup>
// 根组件不需要特殊逻辑
</script>

<style>
/* 可以放一些全局样式 */
body {
  margin: 0;
  font-family: sans-serif;
}
nav {
  padding: 20px;
  text-align: center;
  background-color: #f5f5f5;
}
nav a {
  margin: 0 10px;
  color: #2c3e50;
  text-decoration: none;
}
nav a.router-link-exact-active {
  color: #42b983;
  font-weight: bold;
}
</style>
