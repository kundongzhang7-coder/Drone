// 引入所有模块的 mock 定义
import './data.js'
// 如果有其他模块，继续引入
// import './article'

// 可选：设置全局响应延迟，模拟真实网络环境
Mock.setup({
  timeout: '200-600'   // 随机延迟 200~600 毫秒
})
