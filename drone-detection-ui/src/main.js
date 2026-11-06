import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'

// 生产环境使用真实后端；Mock 仅在本地开发且显式需要时启用
if (process.env.NODE_ENV === 'development' && import.meta.env.VITE_ENABLE_MOCK === 'true') {
  import('@/mock/data')
    .then(() => console.log('Mock 服务启动成功'))
    .catch((err) => console.error('Mock 加载失败', err))
}

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
