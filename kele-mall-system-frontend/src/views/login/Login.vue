<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Right } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus' // 引入消息提示
import { doPost } from '../../http/httpRequest' // 引入封装的请求工具

const router = useRouter()
const loading = ref(false)
// 默认密码改为空，或者保留方便测试均可
const form = ref({ username: '', password: '' })

const handleLogin = () => {
  // 1. 简单校验
  if (!form.value.username || !form.value.password) {
    return ElMessage.warning('请输入账号和密码')
  }

  loading.value = true

  // 2. 发送 POST 请求
  // 根据 API 文档：B端登录接口为 /admin/sso/login
  doPost('/admin/sso/login', form.value)
    .then((resp: any) => {
      // 假设封装的 httpRequest 已经拦截了非 200 的错误，或者直接返回了 response.data
      // 这里根据通用的 Result 结构判断
      if (resp.code === 200) {
        const tokenMap = resp.data
        // 拼接 Token：Bearer + 空格 + token
        const fullToken = tokenMap.tokenHead + tokenMap.token
        
        // 3. 存储 Token
        localStorage.setItem('cola_admin_token', fullToken)
        
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        // 如果后端返回 code != 200 (例如密码错误)
        ElMessage.error(resp.message || '登录失败')
      }
    })
    .catch((err) => {
      console.error('登录异常:', err)
      // 通常 request.ts 拦截器会处理网络错误，这里可保留或省略
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<template>
  <div class="h-screen w-full bg-bg-100 flex items-center justify-center relative overflow-hidden">
    <div class="absolute top-[-10%] left-[-10%] w-[500px] h-[500px] rounded-full bg-primary-100 opacity-20 blur-[100px]"></div>
    <div class="absolute bottom-[-10%] right-[-10%] w-[600px] h-[600px] rounded-full bg-accent-100 opacity-10 blur-[120px]"></div>

    <div class="bg-bg-200 p-10 rounded-2xl border border-bg-300 w-[400px] shadow-2xl relative z-10">
      <div class="text-center mb-10">
        <h1 class="text-3xl font-bold text-white mb-2">Cola Admin 🥤</h1>
        <p class="text-text-200">可乐电商后台管理系统</p>
      </div>

      <el-form size="large">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="账号" 
            :prefix-icon="User"
            class="!bg-bg-300"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button 
          class="w-full mt-4 !bg-accent-100 !border-none hover:!bg-accent-100/80 text-white font-bold h-12 text-lg" 
          :loading="loading"
          @click="handleLogin"
        >
          登 录 <el-icon class="ml-2"><Right /></el-icon>
        </el-button>
      </el-form>

      <div class="mt-6 text-center text-text-200 text-sm">
        <span class="cursor-pointer hover:text-white">忘记密码?</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 深度修改 Input 样式适配深色 */
:deep(.el-input__wrapper) {
  background-color: #374357 !important; /* bg-300 */
  box-shadow: none !important;
  border: 1px solid transparent;
  color: white;
}
:deep(.el-input__wrapper.is-focus) {
  border: 1px solid #3D5A80;
}
:deep(.el-input__inner) {
  color: white !important;
  height: 40px;
}
</style>