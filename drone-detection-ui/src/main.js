import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import '@/mock/data.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'

if (process.env.NODE_ENV === 'development') {
  import('@/mock/data')
    .then(() => console.log('Mock 服务启动成功'))
    .catch(err => console.error('Mock 加载失败', err));
}

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
