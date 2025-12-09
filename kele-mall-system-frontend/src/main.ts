import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css' // 这里确保引入了刚才修改的那个包含 @tailwind 的 css 文件
import App from './App.vue'
import router from './router' // 确保你之前创建了 router 文件夹

const app = createApp(App)

app.use(ElementPlus)
app.use(router) // 挂载路由

app.mount('#app')